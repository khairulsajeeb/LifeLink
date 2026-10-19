# LifeLink — BMU Hall Blood Network

A multipage, plain-JavaScript blood donor coordination website backed by Firebase Authentication and Realtime Database. Donor records are never rendered on public pages. Student ID entry checks the 8-digit format and a 2019-to-current-year year prefix; this is a format check, not verification against an official university registry.

## Set up Firebase

1. In the Firebase console for the supplied project, enable **Authentication → Sign-in method → Email/Password** and create the administrator account.
2. Find that account's Firebase **UID** in Authentication → Users.
3. Replace every `REPLACE_WITH_ADMIN_UID` in `database.rules.json` with the administrator's actual UID. Do not use an email address here.
4. Publish `database.rules.json` under **Realtime Database → Rules**. Until the UID is configured, admin reads and donor changes are intentionally denied.
5. In Firebase Authentication → Settings → Authorized domains, add the domain where the site will run.
6. Verify that the Firebase web app configuration in `js/firebase.js` matches Project settings → Your apps.

The public request page writes new requests only to `/requests`, and the donor application page creates new `Pending` entries under `/donorApplications`. Realtime Database rules prevent public listing/reading and changes to existing entries. Only the configured admin UID can read requests, review donor applications, or manage donors. Approving a donor application adds the applicant to `/donors` and marks the application approved; declining it does not create a donor record. The rules' student-year prefix currently covers 2019–2026; update the upper bound in the Realtime Database rules as calendar years advance. Client validation uses the browser's current year.

## Run locally

Serve this folder from a local web server (ES modules and Firebase require HTTP/HTTPS; opening `index.html` as a `file://` URL will not work). For example, use VS Code Live Server or another static server. Then open `index.html` on the served origin.

## Pages

- `index.html` — public landing page
- `about.html` — process and privacy information
- `request.html` — student-ID check and private request submission
- `donor.html` — student-ID check and private donor application submission
- `blog.html` — community stories and individual blog articles
- `admin.html` — Firebase-authenticated donor and request dashboard

Starter blog stories are maintained in `js/blog.js`; published administrator posts are loaded from Firebase and feed the homepage story slider, blog archive, article views, and latest-stories navigation menu.

Administrators can publish blog posts from the Blog posts tab in `admin.html`. Post titles, bodies, and publicly accessible HTTPS cover image URLs are stored in `/blogPosts`; image files must be hosted separately. Existing sample stories in `js/blog.js` remain visible alongside administrator posts.

Admin features include donor add/edit/remove, name/phone/location search, blood-group and availability filters, donor application approval/decline, blog post publishing/deletion, request search, status updates, and request deletion. The donor directory, donor applications, blog publishing, and request details require the configured Firebase admin UID.
