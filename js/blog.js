import { database } from './firebase.js';
import { ref, onValue } from 'https://www.gstatic.com/firebasejs/10.12.2/firebase-database.js';

const starterPosts = [
  {
    id: 'small-acts-campus',
    category: 'Community',
    date: 'October 16, 2026',
    readTime: '3 min read',
    title: 'A small act can change the shape of someone\'s day',
    excerpt: 'A campus donor network is more than a list of names. It is a way to make help feel closer when a family needs it most.',
    image: 'https://images.unsplash.com/photo-1532629345422-7515f3d16bb6?auto=format&fit=crop&w=1500&q=85',
    imageAlt: 'Hands joined together in a gesture of community support',
    paragraphs: [
      'When someone asks for blood, the first feeling is often urgency. The next can be uncertainty: who should we call, and where do we begin? A local network gives that moment a starting point.',
      'LifeLink brings students and volunteers into one coordinated community. Administrators review requests privately and contact registered donors directly, so personal details are not posted for everyone to see.',
      'Offering to donate is a personal decision, and every donation depends on eligibility and professional guidance. But letting the network know you are willing to be contacted can help a coordinator find the right next step.'
    ]
  },
  {
    id: 'before-donation',
    category: 'Practical guide',
    date: 'October 11, 2026',
    readTime: '4 min read',
    title: 'Getting ready to donate: start with the people who know best',
    excerpt: 'A calm, useful checklist begins with your local blood service. Here is what to ask before making a donation appointment.',
    image: 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=1200&q=85',
    imageAlt: 'Healthcare professional speaking with a patient',
    paragraphs: [
      'Donation guidance can vary by location and by person. Before you go, check with the blood collection service or a healthcare professional about eligibility, identification, appointment details, and anything you should do beforehand.',
      'Plan enough time for the visit and follow the instructions the service gives you. If you feel unwell or have a question about your health, contact them before your appointment rather than relying on a general checklist.',
      'After donation, use the collection team\'s aftercare guidance. Their advice is specific to the donation process and should take priority over online tips.'
    ]
  },
  {
    id: 'asking-for-help',
    category: 'Student life',
    date: 'October 6, 2026',
    readTime: '3 min read',
    title: 'When a friend needs help, clear information matters',
    excerpt: 'A few accurate details help coordinators understand a request and follow up without putting private information on public display.',
    image: 'https://images.unsplash.com/photo-1529156069898-49953e39b3ac?auto=format&fit=crop&w=1200&q=85',
    imageAlt: 'Friends talking together outdoors',
    paragraphs: [
      'In a stressful moment, it can be hard to know what to share. Start with the information a coordinator needs to understand the request: the blood group if known, where support is needed, when it is needed, and a reliable contact number.',
      'Use a private request channel instead of posting someone else\'s contact details in a public group. Ask the person or their family before sharing personal information, and make sure the request is genuine and current.',
      'A community network can help people connect, but it is not an emergency service. For urgent medical needs, contact the hospital or local emergency services directly.'
    ]
  },
  {
    id: 'donor-privacy',
    category: 'How it works',
    date: 'October 2, 2026',
    readTime: '3 min read',
    title: 'Why donor details belong in trusted hands',
    excerpt: 'Privacy is part of making a community directory useful: contact details should reach the people coordinating a request, not the whole internet.',
    image: 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=1200&q=85',
    imageAlt: 'Community volunteer smiling during a conversation',
    paragraphs: [
      'A donor directory works best when people can trust how it is handled. Contact information can be sensitive, so LifeLink keeps donor records available to authorized administrators rather than displaying them publicly.',
      'Each donor application is reviewed before it becomes part of the directory. The administrator can confirm the details and decide whether the application is ready to approve.',
      'If your phone number or availability changes, let the administrator know so the directory stays accurate and coordinators can respect your preferences.'
    ]
  }
];

let posts = [...starterPosts];

const escapeHtml = (value) => String(value).replace(/[&<>"']/g, (character) => ({
  '&': '&amp;',
  '<': '&lt;',
  '>': '&gt;',
  '"': '&quot;',
  "'": '&#39;'
})[character]);

const postUrl = (post) => `./?post=${encodeURIComponent(post.id)}`;

function renderMegaPosts() {
  document.querySelectorAll('[data-blog-mega-posts]').forEach((container) => {
    container.innerHTML = posts.slice(0, 3).map((post) => `
      <a class="mega-post" href="${postUrl(post)}">
        <img src="${escapeHtml(post.image)}" alt="" loading="lazy">
        <span>
          <small>${escapeHtml(post.category)}</small>
          <b>${escapeHtml(post.title)}</b>
          <i>${escapeHtml(post.date)}</i>
        </span>
      </a>
    `).join('');
  });
}

function renderHomeSlider() {
  const slider = document.querySelector('[data-blog-slider]');
  if (!slider) return;

  slider.innerHTML = posts.slice(0, 4).map((post) => `
    <article class="blog-slide">
      <a class="blog-slide-image" href="${postUrl(post)}">
        <img src="${escapeHtml(post.image)}" alt="${escapeHtml(post.imageAlt)}" loading="lazy">
        <span>${escapeHtml(post.category)}</span>
      </a>
      <div class="blog-slide-copy">
        <small>${escapeHtml(post.date)} <i>·</i> ${escapeHtml(post.readTime)}</small>
        <h3><a href="${postUrl(post)}">${escapeHtml(post.title)}</a></h3>
        <p>${escapeHtml(post.excerpt)}</p>
        <a class="text-link" href="${postUrl(post)}">Read story <span>↗</span></a>
      </div>
    </article>
  `).join('');

  document.querySelectorAll('[data-blog-scroll]').forEach((button) => {
    button.addEventListener('click', () => {
      slider.scrollBy({
        left: Number(button.dataset.blogScroll) * slider.clientWidth * 0.78,
        behavior: 'smooth'
      });
    });
  });
}

function renderArchive() {
  const featured = document.querySelector('#blog-featured');
  const grid = document.querySelector('#blog-grid');
  if (!featured || !grid || !posts.length) return;

  const latest = posts[0];
  featured.innerHTML = `
    <article class="featured-story">
      <a class="featured-story-image" href="${postUrl(latest)}">
        <img src="${escapeHtml(latest.image)}" alt="${escapeHtml(latest.imageAlt)}">
      </a>
      <div class="featured-story-copy">
        <span class="section-kicker">FEATURED STORY · ${escapeHtml(latest.category.toUpperCase())}</span>
        <h2><a href="${postUrl(latest)}">${escapeHtml(latest.title)}</a></h2>
        <p>${escapeHtml(latest.excerpt)}</p>
        <div class="blog-byline">
          <span>${escapeHtml(latest.date)}</span>
          <span>${escapeHtml(latest.readTime)}</span>
        </div>
        <a class="button button-primary" href="${postUrl(latest)}">Read the story <span>→</span></a>
      </div>
    </article>
  `;

  grid.innerHTML = posts.slice(1).map((post) => `
    <article class="blog-archive-card">
      <a class="archive-image" href="${postUrl(post)}">
        <img src="${escapeHtml(post.image)}" alt="${escapeHtml(post.imageAlt)}" loading="lazy">
      </a>
      <div class="archive-card-copy">
        <div class="blog-byline">
          <span>${escapeHtml(post.category)}</span>
          <span>${escapeHtml(post.date)}</span>
        </div>
        <h3><a href="${postUrl(post)}">${escapeHtml(post.title)}</a></h3>
        <p>${escapeHtml(post.excerpt)}</p>
        <a class="text-link" href="${postUrl(post)}">Read story <span>↗</span></a>
      </div>
    </article>
  `).join('');
}

function renderArticle() {
  const archive = document.querySelector('#blog-archive');
  const articleView = document.querySelector('#blog-article-view');
  if (!archive || !articleView) return;

  const articleId = new URLSearchParams(window.location.search).get('post');
  const post = posts.find((item) => item.id === articleId);
  if (!post) return;

  archive.hidden = true;
  articleView.hidden = false;
  document.title = `${post.title} — LifeLink`;

  const description = post.excerpt || post.paragraphs[0] || `Read ${post.title} from the LifeLink BMU Students Platform community.`;
  document.querySelector('meta[name="description"]')?.setAttribute('content', description);
  document.querySelector('meta[property="og:type"]')?.setAttribute('content', 'article');
  document.querySelector('meta[property="og:title"]')?.setAttribute('content', `${post.title} — LifeLink`);
  document.querySelector('meta[property="og:description"]')?.setAttribute('content', description);
  document.querySelector('meta[property="og:image"]')?.setAttribute('content', post.image);
  document.querySelector('meta[name="twitter:card"]')?.setAttribute('content', 'summary_large_image');
  document.querySelector('meta[name="twitter:title"]')?.setAttribute('content', `${post.title} — LifeLink`);
  document.querySelector('meta[name="twitter:description"]')?.setAttribute('content', description);
  document.querySelector('meta[name="twitter:image"]')?.setAttribute('content', post.image);

  articleView.innerHTML = `
    <a class="text-link article-back-link" href="./">← All stories</a>
    <header class="article-heading">
      <span class="section-kicker">${escapeHtml(post.category.toUpperCase())}</span>
      <h1>${escapeHtml(post.title)}</h1>
      <div class="blog-byline">
        <span>${escapeHtml(post.date)}</span>
        <span>${escapeHtml(post.readTime)}</span>
      </div>
    </header>
    <img class="article-cover" src="${escapeHtml(post.image)}" alt="${escapeHtml(post.imageAlt)}">
    <div class="article-body">
      ${post.paragraphs.map((paragraph) => `<p>${escapeHtml(paragraph)}</p>`).join('')}
      <div class="article-endnote">
        <span class="section-kicker">LIFELINK · BMU STUDENTS PLATFORM</span>
        <a class="text-link" href="../donor/">Offer to donate <span>↗</span></a>
      </div>
    </div>
  `;
}

function renderBlog() {
  renderMegaPosts();
  renderHomeSlider();
  renderArchive();
  renderArticle();
}

function loadPublishedPosts() {
  onValue(ref(database, 'blogPosts'), (snapshot) => {
    const published = Object.entries(snapshot.val() || {}).map(([id, post]) => {
      const date = Number(post.createdAt) || 0;
      const paragraphs = String(post.body || '').split(/\n\s*\n/).map((paragraph) => paragraph.trim()).filter(Boolean);
      const wordCount = String(post.body || '').trim().split(/\s+/).filter(Boolean).length;
      const excerpt = paragraphs[0] || '';

      return {
        id,
        category: 'Community',
        date: date ? new Intl.DateTimeFormat(undefined, { dateStyle: 'long' }).format(date) : 'Recently',
        publishedAt: date,
        readTime: `${Math.max(1, Math.ceil(wordCount / 200))} min read`,
        title: post.title || 'Untitled post',
        excerpt: excerpt.length > 180 ? `${excerpt.slice(0, 177)}…` : excerpt,
        image: post.coverUrl || '',
        imageAlt: post.title || 'LifeLink community story',
        paragraphs
      };
    });

    posts = [...published, ...starterPosts].sort((first, second) => {
      const firstDate = first.publishedAt || Date.parse(first.date) || 0;
      const secondDate = second.publishedAt || Date.parse(second.date) || 0;
      return secondDate - firstDate;
    });

    renderBlog();
  }, (error) => {
    console.error('Could not load published blog posts:', error);
  });
}

renderBlog();
loadPublishedPosts();
