/**
 * Pillai System - Student Dashboard Script
 * Fetches personal student profile, attendance summary, subject-wise log, and exam results
 */

let currentStudentId = null;

document.addEventListener('DOMContentLoaded', async () => {
    if (!checkRoleAccess('STUDENT')) return;

    const user = getUser();
    if (user) {
        document.getElementById('studentNameDisplay').textContent = user.name || user.username;
    }

    await loadStudentProfile();
});

function showStudentTab(tabId) {
    document.querySelectorAll('.student-tab-content').forEach(el => el.classList.add('d-none'));
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

async function loadStudentProfile() {
    try {
        const user = getUser();
        let studentData = null;

        if (user && user.entityId) {
            const res = await apiCall(`/students/${user.entityId}`);
            if (res.success) studentData = res.data;
        }

        if (!studentData) {
            // Fallback fetch profile by username
            const res = await apiCall('/students/me');
            if (res.success) studentData = res.data;
        }

        if (!studentData) throw new Error('Student record not found.');

        currentStudentId = studentData.id;
        document.getElementById('studentNameDisplay').textContent = studentData.name;

        // Render Personal Info Card
        const infoCard = document.getElementById('studentInfoCard');
        infoCard.innerHTML = `
            <div class="mb-2"><strong>Student ID:</strong> <span class="badge bg-primary fs-6">${studentData.studentId}</span></div>
            <div class="mb-2"><strong>Full Name:</strong> ${studentData.name}</div>
            <div class="mb-2"><strong>Email:</strong> ${studentData.email}</div>
            <div class="mb-2"><strong>Phone:</strong> ${studentData.phone || 'N/A'}</div>
            <div class="mb-2"><strong>Enrolled Class:</strong> <span class="badge bg-info">${studentData.className || 'Unassigned'}</span></div>
            <div class="mb-2"><strong>Department:</strong> ${studentData.departmentName || 'N/A'}</div>
            <div class="mb-2"><strong>Gender:</strong> ${studentData.gender || 'N/A'}</div>
            <div><strong>Address:</strong> ${studentData.address || 'N/A'}</div>
        `;

        // Load Class Subjects
        if (studentData.classId) {
            loadEnrolledSubjects(studentData.classId);
        } else {
            document.getElementById('studentSubjectsTableBody').innerHTML = '<tr><td colspan="4" class="text-center text-muted py-3">No class assigned.</td></tr>';
        }

        // Load Attendance & Results summaries concurrently
        loadAttendanceData(studentData.id);
        loadResultsData(studentData.id);

    } catch (err) {
        showToast('Error loading profile: ' + err.message, 'danger');
    }
}

async function loadEnrolledSubjects(classId) {
    try {
        const res = await apiCall(`/subjects/class/${classId}`);
        const tbody = document.getElementById('studentSubjectsTableBody');
        tbody.innerHTML = '';

        if (res.success && res.data.length > 0) {
            res.data.forEach(sub => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="fw-bold text-primary">${sub.subjectCode}</td>
                    <td>${sub.subjectName}</td>
                    <td><span class="badge bg-secondary">${sub.credits}</span></td>
                    <td>${sub.teacherName || 'Faculty Member'}</td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted py-3">No subjects configured for this class.</td></tr>';
        }
    } catch (err) {
        console.error('Failed to load subjects:', err);
    }
}

async function loadAttendanceData(studentId) {
    try {
        // Fetch Attendance Summary & Log
        const [sumRes, logRes] = await Promise.all([
            apiCall(`/attendance/summary/student/${studentId}`),
            apiCall(`/attendance/student/${studentId}`)
        ]);

        if (sumRes.success && sumRes.data) {
            const sum = sumRes.data;
            document.getElementById('profileAttPct').textContent = `${sum.attendancePercentage}%`;
            document.getElementById('sumTotalClasses').textContent = sum.totalClasses;
            document.getElementById('sumPresent').textContent = sum.presentCount;
            document.getElementById('sumAbsent').textContent = sum.absentCount;
            document.getElementById('sumLate').textContent = sum.lateCount;

            // Low attendance alert banner check
            const alertBanner = document.getElementById('studentLowAttAlert');
            if (sum.lowAttendance) {
                alertBanner.classList.remove('d-none');
                document.getElementById('studentLowAttText').textContent = 
                    `Your current attendance is ${sum.attendancePercentage}%, which is below the mandatory 75% requirement!`;
            } else {
                alertBanner.classList.add('d-none');
            }
        }

        // Render Attendance Log Table
        const tbody = document.getElementById('studentAttendanceTableBody');
        tbody.innerHTML = '';
        if (logRes.success && logRes.data.length > 0) {
            logRes.data.forEach(att => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="fw-semibold">${att.date}</td>
                    <td class="fw-bold text-primary">${att.subjectCode}</td>
                    <td>${att.subjectName}</td>
                    <td>${formatAttendanceBadge(att.status)}</td>
                    <td><small class="text-muted">${att.remarks || '-'}</small></td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-4">No attendance records logged yet.</td></tr>';
        }

    } catch (err) {
        showToast('Failed to load attendance records: ' + err.message, 'danger');
    }
}

async function loadResultsData(studentId) {
    try {
        const sumRes = await apiCall(`/results/summary/student/${studentId}`);

        if (sumRes.success && sumRes.data) {
            const sum = sumRes.data;
            document.getElementById('profileGrade').textContent = sum.overallGrade || 'N/A';
            document.getElementById('profileExamPct').textContent = `${sum.overallPercentage}%`;

            document.getElementById('resSummaryStudentName').textContent = `${sum.studentName} (${sum.studentNumber})`;
            document.getElementById('resSummaryClass').textContent = `Class: ${sum.className} | Total Marks: ${sum.totalMarks} / ${sum.totalMaxMarks} (${sum.overallPercentage}%)`;
            document.getElementById('resSummaryOverallGrade').textContent = sum.overallGrade;

            // Render Exam Results Table
            const tbody = document.getElementById('studentResultsTableBody');
            tbody.innerHTML = '';

            if (sum.results && sum.results.length > 0) {
                sum.results.forEach(r => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td class="fw-bold text-warning">${r.subjectCode}</td>
                        <td>${r.subjectName}</td>
                        <td class="fw-bold text-primary">${r.marks}</td>
                        <td>${r.maxMarks}</td>
                        <td>${r.percentage}%</td>
                        <td>${formatGradeBadge(r.grade)}</td>
                        <td>${r.examType}</td>
                        <td>${r.semester}</td>
                    `;
                    tbody.appendChild(tr);
                });
            } else {
                tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted py-4">No examination results available yet.</td></tr>';
            }
        }
    } catch (err) {
        showToast('Failed to load examination results: ' + err.message, 'danger');
    }
}
