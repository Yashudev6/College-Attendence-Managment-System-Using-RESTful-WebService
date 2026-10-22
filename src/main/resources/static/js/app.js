/**
 * Pillai System - Master JavaScript Library
 * Shared Utilities, REST API Fetch Wrapper, Auth Session Management
 */

const API_BASE_URL = '/api';

// Auth State Helpers
function getToken() {
    return localStorage.getItem('pillai_jwt_token');
}

function getUser() {
    const userStr = localStorage.getItem('pillai_user');
    return userStr ? JSON.parse(userStr) : null;
}

function setAuth(authData) {
    localStorage.setItem('pillai_jwt_token', authData.token);
    localStorage.setItem('pillai_user', JSON.stringify(authData));
}

function clearAuth() {
    localStorage.removeItem('pillai_jwt_token');
    localStorage.removeItem('pillai_user');
}

function logout() {
    clearAuth();
    window.location.href = '/index.html';
}

function checkRoleAccess(requiredRole) {
    const user = getUser();
    const token = getToken();
    if (!token || !user) {
        window.location.href = '/index.html';
        return false;
    }
    if (requiredRole && user.role !== requiredRole && user.role !== 'ADMIN') {
        alert('Access denied: You do not have permission to view this panel.');
        window.location.href = '/index.html';
        return false;
    }
    return true;
}

// REST API Fetch Wrapper
async function apiCall(endpoint, method = 'GET', body = null) {
    const token = getToken();
    const headers = {
        'Content-Type': 'application/json'
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        method: method,
        headers: headers
    };

    if (body) {
        config.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);

        if (response.status === 401) {
            clearAuth();
            window.location.href = '/index.html';
            throw new Error('Session expired. Please log in again.');
        }

        const json = await response.json();

        if (!response.ok) {
            throw new Error(json.message || `HTTP error! Status: ${response.status}`);
        }

        return json;
    } catch (error) {
        console.error(`API Call Error [${method} ${endpoint}]:`, error);
        throw error;
    }
}

// UI Toast Notification helper
function showToast(message, type = 'success') {
    let container = document.getElementById('toastContainer');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const bgClass = type === 'success' ? 'bg-success' : type === 'danger' ? 'bg-danger' : 'bg-warning';

    const toastHtml = `
        <div class="toast align-items-center text-white ${bgClass} border-0 show" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;

    const div = document.createElement('div');
    div.innerHTML = toastHtml;
    const toastEl = div.firstElementChild;
    container.appendChild(toastEl);

    setTimeout(() => {
        toastEl.remove();
    }, 4000);
}

// Grade Badge Formatter
function formatGradeBadge(grade) {
    if (!grade) return '<span class="badge bg-secondary">N/A</span>';
    let gClass = 'grade-c';
    if (grade === 'A+') gClass = 'grade-aplus';
    else if (grade === 'A') gClass = 'grade-a';
    else if (grade === 'B+') gClass = 'grade-bplus';
    else if (grade === 'B') gClass = 'grade-b';
    else if (grade === 'F') gClass = 'grade-f';

    return `<span class="badge-grade ${gClass}">${grade}</span>`;
}

// Attendance Badge Formatter
function formatAttendanceBadge(status) {
    if (status === 'PRESENT') return '<span class="badge-status badge-present">Present</span>';
    if (status === 'ABSENT') return '<span class="badge-status badge-absent">Absent</span>';
    if (status === 'LATE') return '<span class="badge-status badge-late">Late</span>';
    return `<span class="badge bg-secondary">${status}</span>`;
}
