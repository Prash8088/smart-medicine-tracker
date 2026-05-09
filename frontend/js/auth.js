// ─── Shared Auth Utilities ────────────────────────────────────────────────
import { auth, API_BASE_URL } from "./firebase-config.js";
import { onAuthStateChanged, signOut } from "https://www.gstatic.com/firebasejs/10.12.0/firebase-auth.js";

/**
 * Returns a Promise that resolves with the current Firebase user or null.
 * Redirects to index.html if no user is logged in and requireAuth is true.
 */
export function requireAuth(requireAuth = true) {
  return new Promise((resolve) => {
    onAuthStateChanged(auth, (user) => {
      if (!user && requireAuth) {
        window.location.href = "index.html";
      } else {
        resolve(user);
      }
    });
  });
}

/**
 * Gets the current user's Firebase ID token.
 */
export async function getToken() {
  const user = auth.currentUser;
  if (!user) throw new Error("Not authenticated");
  return user.getIdToken();
}

/**
 * Makes an authenticated API call to the backend.
 */
export async function apiCall(path, method = "GET", body = null) {
  const token = await getToken();
  const opts = {
    method,
    headers: {
      "Authorization": `Bearer ${token}`,
      "Content-Type":  "application/json"
    }
  };
  if (body) opts.body = JSON.stringify(body);
  const res = await fetch(`${API_BASE_URL}${path}`, opts);
  if (!res.ok) {
    const err = await res.json().catch(() => ({ error: res.statusText }));
    throw new Error(err.error || "Request failed");
  }
  return res.json();
}

/**
 * Signs the current user out and redirects to index.html.
 */
export async function logout() {
  await signOut(auth);
  window.location.href = "index.html";
}

/**
 * Checks whether the current user has the admin custom claim.
 */
export async function isAdmin() {
  const user = auth.currentUser;
  if (!user) return false;
  const token = await user.getIdTokenResult();
  return Boolean(token.claims.admin);
}

/**
 * Populates sidebar user info elements.
 */
export function populateSidebarUser(user) {
  const nameEl  = document.getElementById("sidebarUserName");
  const emailEl = document.getElementById("sidebarUserEmail");
  const avatarEl = document.getElementById("sidebarAvatar");
  if (nameEl)  nameEl.textContent  = user.displayName || "User";
  if (emailEl) emailEl.textContent = user.email || "";
  if (avatarEl) avatarEl.textContent = (user.displayName || user.email || "U")[0].toUpperCase();
}

// ─── Theme Toggle ─────────────────────────────────────────────────────────
const THEME_KEY = "smt-theme";

export function initTheme() {
  const saved = localStorage.getItem(THEME_KEY) || "light";
  document.documentElement.setAttribute("data-theme", saved);
  updateThemeIcon(saved);
}

export function toggleTheme() {
  const current = document.documentElement.getAttribute("data-theme") || "light";
  const next = current === "light" ? "dark" : "light";
  document.documentElement.setAttribute("data-theme", next);
  localStorage.setItem(THEME_KEY, next);
  updateThemeIcon(next);
}

function updateThemeIcon(theme) {
  document.querySelectorAll(".theme-toggle").forEach(btn => {
    btn.textContent = theme === "dark" ? "☀️" : "🌙";
  });
}

// ─── Toast Notifications ──────────────────────────────────────────────────
export function showToast(message, type = "info", duration = 3500) {
  let container = document.querySelector(".toast-container");
  if (!container) {
    container = document.createElement("div");
    container.className = "toast-container";
    document.body.appendChild(container);
  }

  const icons = { success: "✅", error: "❌", info: "ℹ️" };
  const toast = document.createElement("div");
  toast.className = `toast ${type}`;

  const iconSpan = document.createElement("span");
  iconSpan.textContent = icons[type] || "ℹ️";

  const msgSpan = document.createElement("span");
  msgSpan.textContent = message;

  toast.appendChild(iconSpan);
  toast.appendChild(msgSpan);
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = "0";
    toast.style.transform = "translateX(110%)";
    toast.style.transition = "all .3s";
    setTimeout(() => toast.remove(), 300);
  }, duration);
}

// ─── Mobile Sidebar Toggle ────────────────────────────────────────────────
export function initMobileMenu() {
  const menuBtn  = document.getElementById("mobileMenuBtn");
  const closeBtn = document.getElementById("sidebarCloseBtn");
  const sidebar  = document.querySelector(".sidebar");
  const overlay  = document.getElementById("sidebarOverlay");

  if (menuBtn)  menuBtn.addEventListener("click",  () => sidebar.classList.add("open"));
  if (closeBtn) closeBtn.addEventListener("click", () => sidebar.classList.remove("open"));
  if (overlay)  overlay.addEventListener("click",  () => sidebar.classList.remove("open"));
}
