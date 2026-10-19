import { database } from './firebase.js';
import { ref, push, set, serverTimestamp } from 'https://www.gstatic.com/firebasejs/10.12.2/firebase-database.js';
import { downloadRequestCard } from './request-card.js';

const form = document.querySelector('#request-form');
const studentIdInput = document.querySelector('#student-id');
const idError = document.querySelector('#student-id-error');
const message = document.querySelector('#request-message');
const submitButton = form.querySelector('[type="submit"]');
const cardActions = document.querySelector('#request-card-actions');
const downloadCardButton = document.querySelector('#download-request-card');
let shareCardDetails;

function isValidStudentId(value) {
  if (!/^\d{8}$/.test(value)) return false;
  const year = 2000 + Number(value.slice(0, 2));
  return year >= 2019 && year <= new Date().getFullYear();
}

studentIdInput.addEventListener('input', () => {
  studentIdInput.value = studentIdInput.value.replace(/\D/g, '').slice(0, 8);
  idError.textContent = '';
  studentIdInput.removeAttribute('aria-invalid');
});

form.addEventListener('submit', async (event) => {
  event.preventDefault();
  message.textContent = '';
  message.className = 'form-message';

  const studentId = studentIdInput.value.trim();
  if (!isValidStudentId(studentId)) {
    studentIdInput.setAttribute('aria-invalid', 'true');
    idError.textContent = `Enter an 8-digit ID with a year from 2019 to ${new Date().getFullYear()} in its first two digits.`;
    studentIdInput.focus();
    return;
  }
  if (!form.reportValidity()) return;

  submitButton.disabled = true;
  submitButton.innerHTML = 'Sending securely…';
  const fields = new FormData(form);
  const request = {
    studentId,
    name: fields.get('name').trim(),
    phone: fields.get('phone').trim(),
    bloodType: fields.get('bloodType'),
    units: fields.get('units'),
    hospital: fields.get('hospital').trim(),
    urgency: fields.get('urgency'),
    details: fields.get('details').trim(),
    status: 'New',
    createdAt: serverTimestamp()
  };
  const includeContact = document.querySelector('#share-contact').checked;

  try {
    const requestRef = push(ref(database, 'requests'));
    await set(requestRef, request);
    shareCardDetails = {
      bloodType: request.bloodType,
      units: request.units,
      hospital: request.hospital,
      urgency: request.urgency,
      name: includeContact ' request.name : '',
      phone: includeContact ' request.phone : ''
    };
    form.reset();
    message.className = 'form-message success-message';
    message.textContent = 'Your request has been sent privately. A LifeLink administrator will follow up using the contact number you provided.';
    cardActions.querySelector('small').textContent = includeContact
      ' 'This card includes your name and phone number because you opted in. Your student ID is never included.'
      : 'The card includes your request details only. Your name, phone number, and student ID are not included.';
    cardActions.hidden = false;
    cardActions.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  } catch (error) {
    console.error('Could not submit blood request:', error);
    message.className = 'form-message error-message';
    message.textContent = 'We could not send your request. Please check your connection and try again.';
  } finally {
    submitButton.disabled = false;
    submitButton.innerHTML = 'Send private request <span>→</span>';
  }
});

downloadCardButton.addEventListener('click', async () => {
  if (!shareCardDetails) return;
  downloadCardButton.disabled = true;
  const originalLabel = downloadCardButton.innerHTML;
  downloadCardButton.textContent = 'Preparing photo card…';

  try {
    await downloadRequestCard(shareCardDetails, { includeContact: Boolean(shareCardDetails.phone) });
  } catch (error) {
    console.error('Could not download request photo card:', error);
    message.className = 'form-message error-message';
    message.textContent = 'Could not create the photo card. Please try downloading again.';
  } finally {
    downloadCardButton.disabled = false;
    downloadCardButton.innerHTML = originalLabel;
  }
});
