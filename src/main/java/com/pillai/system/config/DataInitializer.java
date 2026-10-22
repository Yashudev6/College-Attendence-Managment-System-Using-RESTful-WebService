package com.pillai.system.config;

import com.pillai.system.entity.*;
import com.pillai.system.repository.*;
import com.pillai.system.service.GradingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GradingService gradingService;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            logger.info("Database already contains data. Skipping DataInitializer seed.");
            return;
        }

        logger.info("Initializing Pillai System database with sample seed data...");

        // 1. Admin User & Profile
        User adminUser = new User("admin", "admin@pillai.edu", passwordEncoder.encode("admin123"), Role.ADMIN);
        userRepository.save(adminUser);

        Admin admin = new Admin("System Administrator", "admin@pillai.edu", adminUser);
        adminRepository.save(admin);

        // 2. Departments
        Department csDept = new Department("Computer Science", "Department of Computer Engineering and Data Science");
        Department itDept = new Department("Information Technology", "Department of Information Technology and Systems");
        departmentRepository.save(csDept);
        departmentRepository.save(itDept);

        // 3. Classes
        SchoolClass csClass = new SchoolClass("CS-Sem1", "Semester 1", csDept);
        SchoolClass itClass = new SchoolClass("IT-Sem1", "Semester 1", itDept);
        classRepository.save(csClass);
        classRepository.save(itClass);

        // 4. Teachers
        User tUser1 = new User("teacher1", "dr.sharma@pillai.edu", passwordEncoder.encode("teacher123"), Role.TEACHER);
        User tUser2 = new User("teacher2", "prof.verma@pillai.edu", passwordEncoder.encode("teacher123"), Role.TEACHER);
        userRepository.save(tUser1);
        userRepository.save(tUser2);

        Teacher teacher1 = new Teacher("TCH001", "Dr. Rajesh Sharma", "dr.sharma@pillai.edu", "+91 98201 11223", csDept, tUser1);
        Teacher teacher2 = new Teacher("TCH002", "Prof. Anita Verma", "prof.verma@pillai.edu", "+91 98202 33445", itDept, tUser2);
        teacherRepository.save(teacher1);
        teacherRepository.save(teacher2);

        // 5. Subjects
        Subject sub1 = new Subject("CS101", "Data Structures & Algorithms", 4, csClass, teacher1);
        Subject sub2 = new Subject("CS102", "Java Programming", 4, csClass, teacher1);
        Subject sub3 = new Subject("CS103", "Database Management Systems", 3, csClass, teacher2);
        Subject sub4 = new Subject("IT101", "Web Technologies", 4, itClass, teacher2);
        Subject sub5 = new Subject("IT102", "Operating Systems", 3, itClass, teacher1);
        subjectRepository.save(sub1);
        subjectRepository.save(sub2);
        subjectRepository.save(sub3);
        subjectRepository.save(sub4);
        subjectRepository.save(sub5);

        // 6. 10 Students
        String[] studentNames = {
                "Aarav Patel", "Ananya Iyer", "Rohan Mehta", "Sanya Gupta", "Vikram Singh",
                "Neha Kapoor", "Aditya Nair", "Priya Joshi", "Karan Malhotra", "Riya Deshmukh"
        };

        Student[] students = new Student[10];

        for (int i = 0; i < 10; i++) {
            String studentNum = "STU00" + (i + 1);
            if (i >= 9) studentNum = "STU0" + (i + 1);
            String username = "student" + (i + 1);
            String email = username + "@pillai.edu";

            User sUser = new User(username, email, passwordEncoder.encode("student123"), Role.STUDENT);
            userRepository.save(sUser);

            SchoolClass targetClass = (i % 2 == 0) ? csClass : itClass;
            Department targetDept = (i % 2 == 0) ? csDept : itDept;

            Student student = new Student(
                    studentNum,
                    studentNames[i],
                    email,
                    "+91 99000 " + (10000 + i),
                    LocalDate.of(2002, 1 + (i % 12), 10 + (i % 15)),
                    (i % 2 == 0) ? "Male" : "Female",
                    "Navi Mumbai, Maharashtra, India",
                    targetClass,
                    targetDept,
                    sUser
            );
            studentRepository.save(student);
            students[i] = student;
        }

        // 7. Attendance Records
        LocalDate baseDate = LocalDate.now().minusDays(10);
        Subject[] csSubjects = {sub1, sub2, sub3};

        for (int day = 0; day < 5; day++) {
            LocalDate date = baseDate.plusDays(day);
            for (int sIdx = 0; sIdx < 10; sIdx++) {
                Student st = students[sIdx];
                Subject subj = (sIdx % 2 == 0) ? csSubjects[day % csSubjects.length] : sub4;

                // Intentionally create one low-attendance student (Student 3 - Rohan Mehta)
                AttendanceStatus status;
                if (sIdx == 2 && day > 0) {
                    status = AttendanceStatus.ABSENT; // Rohan has low attendance (<75%)
                } else if ((sIdx + day) % 7 == 0) {
                    status = AttendanceStatus.LATE;
                } else if ((sIdx + day) % 5 == 0) {
                    status = AttendanceStatus.ABSENT;
                } else {
                    status = AttendanceStatus.PRESENT;
                }

                Attendance att = new Attendance(st, subj, date, status, status == AttendanceStatus.PRESENT ? "Present in lecture" : "Logged in portal");
                attendanceRepository.save(att);
            }
        }

        // 8. Result Records
        for (int i = 0; i < 10; i++) {
            Student st = students[i];
            Subject targetSub = (i % 2 == 0) ? sub1 : sub4;

            double marks = 85.0 - (i * 4.5);
            if (marks < 35.0) marks = 35.0;
            double maxMarks = 100.0;

            String grade = gradingService.calculateGrade(marks);

            Result r1 = new Result(st, targetSub, marks, maxMarks, "Semester 1", "Midterm Examination", grade, "Good performance");
            resultRepository.save(r1);

            Subject targetSub2 = (i % 2 == 0) ? sub2 : sub5;
            double marks2 = 92.0 - (i * 3.0);
            String grade2 = gradingService.calculateGrade(marks2);
            Result r2 = new Result(st, targetSub2, marks2, maxMarks, "Semester 1", "Final Examination", grade2, "Excellent understanding");
            resultRepository.save(r2);
        }

        logger.info("Sample database initialization completed successfully!");
        logger.info("Demo Login Credentials:");
        logger.info("  ADMIN:   admin / admin123");
        logger.info("  TEACHER: teacher1 / teacher123, teacher2 / teacher123");
        logger.info("  STUDENT: student1 / student123 ... student10 / student123");
    }
}
