/**
 * Pillai System - Admin Dashboard Script
 * Implements stats fetching, student/teacher/dept/class/subject CRUD handlers
 */

let cachedDepartments = [];
let cachedClasses = [];
let cachedTeachers = [];

document.addEventListener('DOMContentLoaded', () => {
    if (!checkRoleAccess('ADMIN')) return;

    const user = getUser();
    if (user && user.name) {
        document.getElementById('adminNameDisplay').textContent = user.name;
    }

    loadAdminStats();
    loadDropdownOptions();
    loadStudents();
    loadTeachers();
    loadDepartments();
    loadClasses();
    loadSubjects();
    loadUsers();
});

function showTab(tabId) {
    document.querySelectorAll('.tab-pane-content').forEach(el => el.classList.add('d-none'));
    document.querySelectorAll('.sidebar-nav .nav-link').forEach(el => el.classList.remove('active'));

    const targetTab = document.getElementById(tabId);
    if (targetTab) {
        targetTab.classList.remove('d-none');
    }

    // Highlight sidebar link
    const activeLink = document.querySelector(`.sidebar-nav .nav-link[onclick*="${tabId}"]`);
    if (activeLink) {
        activeLink.classList.add('active');
    }
}

// -------------------------------------------------------------
// OVERVIEW STATS & LOW ATTENDANCE
// -------------------------------------------------------------
async function loadAdminStats() {
    try {
        const response = await apiCall('/dashboard/stats');
        if (response.success && response.data) {
            const stats = response.data;
            document.getElementById('statTotalStudents').textContent = stats.totalStudents;
            document.getElementById('statTotalTeachers').textContent = stats.totalTeachers;
            document.getElementById('statTotalSubjects').textContent = stats.totalSubjects;
            document.getElementById('statTotalClasses').textContent = stats.totalClasses;
            document.getElementById('statTotalAttendance').textContent = stats.totalAttendanceRecords;
            document.getElementById('statTotalResults').textContent = stats.totalResults;
            document.getElementById('statLowAttendance').textContent = stats.lowAttendanceCount;

            // Low attendance banner
            const banner = document.getElementById('lowAttendanceBanner');
            if (stats.lowAttendanceCount > 0) {
                banner.classList.remove('d-none');
                document.getElementById('lowAttendanceBannerText').textContent = 
                    `${stats.lowAttendanceCount} student(s) have attendance below the required 75% threshold!`;
            } else {
                banner.classList.add('d-none');
            }

            // Low attendance table
            const tbody = document.getElementById('lowAttendanceTableBody');
            tbody.innerHTML = '';
            if (stats.lowAttendanceStudents && stats.lowAttendanceStudents.length > 0) {
                stats.lowAttendanceStudents.forEach(item => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td class="fw-bold text-danger">${item.studentNumber}</td>
                        <td>${item.studentName}</td>
                        <td>${item.className}</td>
                        <td>${item.presentCount + item.lateCount} / ${item.totalClasses}</td>
                        <td><span class="badge bg-danger">${item.attendancePercentage}%</span></td>
                    `;
                    tbody.appendChild(tr);
                });
            } else {
                tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-4">No low attendance warnings detected.</td></tr>';
            }

            // Recent activity
            const actList = document.getElementById('recentActivitiesList');
            actList.innerHTML = '';
            if (stats.recentActivities && stats.recentActivities.length > 0) {
                stats.recentActivities.forEach(act => {
                    const li = document.createElement('li');
                    li.className = 'list-group-item d-flex align-items-center gap-2';
                    li.innerHTML = `<i class="fa-solid fa-circle-info text-accent"></i> ${act}`;
                    actList.appendChild(li);
                });
            }
        }
    } catch (err) {
        showToast('Failed to load dashboard stats: ' + err.message, 'danger');
    }
}

// -------------------------------------------------------------
// DROPDOWN OPTION LOADERS
// -------------------------------------------------------------
async function loadDropdownOptions() {
    try {
        const [deptRes, classRes, teachRes] = await Promise.all([
            apiCall('/departments'),
            apiCall('/classes'),
            apiCall('/teachers')
        ]);

        if (deptRes.success) cachedDepartments = deptRes.data;
        if (classRes.success) cachedClasses = classRes.data;
        if (teachRes.success) cachedTeachers = teachRes.data;

        populateSelectOptions('studentDeptSelect', cachedDepartments, 'id', 'departmentName');
        populateSelectOptions('studentClassSelect', cachedClasses, 'id', 'className');
        populateSelectOptions('teacherDeptSelect', cachedDepartments, 'id', 'departmentName');
        populateSelectOptions('classDeptSelect', cachedDepartments, 'id', 'departmentName');
        populateSelectOptions('subjectClassSelect', cachedClasses, 'id', 'className');
        populateSelectOptions('subjectTeacherSelect', cachedTeachers, 'id', 'name');
    } catch (err) {
        console.error('Error loading dropdowns:', err);
    }
}

function populateSelectOptions(selectId, items, valueKey, labelKey) {
    const select = document.getElementById(selectId);
    if (!select) return;
    select.innerHTML = '<option value="">-- Select --</option>';
    items.forEach(item => {
        const opt = document.createElement('option');
        opt.value = item[valueKey];
        opt.textContent = item[labelKey];
        select.appendChild(opt);
    });
}

// -------------------------------------------------------------
// STUDENTS CRUD
// -------------------------------------------------------------
async function loadStudents(searchQuery = '') {
    try {
        const endpoint = searchQuery ? `/students?search=${encodeURIComponent(searchQuery)}` : '/students';
        const res = await apiCall(endpoint);
        const tbody = document.getElementById('studentsTableBody');
        tbody.innerHTML = '';

        if (res.success && res.data.length > 0) {
            res.data.forEach(s => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="fw-bold">${s.studentId}</td>
                    <td>${s.name}</td>
                    <td>${s.email}</td>
                    <td>${s.phone || 'N/A'}</td>
                    <td><span class="badge bg-secondary">${s.className || 'Unassigned'}</span></td>
                    <td>${s.departmentName || 'N/A'}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary me-1" onclick="editStudent(${s.id})"><i class="fa-solid fa-pen"></i></button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteStudent(${s.id})"><i class="fa-solid fa-trash"></i></button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted py-4">No students found.</td></tr>';
        }
    } catch (err) {
        showToast('Error loading students: ' + err.message, 'danger');
    }
}

function searchStudents() {
    const query = document.getElementById('studentSearchInput').value;
    loadStudents(query);
}

function openStudentModal(student = null) {
    const modal = new bootstrap.Modal(document.getElementById('studentModal'));
    document.getElementById('studentForm').reset();
    document.getElementById('studentEditId').value = '';

    if (student) {
        document.getElementById('studentModalTitle').textContent = 'Edit Student';
        document.getElementById('studentEditId').value = student.id;
        document.getElementById('studentIdCode').value = student.studentId;
        document.getElementById('studentName').value = student.name;
        document.getElementById('studentEmail').value = student.email;
        document.getElementById('studentPhone').value = student.phone || '';
        document.getElementById('studentClassSelect').value = student.classId || '';
        document.getElementById('studentDeptSelect').value = student.departmentId || '';
    } else {
        document.getElementById('studentModalTitle').textContent = 'Add New Student';
    }
    modal.show();
}

async function editStudent(id) {
    try {
        const res = await apiCall(`/students/${id}`);
        if (res.success) {
            openStudentModal(res.data);
        }
    } catch (err) {
        showToast('Failed to fetch student details', 'danger');
    }
}

async function deleteStudent(id) {
    if (!confirm('Are you sure you want to delete this student record? This cannot be undone.')) return;
    try {
        const res = await apiCall(`/students/${id}`, 'DELETE');
        if (res.success) {
            showToast('Student deleted successfully', 'success');
            loadStudents();
            loadAdminStats();
        }
    } catch (err) {
        showToast('Delete failed: ' + err.message, 'danger');
    }
}

document.getElementById('studentForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('studentEditId').value;
    const body = {
        studentId: document.getElementById('studentIdCode').value.trim(),
        name: document.getElementById('studentName').value.trim(),
        email: document.getElementById('studentEmail').value.trim(),
        phone: document.getElementById('studentPhone').value.trim(),
        classId: document.getElementById('studentClassSelect').value ? parseInt(document.getElementById('studentClassSelect').value) : null,
        departmentId: document.getElementById('studentDeptSelect').value ? parseInt(document.getElementById('studentDeptSelect').value) : null
    };

    try {
        const method = id ? 'PUT' : 'POST';
        const endpoint = id ? `/students/${id}` : '/students';
        const res = await apiCall(endpoint, method, body);
        if (res.success) {
            showToast(`Student ${id ? 'updated' : 'created'} successfully`, 'success');
            bootstrap.Modal.getInstance(document.getElementById('studentModal')).hide();
            loadStudents();
            loadAdminStats();
        }
    } catch (err) {
        showToast('Save failed: ' + err.message, 'danger');
    }
});

// -------------------------------------------------------------
// TEACHERS CRUD
// -------------------------------------------------------------
async function loadTeachers() {
    try {
        const res = await apiCall('/teachers');
        const tbody = document.getElementById('teachersTableBody');
        tbody.innerHTML = '';

        if (res.success && res.data.length > 0) {
            res.data.forEach(t => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="fw-bold">${t.teacherId}</td>
                    <td>${t.name}</td>
                    <td>${t.email}</td>
                    <td>${t.phone || 'N/A'}</td>
                    <td><span class="badge bg-secondary">${t.departmentName || 'N/A'}</span></td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary me-1" onclick="editTeacher(${t.id})"><i class="fa-solid fa-pen"></i></button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteTeacher(${t.id})"><i class="fa-solid fa-trash"></i></button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">No teachers registered.</td></tr>';
        }
    } catch (err) {
        showToast('Error loading teachers: ' + err.message, 'danger');
    }
}

function openTeacherModal(teacher = null) {
    const modal = new bootstrap.Modal(document.getElementById('teacherModal'));
    document.getElementById('teacherForm').reset();
    document.getElementById('teacherEditId').value = '';

    if (teacher) {
        document.getElementById('teacherModalTitle').textContent = 'Edit Teacher';
        document.getElementById('teacherEditId').value = teacher.id;
        document.getElementById('teacherIdCode').value = teacher.teacherId;
        document.getElementById('teacherName').value = teacher.name;
        document.getElementById('teacherEmail').value = teacher.email;
        document.getElementById('teacherPhone').value = teacher.phone || '';
        document.getElementById('teacherDeptSelect').value = teacher.departmentId || '';
    } else {
        document.getElementById('teacherModalTitle').textContent = 'Add New Teacher';
    }
    modal.show();
}

async function editTeacher(id) {
    try {
        const res = await apiCall(`/teachers/${id}`);
        if (res.success) openTeacherModal(res.data);
    } catch (err) {
        showToast('Failed to fetch teacher details', 'danger');
    }
}

async function deleteTeacher(id) {
    if (!confirm('Are you sure you want to delete this teacher?')) return;
    try {
        const res = await apiCall(`/teachers/${id}`, 'DELETE');
        if (res.success) {
            showToast('Teacher deleted successfully', 'success');
            loadTeachers();
            loadAdminStats();
        }
    } catch (err) {
        showToast('Delete failed: ' + err.message, 'danger');
    }
}

document.getElementById('teacherForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('teacherEditId').value;
    const body = {
        teacherId: document.getElementById('teacherIdCode').value.trim(),
        name: document.getElementById('teacherName').value.trim(),
        email: document.getElementById('teacherEmail').value.trim(),
        phone: document.getElementById('teacherPhone').value.trim(),
        departmentId: document.getElementById('teacherDeptSelect').value ? parseInt(document.getElementById('teacherDeptSelect').value) : null
    };

    try {
        const method = id ? 'PUT' : 'POST';
        const endpoint = id ? `/teachers/${id}` : '/teachers';
        const res = await apiCall(endpoint, method, body);
        if (res.success) {
            showToast(`Teacher ${id ? 'updated' : 'created'} successfully`, 'success');
            bootstrap.Modal.getInstance(document.getElementById('teacherModal')).hide();
            loadTeachers();
            loadAdminStats();
            loadDropdownOptions();
        }
    } catch (err) {
        showToast('Save failed: ' + err.message, 'danger');
    }
});

// -------------------------------------------------------------
// DEPARTMENTS CRUD
// -------------------------------------------------------------
async function loadDepartments() {
    try {
        const res = await apiCall('/departments');
        const tbody = document.getElementById('departmentsTableBody');
        tbody.innerHTML = '';

        if (res.success && res.data.length > 0) {
            res.data.forEach(d => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="fw-bold">${d.id}</td>
                    <td class="fw-semibold text-primary">${d.departmentName}</td>
                    <td>${d.description || 'N/A'}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary me-1" onclick="editDepartment(${d.id})"><i class="fa-solid fa-pen"></i></button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteDepartment(${d.id})"><i class="fa-solid fa-trash"></i></button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted py-4">No departments found.</td></tr>';
        }
    } catch (err) {
        showToast('Error loading departments: ' + err.message, 'danger');
    }
}

function openDepartmentModal(dept = null) {
    const modal = new bootstrap.Modal(document.getElementById('deptModal'));
    document.getElementById('deptForm').reset();
    document.getElementById('deptEditId').value = '';

    if (dept) {
        document.getElementById('deptModalTitle').textContent = 'Edit Department';
        document.getElementById('deptEditId').value = dept.id;
        document.getElementById('deptName').value = dept.departmentName;
        document.getElementById('deptDesc').value = dept.description || '';
    } else {
        document.getElementById('deptModalTitle').textContent = 'Add Department';
    }
    modal.show();
}

async function editDepartment(id) {
    try {
        const res = await apiCall(`/departments/${id}`);
        if (res.success) openDepartmentModal(res.data);
    } catch (err) {
        showToast('Failed to fetch department details', 'danger');
    }
}

async function deleteDepartment(id) {
    if (!confirm('Are you sure you want to delete this department?')) return;
    try {
        const res = await apiCall(`/departments/${id}`, 'DELETE');
        if (res.success) {
            showToast('Department deleted successfully', 'success');
            loadDepartments();
            loadDropdownOptions();
        }
    } catch (err) {
        showToast('Delete failed: ' + err.message, 'danger');
    }
}

document.getElementById('deptForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('deptEditId').value;
    const body = {
        departmentName: document.getElementById('deptName').value.trim(),
        description: document.getElementById('deptDesc').value.trim()
    };

    try {
        const method = id ? 'PUT' : 'POST';
        const endpoint = id ? `/departments/${id}` : '/departments';
        const res = await apiCall(endpoint, method, body);
        if (res.success) {
            showToast(`Department ${id ? 'updated' : 'created'} successfully`, 'success');
            bootstrap.Modal.getInstance(document.getElementById('deptModal')).hide();
            loadDepartments();
            loadDropdownOptions();
        }
    } catch (err) {
        showToast('Save failed: ' + err.message, 'danger');
    }
});

// -------------------------------------------------------------
// CLASSES CRUD
// -------------------------------------------------------------
async function loadClasses() {
    try {
        const res = await apiCall('/classes');
        const tbody = document.getElementById('classesTableBody');
        tbody.innerHTML = '';

        if (res.success && res.data.length > 0) {
            res.data.forEach(c => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="fw-bold">${c.id}</td>
                    <td class="fw-semibold text-warning">${c.className}</td>
                    <td>${c.semester}</td>
                    <td>${c.departmentName || 'N/A'}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary me-1" onclick="editClass(${c.id})"><i class="fa-solid fa-pen"></i></button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteClass(${c.id})"><i class="fa-solid fa-trash"></i></button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-4">No classes configured.</td></tr>';
        }
    } catch (err) {
        showToast('Error loading classes: ' + err.message, 'danger');
    }
}

function openClassModal(cls = null) {
    const modal = new bootstrap.Modal(document.getElementById('classModal'));
    document.getElementById('classForm').reset();
    document.getElementById('classEditId').value = '';

    if (cls) {
        document.getElementById('classModalTitle').textContent = 'Edit Class';
        document.getElementById('classEditId').value = cls.id;
        document.getElementById('classNameInput').value = cls.className;
        document.getElementById('classSemesterInput').value = cls.semester;
        document.getElementById('classDeptSelect').value = cls.departmentId || '';
    } else {
        document.getElementById('classModalTitle').textContent = 'Add Class';
    }
    modal.show();
}

async function editClass(id) {
    try {
        const res = await apiCall(`/classes/${id}`);
        if (res.success) openClassModal(res.data);
    } catch (err) {
        showToast('Failed to fetch class details', 'danger');
    }
}

async function deleteClass(id) {
    if (!confirm('Are you sure you want to delete this class?')) return;
    try {
        const res = await apiCall(`/classes/${id}`, 'DELETE');
        if (res.success) {
            showToast('Class deleted successfully', 'success');
            loadClasses();
            loadAdminStats();
            loadDropdownOptions();
        }
    } catch (err) {
        showToast('Delete failed: ' + err.message, 'danger');
    }
}

document.getElementById('classForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('classEditId').value;
    const body = {
        className: document.getElementById('classNameInput').value.trim(),
        semester: document.getElementById('classSemesterInput').value.trim(),
        departmentId: parseInt(document.getElementById('classDeptSelect').value)
    };

    try {
        const method = id ? 'PUT' : 'POST';
        const endpoint = id ? `/classes/${id}` : '/classes';
        const res = await apiCall(endpoint, method, body);
        if (res.success) {
            showToast(`Class ${id ? 'updated' : 'created'} successfully`, 'success');
            bootstrap.Modal.getInstance(document.getElementById('classModal')).hide();
            loadClasses();
            loadAdminStats();
            loadDropdownOptions();
        }
    } catch (err) {
        showToast('Save failed: ' + err.message, 'danger');
    }
});

// -------------------------------------------------------------
// SUBJECTS CRUD
// -------------------------------------------------------------
async function loadSubjects() {
    try {
        const res = await apiCall('/subjects');
        const tbody = document.getElementById('subjectsTableBody');
        tbody.innerHTML = '';

        if (res.success && res.data.length > 0) {
            res.data.forEach(sub => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="fw-bold text-info">${sub.subjectCode}</td>
                    <td>${sub.subjectName}</td>
                    <td><span class="badge bg-secondary">${sub.credits} Credits</span></td>
                    <td>${sub.className || 'Unassigned'}</td>
                    <td>${sub.teacherName || '<span class="text-danger">Unassigned</span>'}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary me-1" onclick="editSubject(${sub.id})"><i class="fa-solid fa-pen"></i></button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteSubject(${sub.id})"><i class="fa-solid fa-trash"></i></button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">No subjects found.</td></tr>';
        }
    } catch (err) {
        showToast('Error loading subjects: ' + err.message, 'danger');
    }
}

function openSubjectModal(sub = null) {
    const modal = new bootstrap.Modal(document.getElementById('subjectModal'));
    document.getElementById('subjectForm').reset();
    document.getElementById('subjectEditId').value = '';

    if (sub) {
        document.getElementById('subjectModalTitle').textContent = 'Edit Subject';
        document.getElementById('subjectEditId').value = sub.id;
        document.getElementById('subjectCodeInput').value = sub.subjectCode;
        document.getElementById('subjectNameInput').value = sub.subjectName;
        document.getElementById('subjectCreditsInput').value = sub.credits || 4;
        document.getElementById('subjectClassSelect').value = sub.classId || '';
        document.getElementById('subjectTeacherSelect').value = sub.teacherId || '';
    } else {
        document.getElementById('subjectModalTitle').textContent = 'Add Subject';
    }
    modal.show();
}

async function editSubject(id) {
    try {
        const res = await apiCall(`/subjects/${id}`);
        if (res.success) openSubjectModal(res.data);
    } catch (err) {
        showToast('Failed to fetch subject details', 'danger');
    }
}

async function deleteSubject(id) {
    if (!confirm('Are you sure you want to delete this subject?')) return;
    try {
        const res = await apiCall(`/subjects/${id}`, 'DELETE');
        if (res.success) {
            showToast('Subject deleted successfully', 'success');
            loadSubjects();
            loadAdminStats();
        }
    } catch (err) {
        showToast('Delete failed: ' + err.message, 'danger');
    }
}

document.getElementById('subjectForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('subjectEditId').value;
    const body = {
        subjectCode: document.getElementById('subjectCodeInput').value.trim(),
        subjectName: document.getElementById('subjectNameInput').value.trim(),
        credits: parseInt(document.getElementById('subjectCreditsInput').value),
        classId: document.getElementById('subjectClassSelect').value ? parseInt(document.getElementById('subjectClassSelect').value) : null,
        teacherId: document.getElementById('subjectTeacherSelect').value ? parseInt(document.getElementById('subjectTeacherSelect').value) : null
    };

    try {
        const method = id ? 'PUT' : 'POST';
        const endpoint = id ? `/subjects/${id}` : '/subjects';
        const res = await apiCall(endpoint, method, body);
        if (res.success) {
            showToast(`Subject ${id ? 'updated' : 'created'} successfully`, 'success');
            bootstrap.Modal.getInstance(document.getElementById('subjectModal')).hide();
            loadSubjects();
            loadAdminStats();
        }
    } catch (err) {
        showToast('Save failed: ' + err.message, 'danger');
    }
});

// -------------------------------------------------------------
// USER MANAGEMENT
// -------------------------------------------------------------
async function loadUsers() {
    try {
        const res = await apiCall('/users');
        const tbody = document.getElementById('usersTableBody');
        tbody.innerHTML = '';

        if (res.success && res.data.length > 0) {
            res.data.forEach(u => {
                const tr = document.createElement('tr');
                const badgeClass = u.enabled ? 'bg-success' : 'bg-danger';
                const statusText = u.enabled ? 'Enabled' : 'Disabled';
                const toggleText = u.enabled ? 'Disable' : 'Enable';
                const toggleClass = u.enabled ? 'btn-outline-danger' : 'btn-outline-success';

                tr.innerHTML = `
                    <td class="fw-bold">${u.id}</td>
                    <td>${u.username}</td>
                    <td>${u.email}</td>
                    <td><span class="badge bg-secondary">${u.role}</span></td>
                    <td><span class="badge ${badgeClass}">${statusText}</span></td>
                    <td>
                        <button class="btn btn-sm ${toggleClass}" onclick="toggleUserStatus(${u.id}, ${!u.enabled})">
                            ${toggleText}
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">No users found.</td></tr>';
        }
    } catch (err) {
        showToast('Error loading users: ' + err.message, 'danger');
    }
}

async function toggleUserStatus(userId, newStatus) {
    try {
        const res = await apiCall(`/users/${userId}/status`, 'PATCH', { enabled: newStatus });
        if (res.success) {
            showToast(res.message, 'success');
            loadUsers();
        }
    } catch (err) {
        showToast('Status update failed: ' + err.message, 'danger');
    }
}
