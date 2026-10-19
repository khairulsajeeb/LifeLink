import { initializeApp } from 'https://www.gstatic.com/firebasejs/10.12.2/firebase-app.js';
import { getAuth } from 'https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js';
import { getDatabase } from 'https://www.gstatic.com/firebasejs/10.12.2/firebase-database.js';

const firebaseConfig = {
  apiKey: 'AIzaSyB69bWI69iFcL0ZqhkgFpmA8QL8Dlgp0wk',
  authDomain: 'bmu-hall.firebaseapp.com',
  databaseURL: 'https://bmu-hall-default-rtdb.asia-southeast1.firebasedatabase.app',
  projectId: 'bmu-hall',
  storageBucket: 'bmu-hall.firebasestorage.app',
  messagingSenderId: '494115549490',
  appId: '1:494115549490:web:b2bdc042994efb1528672b'
};

const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
export const database = getDatabase(app);
