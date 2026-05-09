// ─── Firebase Configuration ───────────────────────────────────────────────
// Replace the values below with your actual Firebase project config.
// You can find these in the Firebase Console → Project Settings → Your Apps.

const firebaseConfig = {
  apiKey:            "YOUR_API_KEY",
  authDomain:        "YOUR_PROJECT_ID.firebaseapp.com",
  projectId:         "YOUR_PROJECT_ID",
  storageBucket:     "YOUR_PROJECT_ID.appspot.com",
  messagingSenderId: "YOUR_MESSAGING_SENDER_ID",
  appId:             "YOUR_APP_ID"
};

// ─── Backend API Base URL ─────────────────────────────────────────────────
// Change this to your deployed Spring Boot backend URL.
const API_BASE_URL = "http://localhost:8080";

// ─── Initialize Firebase ──────────────────────────────────────────────────
import { initializeApp } from "https://www.gstatic.com/firebasejs/10.12.0/firebase-app.js";
import { getAuth }       from "https://www.gstatic.com/firebasejs/10.12.0/firebase-auth.js";

const app  = initializeApp(firebaseConfig);
const auth = getAuth(app);

export { app, auth, API_BASE_URL };
