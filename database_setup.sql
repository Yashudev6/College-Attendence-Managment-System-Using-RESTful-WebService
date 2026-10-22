-- ===================================================================
-- Pillai System - Complete MySQL Database Schema Script
-- Database Name: pillai_system_db
-- MySQL Version: 8.0+
-- ===================================================================

CREATE DATABASE IF NOT EXISTS `pillai_system_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `pillai_system_db`;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(20) NOT NULL,
    `enabled` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Departments Table
CREATE TABLE IF NOT EXISTS `departments` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `department_name` VARCHAR(100) NOT NULL UNIQUE,
    `description` TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Classes Table
CREATE TABLE IF NOT EXISTS `classes` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `class_name` VARCHAR(50) NOT NULL,
    `semester` VARCHAR(20) NOT NULL,
    `department_id` BIGINT,
    CONSTRAINT `fk_class_dept` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Admins Table
CREATE TABLE IF NOT EXISTS `admins` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `user_id` BIGINT UNIQUE,
    CONSTRAINT `fk_admin_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Teachers Table
CREATE TABLE IF NOT EXISTS `teachers` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `teacher_id` VARCHAR(50) NOT NULL UNIQUE,
    `name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `phone` VARCHAR(20),
    `department_id` BIGINT,
    `user_id` BIGINT UNIQUE,
    CONSTRAINT `fk_teacher_dept` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_teacher_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Students Table
CREATE TABLE IF NOT EXISTS `students` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `student_id` VARCHAR(50) NOT NULL UNIQUE,
    `name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `phone` VARCHAR(20),
    `date_of_birth` DATE,
    `gender` VARCHAR(10),
    `address` TEXT,
    `class_id` BIGINT,
    `department_id` BIGINT,
    `user_id` BIGINT UNIQUE,
    CONSTRAINT `fk_student_class` FOREIGN KEY (`class_id`) REFERENCES `classes` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_student_dept` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Subjects Table
CREATE TABLE IF NOT EXISTS `subjects` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `subject_code` VARCHAR(20) NOT NULL UNIQUE,
    `subject_name` VARCHAR(100) NOT NULL,
    `credits` INT DEFAULT 3,
    `class_id` BIGINT,
    `teacher_id` BIGINT,
    CONSTRAINT `fk_subject_class` FOREIGN KEY (`class_id`) REFERENCES `classes` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_subject_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Attendance Table
CREATE TABLE IF NOT EXISTS `attendance` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL,
    `subject_id` BIGINT NOT NULL,
    `date` DATE NOT NULL,
    `status` VARCHAR(20) NOT NULL,
    `remarks` VARCHAR(255),
    CONSTRAINT `uk_student_subject_date` UNIQUE (`student_id`, `subject_id`, `date`),
    CONSTRAINT `fk_attendance_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_attendance_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. Results Table
CREATE TABLE IF NOT EXISTS `results` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL,
    `subject_id` BIGINT NOT NULL,
    `marks` DOUBLE NOT NULL,
    `max_marks` DOUBLE NOT NULL,
    `semester` VARCHAR(20) NOT NULL,
    `exam_type` VARCHAR(50) NOT NULL,
    `grade` VARCHAR(5),
    `remarks` VARCHAR(255),
    CONSTRAINT `fk_result_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_result_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
