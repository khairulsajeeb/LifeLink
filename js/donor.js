import { database } from './firebase.js';
import { ref, push, set, serverTimestamp } from 'https://www.gstatic.com/firebasejs/10.12.2/firebase-database.js';

const form = document.querySelector('#donor-application-form');
const studentIdInput = document.querySelector('#student-id');
const idError = document.querySelector('#student-id-error');
const message = document.querySelector('#donor-application-message');
const submitButton = form.querySelector('[type="submit"]');

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
  submitButton.textContent = 'Submitting securely…';
  const fields = new FormData(form);
  const application = {
    studentId,
    name: fields.get('name').trim(),
    phone: fields.get('phone').trim(),
    bloodType: fields.get('bloodType'),
    location: fields.get('location').trim(),
    status: 'Pending',
    createdAt: serverTimestamp()
  };

  try {
    await set(push(ref(database, 'donorApplications')), application);
    form.reset();
    message.className = 'form-message success-message';
    message.textContent = 'Your donor application has been submitted. The administrator will review it before you are added to the donor directory.';
  } catch (error) {
    console.error('Could not submit donor application:', error);
    message.className = 'form-message error-message';
    message.textContent = 'We could not send your application. Please check your connection and try again.';
  } finally {
    submitButton.disabled = false;
    submitButton.innerHTML = 'Submit for review <span>→</span>';
  }
});