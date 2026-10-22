# Pillai System - Student Attendance & Result Management System

A full-stack, enterprise-grade **Student Attendance & Result Management System** built with **Spring Boot**, **Spring Security**, **JWT Authentication**, **Hibernate / JPA**, **MySQL 8+**, and a modern **Bootstrap 5** frontend dashboard.

---

## 1. Project Overview
**Pillai System** is a complete multi-role web application designed for academic institutions to manage students, faculty, departments, classes, subjects, daily attendance, and examination results.

The system features real-time REST API integration, automatic attendance percentage calculation `(Present / Total) × 100`, low-attendance warning alerts (<75%), configurable letter grade assignment (A+, A, B+, B, C, D, F), role-based access control (ADMIN, TEACHER, STUDENT), and Apache NetBeans IDE compatibility.

---

## 2. Key Features

### Admin Panel (`/admin.html`)
- **Dashboard Statistics**: Real-time totals for Students, Teachers, Subjects, Classes, Attendance Records, Results, and Low-Attendance Warnings (<75%).
- **Student CRUD**: Create, Update, Delete, View, and Search students by name, ID, or email.
- **Teacher CRUD**: Create, Update, Delete, and View faculty members assigned to departments.
- **Department CRUD**: Manage institutional departments.
- **Class CRUD**: Manage classes and academic semesters.
- **Subject & Faculty Assignment**: Create subjects, assign credits, link to classes, and assign faculty teachers.
- **User Account Governance**: Enable or disable user access status instantly.

### Teacher Panel (`/teacher.html`)
- **Faculty Profile**: View assigned department, classes, and subjects.
- **Class Roster Inspection**: View student rosters per class.
- **Attendance Management**: Mark daily attendance (`PRESENT`, `ABSENT`, `LATE`), update records, and view attendance logs.
- **Marks & Results Entry**: Enter student examination marks, update marks, and view grade sheets with automated letter grade calculation.

### Student Panel (`/student.html`)
- **Personal Profile**: Enrolled class, department, contact info, and subject list.
- **Attendance Performance**: View overall attendance percentage, attendance breakdown, and low attendance warning alerts (<75%).
- **Examination Transcript**: View subject-wise marks, percentage, assigned grades (A+, A, B+, B, C, D, F), overall cumulative percentage, and overall grade.

---

## 3. Technology Stack

### Backend
- **Java 17** (OpenJDK)
- **Spring Boot 3.2.3**
- **Spring Web** (RESTful APIs)
- **Spring Data JPA** & **Hibernate**
- **Spring Security** (Stateless REST protection, BCrypt password encoder)
- **JSON Web Tokens (JWT)** (`jjwt 0.11.5`)
- **Maven**

### Database
- **MySQL 8+** (Relational normalized database with FK constraints)
- **H2 Database** (Configured in test profile for instant standalone testing)

### Frontend
- **HTML5**, **CSS3**, **JavaScript (ES6 Fetch API)**
- **Bootstrap 5.3.2**
- **FontAwesome 6.4.2** & **Google Fonts (Inter)**

---

## 4. System Architecture

```
                                  [ Browser / Client ]
                                           │
                                  [ Static Front-End ]
                   (index.html | admin.html | teacher.html | student.html)
                                           │
                                     REST APIs (JSON)
                                           │
                           ┌───────────────┴───────────────┐
                           ▼                               ▼
                 [ AuthController ]              [ Entity Controllers ]
                           │                               │
                [ Spring Security & JWT ]        [ DTO & Service Layer ]
                           │                               │
                           └───────────────┬───────────────┘
                                           ▼
                                [ Spring Data JPA Repos ]
                                           │
                                           ▼
                                   [ MySQL 8 Database ]
```

---

## 5. Database Setup & MySQL Configuration

### Step 1: Create Database in MySQL
Execute the provided SQL script `database_setup.sql` or create manually in MySQL:
```sql
CREATE DATABASE pillai_system_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Step 2: Configure Credentials in `application.properties`
Located at `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/pillai_system_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```
*You can also set environment variables `DB_USERNAME` and `DB_PASSWORD`.*

---

## 6. How to Run the Application

### Option A: Using Command Line (Maven Wrapper)
No external Maven installation required!

1. Open terminal in project root folder (`c:\Users\Yash\Desktop\pillaisys`).
2. Run test suite:
   ```bash
   mvnw.cmd clean test
   ```
3. Run Spring Boot Application:
   ```bash
   mvnw.cmd spring-boot:run
   ```
4. Access application in browser:
   **`http://localhost:8080`**

### Option B: Using Apache NetBeans IDE
1. Open **Apache NetBeans**.
2. Go to **File -> Open Project**.
3. Browse to `c:\Users\Yash\Desktop\pillaisys` and select the project.
4. NetBeans will automatically detect the standard Maven `pom.xml`.
5. Right-click project -> **Clean and Build**.
6. Right-click `PillaiSystemApplication.java` -> **Run File** (or click the green **Run** button).

---

## 7. Demo Login Credentials

Upon application startup, sample seed data (`DataInitializer`) automatically populates the database:

| Role | Username | Password | Profile Name | Access Rights |
| :--- | :--- | :--- | :--- | :--- |
| **ADMIN** | `admin` | `admin123` | System Administrator | Full System Access & User Governance |
| **TEACHER** | `teacher1` | `teacher123` | Dr. Rajesh Sharma | Attendance & Result Management |
| **TEACHER** | `teacher2` | `teacher123` | Prof. Anita Verma | Attendance & Result Management |
| **STUDENT** | `student1` | `student123` | Aarav Patel | Read-Only Own Attendance & Marks |
| **STUDENT** | `student2` | `student123` | Ananya Iyer | Read-Only Own Attendance & Marks |
| **STUDENT** | `student3` | `student123` | Rohan Mehta *(Low Att)* | Read-Only Own Attendance & Marks |

---

## 8. REST API Endpoints

### Authentication APIs
- `POST /api/auth/login` - Authenticate user & get JWT token
- `POST /api/auth/register` - Register user profile
- `POST /api/auth/logout` - Invalidate session client-side

### Student APIs
- `GET /api/students` - List all students / search (`?search=query`)
- `GET /api/students/{id}` - Get student details
- `GET /api/students/me` - Get current student profile
- `POST /api/students` - Create student
- `PUT /api/students/{id}` - Update student
- `PATCH /api/students/{id}` - Patch student fields
- `DELETE /api/students/{id}` - Delete student

### Teacher APIs
- `GET /api/teachers` - List all teachers
- `GET /api/teachers/{id}` - Get teacher details
- `POST /api/teachers` - Create teacher
- `PUT /api/teachers/{id}` - Update teacher
- `PATCH /api/teachers/{id}` - Patch teacher fields
- `DELETE /api/teachers/{id}` - Delete teacher

### Department & Class APIs
- `GET / POST / PUT / DELETE /api/departments`
- `GET / POST / PUT / DELETE /api/classes`

### Subject APIs
- `GET / POST / PUT / DELETE /api/subjects`
- `GET /api/subjects/class/{classId}`
- `GET /api/subjects/teacher/{teacherId}`

### Attendance APIs
- `GET /api/attendance` - List all attendance records
- `GET /api/attendance/student/{studentId}` - Get student attendance
- `GET /api/attendance/summary/student/{studentId}` - Get attendance % summary
- `GET /api/attendance/low-attendance` - List students with <75% attendance
- `POST /api/attendance` - Mark attendance
- `PUT /api/attendance/{id}` - Update attendance
- `PATCH /api/attendance/{id}` - Patch status/remarks
- `DELETE /api/attendance/{id}` - Delete attendance record

### Result APIs
- `GET /api/results` - List all results
- `GET /api/results/student/{studentId}` - Get student results
- `GET /api/results/summary/student/{studentId}` - Get overall result transcript
- `POST /api/results` - Enter student exam marks
- `PUT /api/results/{id}` - Update exam marks
- `PATCH /api/results/{id}` - Patch marks
- `DELETE /api/results/{id}` - Delete result record

### User Governance & Dashboard Stats
- `GET /api/users` - List user accounts
- `PATCH /api/users/{id}/status` - Enable/Disable user access
- `GET /api/dashboard/stats` - Admin statistics

---

## 9. Postman Collection
Import `postman/Pillai_System_API.postman_collection.json` into Postman. See `postman/README.md` for authorization header configuration instructions.

---

## 10. Database ER Diagram Overview

- **User (1) ── (0..1) Student**: Links login credentials to student profile.
- **User (1) ── (0..1) Teacher**: Links login credentials to faculty profile.
- **User (1) ── (0..1) Admin**: Links login credentials to administrator.
- **Department (1) ── (N) Class**: Department contains multiple classes.
- **Department (1) ── (N) Teacher**: Teachers belong to a department.
- **Class (1) ── (N) Student**: Students belong to an enrolled class.
- **Class (1) ── (N) Subject**: Subjects belong to a class curriculum.
- **Teacher (1) ── (N) Subject**: Faculty teachers teach multiple subjects.
- **Student (1) ── (N) Attendance**: Daily attendance entries per student & subject.
- **Student (1) ── (N) Result**: Examination grade records per student & subject.

---

## 11. Troubleshooting

1. **MySQL Access Denied Error**:
   Ensure `DB_USERNAME` and `DB_PASSWORD` match your local MySQL configuration in `application.properties`.
2. **Port 8080 Already in Use**:
   Change `server.port=8081` in `application.properties`.
3. **JWT Expiration / 401 Unauthorized**:
   Log out and sign back in to issue a fresh JWT token.
