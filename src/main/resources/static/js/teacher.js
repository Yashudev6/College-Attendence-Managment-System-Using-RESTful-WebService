/**
 * Pillai System - Teacher Dashboard Script
 * Handles teacher profile loading, class roster inspection, marking attendance, and entering exam marks
 */

let currentTeacherId = null;
let assignedSubjects = [];
let allStudents = [];
let allClasses = [];

document.addEventListener('DOMContentLoaded', async () => {
    if (!checkRoleAccess('TEACHER')) return;

    const user = getUser();
    if (user && user.name) {
        document.getElementById('teacherNameDisplay').textContent = user.name;
        currentTeacherId = user.entityId;
    }

    // Set today's date in attendance modal
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('attDate').value = today;

    await loadTeacherOverview();
    loadAttendanceHistory();
    loadResultHistory();
});

function showTeacherTab(tabId) {
    document.querySelectorAll('.teacher-tab-content').forEach(el => el.classList.add('d-none'));
    document.querySelectorAll('.sidebar-nav .nav-link').forEach(el => el.classList.remove('active'));

    const targetTab = document.getElementById(tabId);
    if (targetTab) {
        targetTab.classList.remove('d-none');
    }

    const activeLink = document.querySelector(`.sidebar-nav .nav-link[onclick*="${tabId}"]`);
    if (activeLink) {
        activeLink.classList.add('active');
    }
}

// -------------------------------------------------------------
// TEACHER OVERVIEW & ASSIGNMENTS
// -------------------------------------------------------------
async function loadTeacherOverview() {
    try {
        const user = getUser();
        const [subRes, classRes, studRes, teacherRes] = await Promise.all([
            apiCall('/subjects'),
            apiCall('/classes'),
            apiCall('/students'),
            currentTeacherId ? apiCall(`/teachers/${currentTeacherId}`) : Promise.resolve(null)
        ]);

        if (subRes.success) {
            // Filter subjects assigned to this teacher (or all if admin testing)
            assignedSubjects = subRes.data.filter(s => !currentTeacherId || s.teacherId === currentTeacherId);
            if (assignedSubjects.length === 0) assignedSubjects = subRes.data; // Fallback to all if unassigned
        }

        if (classRes.success) allClasses = classRes.data;
        if (studRes.success) allStudents = studRes.data;

        // Render Assigned Subjects List
        const subList = document.getElementById('assignedSubjectsList');
        subList.innerHTML = '';
        if (assignedSubjects.length > 0) {
            assignedSubjects.forEach(s => {
                const li = document.createElement('li');
                li.className = 'list-group-item d-flex justify-content-between align-items-center';
                li.innerHTML = `
                    <div>
                        <strong class="text-primary">${s.subjectCode}</strong> - ${s.subjectName}
                        <small class="d-block text-muted">Class: ${s.className || 'N/A'}</small>
                    </div>
                    <span class="badge bg-secondary">${s.credits} Credits</span>
                `;
                subList.appendChild(li);
            });
        } else {
            subList.innerHTML = '<li class="list-group-item text-muted">No subjects assigned yet.</li>';
        }

        // Render Teacher Profile Info Box
        const pBox = document.getElementById('teacherProfileBox');
        if (teacherRes && teacherRes.success) {
            const t = teacherRes.data;
            pBox.innerHTML = `
                <div class="mb-2"><strong>Faculty ID:</strong> <span class="badge bg-primary">${t.teacherId}</span></div>
                <div class="mb-2"><strong>Name:</strong> ${t.name}</div>
                <div class="mb-2"><strong>Email:</strong> ${t.email}</div>
                <div class="mb-2"><strong>Phone:</strong> ${t.phone || 'N/A'}</div>
                <div><strong>Department:</strong> ${t.departmentName || 'N/A'}</div>
            `;
        } else {
            pBox.innerHTML = `
                <div class="mb-2"><strong>Name:</strong> ${user.name}</div>
                <div class="mb-2"><strong>Email:</strong> ${user.email}</div>
                <div><strong>Role:</strong> ${user.role}</div>
            `;
        }

        // Populate Class Select Roster dropdown
        const cSelect = document.getElementById('classSelectRoster');
        cSelect.innerHTML = '<option value="">-- Select Class --</option>';
        allClasses.forEach(c => {
            const opt = document.createElement('option');
            opt.value = c.id;
            opt.textContent = c.className;
            cSelect.appendChild(opt);
        });

        // Populate Modal Dropdowns
        populateModalDropdowns();

    } catch (err) {
        showToast('Error loading faculty overview: ' + err.message, 'danger');
    }
}

function populateModalDropdowns() {
    const attSubSelect = document.getElementById('attSubjectSelect');
    const marksSubSelect = document.getElementById('marksSubjectSelect');
    const attStudSelect = document.getElementById('attStudentSelect');
    const marksStudSelect = document.getElementById('marksStudentSelect');
    const attFilterSub = document.getElementById('attendanceFilterSubject');

    attSubSelect.innerHTML = '<option value="">-- Select Subject --</option>';
    marksSubSelect.innerHTML = '<option value="">-- Select Subject --</option>';
    attFilterSub.innerHTML = '<option value="">-- All Subjects --</option>';

    assignedSubjects.forEach(s => {
        const opt1 = new Option(`${s.subjectCode} - ${s.subjectName}`, s.id);
        const opt2 = new Option(`${s.subjectCode} - ${s.subjectName}`, s.id);
        const opt3 = new Option(`${s.subjectCode} - ${s.subjectName}`, s.id);
        attSubSelect.add(opt1);
        marksSubSelect.add(opt2);
        attFilterSub.add(opt3);
    });

    attStudSelect.innerHTML = '<option value="">-- Select Student --</option>';
    marksStudSelect.innerHTML = '<option value="">-- Select Student --</option>';
    allStudents.forEach(st => {
        const opt1 = new Option(`${st.studentId} - ${st.name} (${st.className || 'N/A'})`, st.id);
        const opt2 = new Option(`${st.studentId} - ${st.name} (${st.className || 'N/A'})`, st.id);
        attStudSelect.add(opt1);
        marksStudSelect.add(opt2);
    });
}

function loadClassRoster() {
    const classId = document.getElementById('classSelectRoster').value;
    const tbody = document.getElementById('rosterTableBody');
    tbody.innerHTML = '';

    if (!classId) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-4">Select a class to view roster.</td></tr>';
        return;
    }

    const filtered = allStudents.filter(st => st.classId == classId);
    if (filtered.length > 0) {
        filtered.forEach(st => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td class="fw-bold">${st.studentId}</td>
                <td>${st.name}</td>
                <td>${st.email}</td>
                <td>${st.phone || 'N/A'}</td>
                <td>${st.departmentName || 'N/A'}</td>
            `;
            tbody.appendChild(tr);
        });
    } else {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-4">No students enrolled in this class.</td></tr>';
    }
}

// -------------------------------------------------------------
// ATTENDANCE MANAGEMENT
// -------------------------------------------------------------
let cachedAttendanceHistory = [];

async function loadAttendanceHistory() {
    try {
        const res = await apiCall('/attendance');
        if (res.success) {
            cachedAttendanceHistory = res.data;
            renderAttendanceHistory(cachedAttendanceHistory);
        }
    } catch (err) {
        showToast('Error loading attendance history: ' + err.message, 'danger');
    }
}

function filterAttendanceHistory() {
    const subId = document.getElementById('attendanceFilterSubject').value;
    if (!subId) {
        renderAttendanceHistory(cachedAttendanceHistory);
    } else {
        const filtered = cachedAttendanceHistory.filter(a => a.subjectId == subId);
        renderAttendanceHistory(filtered);
    }
}

function renderAttendanceHistory(dataList) {
    const tbody = document.getElementById('attendanceHistoryTableBody');
    tbody.innerHTML = '';

    if (dataList && dataList.length > 0) {
        dataList.forEach(a => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td class="fw-bold">${a.id}</td>
                <td>${a.date}</td>
                <td class="fw-semibold">${a.studentNumber}</td>
                <td>${a.studentName}</td>
                <td><span class="badge bg-secondary">${a.subjectCode}</span> ${a.subjectName}</td>
                <td>${formatAttendanceBadge(a.status)}</td>
                <td><small class="text-muted">${a.remarks || '-'}</small></td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="editAttendance(${a.id})"><i class="fa-solid fa-pen"></i></button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteAttendance(${a.id})"><i class="fa-solid fa-trash"></i></button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } else {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted py-4">No attendance records found.</td></tr>';
    }
}

function openMarkAttendanceModal(att = null) {
    const modal = new bootstrap.Modal(document.getElementById('markAttendanceModal'));
    document.getElementById('attendanceForm').reset();
    document.getElementById('attEditId').value = '';

    const today = new Date().toISOString().split('T')[0];
    document.getElementById('attDate').value = today;

    if (att) {
        document.getElementById('attModalTitle').textContent = 'Edit Attendance Record';
        document.getElementById('attEditId').value = att.id;
        document.getElementById('attSubjectSelect').value = att.subjectId;
        document.getElementById('attStudentSelect').value = att.studentId;
        document.getElementById('attDate').value = att.date;
        document.getElementById('attStatus').value = att.status;
        document.getElementById('attRemarks').value = att.remarks || '';
    } else {
        document.getElementById('attModalTitle').textContent = 'Mark Class Attendance';
    }
    modal.show();
}

async function editAttendance(id) {
    try {
        const res = await apiCall(`/attendance/${id}`);
        if (res.success) openMarkAttendanceModal(res.data);
    } catch (err) {
        showToast('Failed to fetch attendance details', 'danger');
    }
}

async function deleteAttendance(id) {
    if (!confirm('Are you sure you want to delete this attendance record?')) return;
    try {
        const res = await apiCall(`/attendance/${id}`, 'DELETE');
        if (res.success) {
            showToast('Attendance deleted successfully', 'success');
            loadAttendanceHistory();
        }
    } catch (err) {
        showToast('Delete failed: ' + err.message, 'danger');
    }
}

document.getElementById('attendanceForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('attEditId').value;
    const body = {
        subjectId: parseInt(document.getElementById('attSubjectSelect').value),
        studentId: parseInt(document.getElementById('attStudentSelect').value),
        date: document.getElementById('attDate').value,
        status: document.getElementById('attStatus').value,
        remarks: document.getElementById('attRemarks').value.trim()
    };

    try {
        const method = id ? 'PUT' : 'POST';
        const endpoint = id ? `/attendance/${id}` : '/attendance';
        const res = await apiCall(endpoint, method, body);
        if (res.success) {
            showToast(`Attendance ${id ? 'updated' : 'marked'} successfully`, 'success');
            bootstrap.Modal.getInstance(document.getElementById('markAttendanceModal')).hide();
            loadAttendanceHistory();
        }
    } catch (err) {
        showToast('Operation failed: ' + err.message, 'danger');
    }
});

// -------------------------------------------------------------
// RESULTS & MARKS MANAGEMENT
// -------------------------------------------------------------
async function loadResultHistory() {
    try {
        const res = await apiCall('/results');
        const tbody = document.getElementById('resultsHistoryTableBody');
        tbody.innerHTML = '';

        if (res.success && res.data.length > 0) {
            res.data.forEach(r => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="fw-bold">${r.id}</td>
                    <td><strong>${r.studentNumber}</strong> - ${r.studentName}</td>
                    <td><span class="badge bg-secondary">${r.subjectCode}</span> ${r.subjectName}</td>
                    <td class="fw-bold text-primary">${r.marks}</td>
                    <td>${r.maxMarks}</td>
                    <td>${r.percentage}%</td>
                    <td>${formatGradeBadge(r.grade)}</td>
                    <td>${r.examType}</td>
                    <td>${r.semester}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary me-1" onclick="editMarks(${r.id})"><i class="fa-solid fa-pen"></i></button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteMarks(${r.id})"><i class="fa-solid fa-trash"></i></button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="10" class="text-center text-muted py-4">No examination marks recorded yet.</td></tr>';
        }
    } catch (err) {
        showToast('Error loading exam marks: ' + err.message, 'danger');
    }
}

function openEnterMarksModal(resObj = null) {
    const modal = new bootstrap.Modal(document.getElementById('enterMarksModal'));
    document.getElementById('marksForm').reset();
    document.getElementById('marksEditId').value = '';

    if (resObj) {
        document.getElementById('marksModalTitle').textContent = 'Edit Examination Marks';
        document.getElementById('marksEditId').value = resObj.id;
        document.getElementById('marksSubjectSelect').value = resObj.subjectId;
        document.getElementById('marksStudentSelect').value = resObj.studentId;
        document.getElementById('marksInput').value = resObj.marks;
        document.getElementById('maxMarksInput').value = resObj.maxMarks;
        document.getElementById('semesterInput').value = resObj.semester;
        document.getElementById('examTypeInput').value = resObj.examType;
        document.getElementById('marksRemarks').value = resObj.remarks || '';
    } else {
        document.getElementById('marksModalTitle').textContent = 'Enter Student Examination Marks';
    }
    modal.show();
}

async function editMarks(id) {
    try {
        const res = await apiCall(`/results/${id}`);
        if (res.success) openEnterMarksModal(res.data);
    } catch (err) {
        showToast('Failed to fetch result details', 'danger');
    }
}

async function deleteMarks(id) {
    if (!confirm('Are you sure you want to delete this result record?')) return;
    try {
        const res = await apiCall(`/results/${id}`, 'DELETE');
        if (res.success) {
            showToast('Result record deleted successfully', 'success');
            loadResultHistory();
        }
    } catch (err) {
        showToast('Delete failed: ' + err.message, 'danger');
    }
}

document.getElementById('marksForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('marksEditId').value;
    const body = {
        subjectId: parseInt(document.getElementById('marksSubjectSelect').value),
        studentId: parseInt(document.getElementById('marksStudentSelect').value),
        marks: parseFloat(document.getElementById('marksInput').value),
        maxMarks: parseFloat(document.getElementById('maxMarksInput').value),
        semester: document.getElementById('semesterInput').value.trim(),
        examType: document.getElementById('examTypeInput').value,
        remarks: document.getElementById('marksRemarks').value.trim()
    };

    try {
        const method = id ? 'PUT' : 'POST';
        const endpoint = id ? `/results/${id}` : '/results';
        const res = await apiCall(endpoint, method, body);
        if (res.success) {
            showToast(`Examination marks ${id ? 'updated' : 'entered'} successfully! Grade: ${res.data.grade}`, 'success');
            bootstrap.Modal.getInstance(document.getElementById('enterMarksModal')).hide();
            loadResultHistory();
        }
    } catch (err) {
        showToast('Operation failed: ' + err.message, 'danger');
    }
});
