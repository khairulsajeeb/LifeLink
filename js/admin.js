import { auth, database } from './firebase.js';
import { downloadRequestCard } from './request-card.js';
import { onAuthStateChanged, signInWithEmailAndPassword, signOut } from 'https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js';
import { ref, onValue, push, set, update, remove } from 'https://www.gstatic.com/firebasejs/10.12.2/firebase-database.js';

const loginView = document.querySelector('#login-view');
const dashboardView = document.querySelector('#dashboard-view');
const loginForm = document.querySelector('#login-form');
const loginMessage = document.querySelector('#login-message');
const donorDialog = document.querySelector('#donor-dialog');

let donors = {};
let requests = {};
let donorApplications = {};
let blogPosts = {};
let unsubscribeDonors;
let unsubscribeRequests;
let unsubscribeApplications;
let unsubscribeBlogPosts;

const escapeHtml = (value = '') => String(value).replace(/[&<>"']/g, (char) => ({
  '&': '&amp;',
  '<': '&lt;',
  '>': '&gt;',
  '"': '&quot;',
  "'": '&#39;'
})[char]);

const prettyDate = (value) => {
  if (!value) return 'Just now';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return 'Recently';
  return new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' }).format(date);
};

onAuthStateChanged(auth, (user) => {
  loginView.hidden = Boolean(user);
  dashboardView.hidden = !user;
  if (user) startDashboard();
  else stopDashboard();
});

loginForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  loginMessage.textContent = '';
  loginMessage.className = 'form-message';
  const button = loginForm.querySelector('button[type="submit"]');
  button.disabled = true;

  try {
    await signInWithEmailAndPassword(auth, document.querySelector('#admin-email').value.trim(), document.querySelector('#admin-password').value);
    loginForm.reset();
  } catch (error) {
    console.error('Admin sign-in failed:', error);
    loginMessage.className = 'form-message error-message';
    const messages = {
      'auth/api-key-not-valid': 'Firebase rejected the Web API key. Verify the active Web API key in Firebase Project settings, then check that its website (HTTP referrer) restrictions allow this site and its API restrictions include Identity Toolkit API.',
      'auth/invalid-api-key': 'Firebase rejected the Web API key. Copy the active Web API key from Firebase Project settings → General → Your apps.',
      'auth/unauthorized-domain': 'This website domain is not authorized for Firebase Authentication. Add the current host under Firebase Authentication → Settings → Authorized domains.',
      'auth/operation-not-allowed': 'Email/password sign-in is disabled. Enable it under Firebase Authentication → Sign-in method.',
      'auth/invalid-credential': 'The email or password is incorrect, or this administrator account is not configured.'
    };
    loginMessage.textContent = messages[error.code] || `Sign-in failed (${error.code || 'unknown error'}). Check the Firebase Authentication configuration and administrator credentials.`;
  } finally {
    button.disabled = false;
  }
});

document.querySelector('#logout-button').addEventListener('click', () => signOut(auth));

document.querySelectorAll('.admin-tab').forEach((tab) => tab.addEventListener('click', () => {
  document.querySelectorAll('.admin-tab').forEach((item) => {
    const active = item === tab;
    item.classList.toggle('active', active);
    item.setAttribute('aria-selected', String(active));
  });

  document.querySelector('#donors-panel').hidden = tab.dataset.tab !== 'donors';
  document.querySelector('#applications-panel').hidden = tab.dataset.tab !== 'applications';
  document.querySelector('#requests-panel').hidden = tab.dataset.tab !== 'requests';
  document.querySelector('#blog-panel').hidden = tab.dataset.tab !== 'blog';
}));

function startDashboard() {
  if (unsubscribeDonors || unsubscribeRequests || unsubscribeApplications || unsubscribeBlogPosts) return;

  unsubscribeDonors = onValue(ref(database, 'donors'), (snapshot) => {
    donors = snapshot.val() || {};
    renderDonors();
  }, (error) => showDatabaseError('#donor-list', error));

  unsubscribeRequests = onValue(ref(database, 'requests'), (snapshot) => {
    requests = snapshot.val() || {};
    renderRequests();
  }, (error) => showDatabaseError('#request-list', error));

  unsubscribeApplications = onValue(ref(database, 'donorApplications'), (snapshot) => {
    donorApplications = snapshot.val() || {};
    renderDonorApplications();
  }, (error) => showDatabaseError('#donor-application-list', error));

  unsubscribeBlogPosts = onValue(ref(database, 'blogPosts'), (snapshot) => {
    blogPosts = snapshot.val() || {};
    renderAdminBlogPosts();
  }, (error) => showDatabaseError('#admin-blog-post-list', error));
}

function stopDashboard() {
  unsubscribeDonors?.();
  unsubscribeRequests?.();
  unsubscribeApplications?.();
  unsubscribeBlogPosts?.();
  unsubscribeDonors = undefined;
  unsubscribeRequests = undefined;
  unsubscribeApplications = undefined;
  unsubscribeBlogPosts = undefined;
}

function showDatabaseError(selector, error) {
  console.error('Could not load admin data:', error);
  document.querySelector(selector).innerHTML = '<div class="empty-state"><b>Could not load this data.</b><span>Check your Firebase Realtime Database rules and connection.</span></div>';
}

function renderDonors() {
  const list = document.querySelector('#donor-list');
  const query = document.querySelector('#donor-search').value.trim().toLowerCase();
  const blood = document.querySelector('#blood-filter').value;
  const availability = document.querySelector('#availability-filter').value;
  const entries = Object.entries(donors).filter(([, donor]) => {
    const searchText = [donor.name, donor.phone, donor.location, donor.bloodType].join(' ').toLowerCase();
    return (!query || searchText.includes(query)) && (!blood || donor.bloodType === blood) && (!availability || donor.availability === availability);
  });

  document.querySelector('#donor-count').textContent = Object.keys(donors).length;
  document.querySelector('#donor-tab-count').textContent = Object.keys(donors).length;

  if (!entries.length) {
    const noResults = Object.keys(donors).length > 0;
    list.innerHTML = `<div class="empty-state"><span class="empty-icon">♥</span><b>${noResults ? 'No donors match these filters.' : 'Your donor directory is ready.'}</b><span>${noResults ? 'Try changing your search or filters.' : 'Add a donor to start building your network.'}</span></div>`;
    return;
  }

  list.innerHTML = entries.map(([id, donor]) => {
    const initial = (donor.name || '').trim().charAt(0).toUpperCase() || 'D';
    return `<article class="donor-row"><span class="donor-avatar">${escapeHtml(initial)}</span><div class="donor-person"><b>${escapeHtml(donor.name || 'Unnamed donor')}</b><span>${escapeHtml(donor.phone || 'No phone')}</span></div><span class="blood-badge">${escapeHtml(donor.bloodType || 'Unknown')}</span><span class="donor-location">${escapeHtml(donor.location || '—')}</span><span class="availability-badge ${donor.availability === 'Unavailable' ? 'is-unavailable' : ''}"><i></i>${escapeHtml(donor.availability || 'Available')}</span><div class="row-actions"><button class="icon-button edit-donor" data-id="${escapeHtml(id)}" aria-label="Edit ${escapeHtml(donor.name || 'donor')}" title="Edit donor">✎</button><button class="icon-button delete-donor" data-id="${escapeHtml(id)}" aria-label="Remove ${escapeHtml(donor.name || 'donor')}" title="Remove donor">×</button></div></article>`;
  }).join('');

  list.querySelectorAll('.edit-donor').forEach((button) => button.addEventListener('click', () => openDonorDialog(button.dataset.id)));
  list.querySelectorAll('.delete-donor').forEach((button) => button.addEventListener('click', async () => {
    const donor = donors[button.dataset.id];
    if (donor && window.confirm(`Remove ${donor.name} from the donor directory?`)) {
      await remove(ref(database, `donors/${button.dataset.id}`));
    }
  }));
}

function renderRequests() {
  const list = document.querySelector('#request-list');
  const query = document.querySelector('#request-search').value.trim().toLowerCase();
  const status = document.querySelector('#request-status-filter').value;
  const entries = Object.entries(requests)
    .sort(([, a], [, b]) => (b.createdAt || 0) - (a.createdAt || 0))
    .filter(([, request]) => {
      const searchText = [request.studentId, request.name, request.phone, request.bloodType, request.hospital].join(' ').toLowerCase();
      return (!query || searchText.includes(query)) && (!status || request.status === status);
    });

  const openCount = Object.values(requests).filter((request) => !['Fulfilled', 'Closed'].includes(request.status)).length;
  document.querySelector('#request-count').textContent = openCount;
  document.querySelector('#request-tab-count').textContent = Object.keys(requests).length;

  if (!entries.length) {
    const hasRequests = Object.keys(requests).length > 0;
    list.innerHTML = `<div class="empty-state"><span class="empty-icon">↗</span><b>${hasRequests ? 'No requests match these filters.' : 'No requests yet.'}</b><span>${hasRequests ? 'Try another search or status.' : 'New student requests will appear here.'}</span></div>`;
    return;
  }

  list.innerHTML = entries.map(([id, request]) => {
    const detailMarkup = request.details ? `<p class="request-details">${escapeHtml(request.details)}</p>` : '';
    const statusOptions = ['New', 'Contacted', 'Fulfilled', 'Closed']
      .map((option) => `<option ${request.status === option ? 'selected' : ''}>${option}</option>`)
      .join('');

    return `<article class="request-card"><div class="request-card-top"><div><span class="blood-badge">${escapeHtml(request.bloodType || 'Unknown')}</span><span class="request-status status-${escapeHtml((request.status || 'New').toLowerCase())}">${escapeHtml(request.status || 'New')}</span></div><span class="request-date">${escapeHtml(prettyDate(request.createdAt))}</span></div><h3>${escapeHtml(request.name || 'Student request')} <span>· ID ${escapeHtml(request.studentId || '—')}</span></h3><div class="request-meta"><span>☎ ${escapeHtml(request.phone || '—')}</span><span>⌖ ${escapeHtml(request.hospital || '—')}</span><span>◷ ${escapeHtml(request.urgency || '—')}</span><span>Units: ${escapeHtml(request.units || '1')}</span></div>${detailMarkup}<div class="request-actions"><a class="text-link" href="tel:${escapeHtml(String(request.phone || '').replace(/[^\d+]/g, ''))}">Call student ↗</a><button class="button button-outline request-share-button" data-id="${escapeHtml(id)}" type="button">↓ Download share card</button><label class="status-control">Update status <select class="request-status-select" data-id="${escapeHtml(id)}">${statusOptions}</select></label><button class="icon-button delete-request" data-id="${escapeHtml(id)}" title="Delete request" aria-label="Delete request">×</button></div></article>`;
  }).join('');

  list.querySelectorAll('.request-share-button').forEach((button) => button.addEventListener('click', async () => {
    const request = requests[button.dataset.id];
    if (!request) return;

    const originalLabel = button.textContent;
    button.disabled = true;
    button.textContent = 'Preparing…';

    try {
      await downloadRequestCard(request);
    } catch (error) {
      console.error('Could not download admin request card:', error);
      window.alert('Could not create the request card. Please try again.');
    } finally {
      button.disabled = false;
      button.textContent = originalLabel;
    }
  }));

  list.querySelectorAll('.request-status-select').forEach((select) => select.addEventListener('change', () => {
    update(ref(database, `requests/${select.dataset.id}`), { status: select.value });
  }));

  list.querySelectorAll('.delete-request').forEach((button) => button.addEventListener('click', async () => {
    if (window.confirm('Permanently delete this request?')) {
      await remove(ref(database, `requests/${button.dataset.id}`));
    }
  }));
}

function renderDonorApplications() {
  const list = document.querySelector('#donor-application-list');
  const entries = Object.entries(donorApplications).sort(([, a], [, b]) => {
    if (a.status === 'Pending' && b.status !== 'Pending') return -1;
    if (b.status === 'Pending' && a.status !== 'Pending') return 1;
    return (b.createdAt || 0) - (a.createdAt || 0);
  });

  const pendingCount = Object.values(donorApplications).filter((application) => application.status === 'Pending').length;
  document.querySelector('#application-tab-count').textContent = pendingCount;

  if (!entries.length) {
    list.innerHTML = '<div class="empty-state"><span class="empty-icon">♥</span><b>No donor applications yet.</b><span>New applications will appear here for review.</span></div>';
    return;
  }

  list.innerHTML = entries.map(([id, application]) => {
    const status = application.status || 'Pending';
    const pending = status === 'Pending';
    let actionsMarkup = '';

    if (pending) {
      actionsMarkup = `<div class="application-actions"><a class="text-link" href="tel:${escapeHtml(String(application.phone || '').replace(/[^\d+]/g, ''))}">Call applicant ↗</a><div><button class="button button-outline decline-application" data-id="${escapeHtml(id)}" type="button">Decline</button><button class="button button-primary approve-application" data-id="${escapeHtml(id)}" type="button">Approve donor <span>→</span></button></div></div>`;
    } else if (status === 'Approved') {
      actionsMarkup = '<p class="application-outcome">Added to the donor directory.</p>';
    } else {
      actionsMarkup = '<p class="application-outcome">Application declined.</p>';
    }

    return `<article class="donor-application-card"><div class="application-card-top"><div><span class="blood-badge">${escapeHtml(application.bloodType || 'Unknown')}</span><span class="request-status status-${escapeHtml(status.toLowerCase())}">${escapeHtml(status)}</span></div><span class="request-date">${escapeHtml(prettyDate(application.createdAt))}</span></div><h3>${escapeHtml(application.name || 'Donor applicant')} <span>· ID ${escapeHtml(application.studentId || '—')}</span></h3><div class="request-meta"><span>☎ ${escapeHtml(application.phone || '—')}</span><span>⌖ ${escapeHtml(application.location || '—')}</span></div>${actionsMarkup}</article>`;
  }).join('');

  list.querySelectorAll('.approve-application').forEach((button) => button.addEventListener('click', () => reviewDonorApplication(button.dataset.id, true, button)));
  list.querySelectorAll('.decline-application').forEach((button) => button.addEventListener('click', () => reviewDonorApplication(button.dataset.id, false, button)));
}

async function reviewDonorApplication(id, approve, button) {
  const application = donorApplications[id];
  if (!application || application.status !== 'Pending') return;
  if (!approve && !window.confirm(`Decline ${application.name}'s donor application?`)) return;

  button.disabled = true;
  try {
    const reviewedAt = Date.now();
    if (approve) {
      const donorRef = push(ref(database, 'donors'));
      await update(ref(database), {
        [`donors/${donorRef.key}`]: {
          name: application.name,
          bloodType: application.bloodType,
          phone: application.phone,
          location: application.location,
          availability: 'Available',
          createdAt: reviewedAt,
          updatedAt: reviewedAt,
          applicationId: id
        },
        [`donorApplications/${id}/status`]: 'Approved',
        [`donorApplications/${id}/reviewedAt`]: reviewedAt,
        [`donorApplications/${id}/donorId`]: donorRef.key
      });
    } else {
      await update(ref(database, `donorApplications/${id}`), { status: 'Declined', reviewedAt });
    }
  } catch (error) {
    console.error('Could not review donor application:', error);
    window.alert('Could not update this application. Check the database rules and try again.');
    button.disabled = false;
  }
}

function renderAdminBlogPosts() {
  const list = document.querySelector('#admin-blog-post-list');
  const entries = Object.entries(blogPosts).sort(([, a], [, b]) => (b.createdAt || 0) - (a.createdAt || 0));
  document.querySelector('#blog-post-count').textContent = entries.length;

  if (!entries.length) {
    list.innerHTML = '<div class="empty-state"><b>No published posts yet.</b><span>Your published stories will appear here.</span></div>';
    return;
  }

  list.innerHTML = entries.map(([id, post]) => `<article class="admin-blog-post"><img src="${escapeHtml(post.coverUrl || '')}" alt=""><div><b>${escapeHtml(post.title || 'Untitled post')}</b><small>${escapeHtml(prettyDate(post.createdAt))}</small></div><button class="icon-button delete-blog-post" data-id="${escapeHtml(id)}" type="button" title="Delete blog post" aria-label="Delete ${escapeHtml(post.title || 'blog post')}">×</button></article>`).join('');

  list.querySelectorAll('.delete-blog-post').forEach((button) => button.addEventListener('click', async () => {
    const post = blogPosts[button.dataset.id];
    if (!post || !window.confirm(`Delete '${post.title}' from the blog?`)) return;

    button.disabled = true;
    try {
      await remove(ref(database, `blogPosts/${button.dataset.id}`));
    } catch (error) {
      console.error('Could not delete blog post:', error);
      window.alert('Could not delete this blog post. Check the Realtime Database rules and try again.');
      button.disabled = false;
    }
  }));
}

const blogPostForm = document.querySelector('#blog-post-form');
const blogCoverInput = document.querySelector('#blog-post-cover');
const blogCoverPreview = document.querySelector('#blog-cover-preview');
const blogPostMessage = document.querySelector('#blog-post-message');

blogCoverInput.addEventListener('input', () => {
  const coverUrl = blogCoverInput.value.trim();
  if (!coverUrl) {
    blogCoverPreview.hidden = true;
    blogCoverPreview.removeAttribute('src');
    return;
  }

  try {
    const parsedUrl = new URL(coverUrl);
    if (parsedUrl.protocol !== 'https:') throw new Error('Cover image URLs must use HTTPS.');
    blogCoverPreview.src = parsedUrl.href;
    blogCoverPreview.hidden = false;
    blogPostMessage.textContent = '';
    blogPostMessage.className = 'form-message';
  } catch {
    blogCoverPreview.hidden = true;
    blogCoverPreview.removeAttribute('src');
    blogPostMessage.className = 'form-message error-message';
    blogPostMessage.textContent = 'Enter a valid HTTPS image URL.';
  }
});

blogCoverPreview.addEventListener('error', () => {
  blogCoverPreview.hidden = true;
  blogPostMessage.className = 'form-message error-message';
  blogPostMessage.textContent = 'The cover image could not be loaded. Check that the URL points to a public image.';
});

blogPostForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  if (!blogPostForm.reportValidity()) return;

  const title = document.querySelector('#blog-post-title').value.trim();
  const body = document.querySelector('#blog-post-body').value.trim();
  let coverUrl;

  try {
    coverUrl = new URL(blogCoverInput.value.trim());
  } catch {
    blogPostMessage.className = 'form-message error-message';
    blogPostMessage.textContent = 'Enter a valid HTTPS image URL.';
    return;
  }

  if (coverUrl.protocol !== 'https:' || !title || !body) {
    blogPostMessage.className = 'form-message error-message';
    blogPostMessage.textContent = coverUrl.protocol !== 'https:' ? 'Cover images must use HTTPS.' : 'Enter a title and text body for your post.';
    return;
  }

  if (!blogCoverPreview.complete || blogCoverPreview.naturalWidth === 0) {
    blogPostMessage.className = 'form-message error-message';
    blogPostMessage.textContent = 'Wait for the cover preview to load, and confirm the URL points to a public image.';
    return;
  }

  const button = blogPostForm.querySelector('[type="submit"]');
  button.disabled = true;
  button.textContent = 'Publishing…';
  blogPostMessage.textContent = '';
  blogPostMessage.className = 'form-message';

  const postRef = push(ref(database, 'blogPosts'));
  try {
    const now = Date.now();
    await set(postRef, {
      title,
      body,
      coverUrl: coverUrl.href,
      createdAt: now,
      updatedAt: now
    });
    blogPostForm.reset();
    blogCoverPreview.hidden = true;
    blogCoverPreview.removeAttribute('src');
    blogPostMessage.className = 'form-message success-message';
    blogPostMessage.textContent = 'Your post is published and now appears on the blog and homepage.';
  } catch (error) {
    console.error('Could not publish blog post:', error);
    blogPostMessage.className = 'form-message error-message';
    blogPostMessage.textContent = 'Could not publish this post. Check the Realtime Database rules and try again.';
  } finally {
    button.disabled = false;
    button.innerHTML = 'Publish blog post <span>♥</span>';
  }
});

['#donor-search', '#blood-filter', '#availability-filter'].forEach((selector) => document.querySelector(selector).addEventListener('input', renderDonors));
['#request-search', '#request-status-filter'].forEach((selector) => document.querySelector(selector).addEventListener('input', renderRequests));

document.querySelector('#add-donor-button').addEventListener('click', () => openDonorDialog());
document.querySelector('#close-donor-dialog').addEventListener('click', () => donorDialog.close());
document.querySelector('#cancel-donor').addEventListener('click', () => donorDialog.close());

document.querySelector('#donor-form').addEventListener('submit', async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const key = document.querySelector('#donor-key').value;
  const button = form.querySelector('[type="submit"]');
  const message = document.querySelector('#donor-message');
  button.disabled = true;
  message.textContent = '';

  const donor = {
    name: document.querySelector('#donor-name').value.trim(),
    bloodType: document.querySelector('#donor-blood').value,
    phone: document.querySelector('#donor-phone').value.trim(),
    location: document.querySelector('#donor-location').value.trim(),
    availability: document.querySelector('#donor-availability').value,
    updatedAt: Date.now()
  };

  try {
    if (key) await update(ref(database, `donors/${key}`), donor);
    else await set(push(ref(database, 'donors')), { ...donor, createdAt: Date.now() });
    donorDialog.close();
  } catch (error) {
    console.error('Could not save donor:', error);
    message.className = 'form-message error-message';
    message.textContent = 'Could not save donor. Check your Firebase database rules and try again.';
  } finally {
    button.disabled = false;
  }
});

function openDonorDialog(key = '') {
  const donor = key ? donors[key] || {} : {};
  document.querySelector('#donor-form').reset();
  document.querySelector('#donor-key').value = key;
  document.querySelector('#donor-dialog-title').textContent = key ? 'Edit donor' : 'Add a donor';
  document.querySelector('#donor-name').value = donor.name || '';
  document.querySelector('#donor-blood').value = donor.bloodType || '';
  document.querySelector('#donor-phone').value = donor.phone || '';
  document.querySelector('#donor-location').value = donor.location || '';
  document.querySelector('#donor-availability').value = donor.availability || 'Available';
  document.querySelector('#donor-message').textContent = '';
  document.querySelector('#donor-message').className = 'form-message';
  donorDialog.showModal();
}
