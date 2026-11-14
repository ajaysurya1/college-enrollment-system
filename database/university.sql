-- ========================================================
-- University Academic & Course Enrollment Management System
-- Database Schema & Initial Data Seed
-- ========================================================

CREATE DATABASE IF NOT EXISTS university_academic_system;
USE university_academic_system;

DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS course_prerequisites;
DROP TABLE IF EXISTS course_offerings;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS semesters;
DROP TABLE IF EXISTS faculty;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS departments;

-- 1. Departments Table
CREATE TABLE departments (
    department_id INT PRIMARY KEY AUTO_INCREMENT,
    department_code VARCHAR(20) UNIQUE NOT NULL,
    department_name VARCHAR(100) NOT NULL
);

-- 2. Users Table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('ADMIN', 'STUDENT', 'FACULTY') NOT NULL,
    department_id INT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_users_department FOREIGN KEY (department_id) REFERENCES departments(department_id) ON DELETE SET NULL
);

-- 3. Students Table (Inherits User)
CREATE TABLE students (
    user_id INT PRIMARY KEY,
    roll_number VARCHAR(30) UNIQUE NOT NULL,
    programme VARCHAR(50) NOT NULL,
    year_of_study INT NOT NULL,
    CONSTRAINT chk_student_year CHECK (year_of_study > 0),
    CONSTRAINT fk_students_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 4. Faculty Table (Inherits User)
CREATE TABLE faculty (
    user_id INT PRIMARY KEY,
    designation VARCHAR(50) NOT NULL,
    CONSTRAINT fk_faculty_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 5. Courses Table
CREATE TABLE courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    course_title VARCHAR(150) NOT NULL,
    credits INT NOT NULL,
    department_id INT NOT NULL,
    description TEXT,
    CONSTRAINT chk_course_credits CHECK (credits > 0),
    CONSTRAINT fk_courses_department FOREIGN KEY (department_id) REFERENCES departments(department_id)
);

-- 6. Semesters Table
CREATE TABLE semesters (
    semester_id INT PRIMARY KEY AUTO_INCREMENT,
    semester_name VARCHAR(50) NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    registration_start DATE NOT NULL,
    registration_end DATE NOT NULL
);

-- 7. Course Offerings Table
CREATE TABLE course_offerings (
    offering_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    semester_id INT NOT NULL,
    faculty_id INT NULL,
    section VARCHAR(10) NOT NULL,
    capacity INT NOT NULL,
    CONSTRAINT chk_offering_capacity CHECK (capacity > 0),
    CONSTRAINT uq_course_semester_section UNIQUE (course_id, semester_id, section),
    CONSTRAINT fk_offerings_course FOREIGN KEY (course_id) REFERENCES courses(course_id),
    CONSTRAINT fk_offerings_semester FOREIGN KEY (semester_id) REFERENCES semesters(semester_id),
    CONSTRAINT fk_offerings_faculty FOREIGN KEY (faculty_id) REFERENCES faculty(user_id) ON DELETE SET NULL
);

-- 8. Course Prerequisites Table
CREATE TABLE course_prerequisites (
    course_id INT NOT NULL,
    prerequisite_course_id INT NOT NULL,
    PRIMARY KEY (course_id, prerequisite_course_id),
    CONSTRAINT chk_prereq_not_self CHECK (course_id <> prerequisite_course_id),
    CONSTRAINT fk_prereq_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    CONSTRAINT fk_prereq_required FOREIGN KEY (prerequisite_course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);

-- 9. Enrollments Table (Transactional)
CREATE TABLE enrollments (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    offering_id INT NOT NULL,
    enrollment_date DATE NOT NULL,
    status ENUM('ENROLLED', 'DROPPED', 'COMPLETED') NOT NULL DEFAULT 'ENROLLED',
    grade VARCHAR(5) NULL,
    CONSTRAINT uq_student_offering UNIQUE (student_id, offering_id),
    CONSTRAINT fk_enrollments_student FOREIGN KEY (student_id) REFERENCES students(user_id),
    CONSTRAINT fk_enrollments_offering FOREIGN KEY (offering_id) REFERENCES course_offerings(offering_id)
);

-- Indexes for performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_courses_dept ON courses(department_id);
CREATE INDEX idx_offerings_course ON course_offerings(course_id);
CREATE INDEX idx_offerings_semester ON course_offerings(semester_id);
CREATE INDEX idx_offerings_faculty ON course_offerings(faculty_id);
CREATE INDEX idx_enrollments_student ON enrollments(student_id);
CREATE INDEX idx_enrollments_offering ON enrollments(offering_id);
CREATE INDEX idx_enrollments_status ON enrollments(status);

-- ========================================================
-- Seed Data
-- ========================================================

INSERT INTO departments (department_id, department_code, department_name) VALUES
(1, 'CS', 'Computer Science and Engineering'),
(2, 'MATH', 'Mathematics'),
(3, 'EE', 'Electrical Engineering');

INSERT INTO users (user_id, username, password_hash, full_name, email, role, department_id, is_active) VALUES
(1, 'admin', '$2a$10$uy1i/2ryPL3dSa2cDmx99uLkhkIjkVZOWnnEHrsyPWumNHkI/ohii', 'System Administrator', 'admin@university.edu', 'ADMIN', 1, TRUE),
(2, 'prof.smith', '$2a$10$QjmeBmqAvygkHclaXGqqces1wSFhscH388jWeBESAs.Debt0/P7Xi', 'Prof. John Smith', 'john.smith@university.edu', 'FACULTY', 1, TRUE),
(3, 'dr.watson', '$2a$10$QjmeBmqAvygkHclaXGqqces1wSFhscH388jWeBESAs.Debt0/P7Xi', 'Dr. Emma Watson', 'emma.watson@university.edu', 'FACULTY', 2, TRUE),
(4, 'prof.alan', '$2a$10$QjmeBmqAvygkHclaXGqqces1wSFhscH388jWeBESAs.Debt0/P7Xi', 'Prof. Alan Turing', 'alan.turing@university.edu', 'FACULTY', 3, TRUE),
(5, 'student.alice', '$2a$10$KQ2ye8zf7r1L8PEbe6iavumwLi9XpBXJZdqpP4n2xfeuIcXphNO4y', 'Alice Johnson', 'alice.johnson@university.edu', 'STUDENT', 1, TRUE),
(6, 'student.bob', '$2a$10$KQ2ye8zf7r1L8PEbe6iavumwLi9XpBXJZdqpP4n2xfeuIcXphNO4y', 'Bob Williams', 'bob.williams@university.edu', 'STUDENT', 1, TRUE),
(7, 'student.charlie', '$2a$10$KQ2ye8zf7r1L8PEbe6iavumwLi9XpBXJZdqpP4n2xfeuIcXphNO4y', 'Charlie Brown', 'charlie.brown@university.edu', 'STUDENT', 2, TRUE),
(8, 'student.diana', '$2a$10$KQ2ye8zf7r1L8PEbe6iavumwLi9XpBXJZdqpP4n2xfeuIcXphNO4y', 'Diana Prince', 'diana.prince@university.edu', 'STUDENT', 3, TRUE),
(9, 'student.evan', '$2a$10$KQ2ye8zf7r1L8PEbe6iavumwLi9XpBXJZdqpP4n2xfeuIcXphNO4y', 'Evan Wright', 'evan.wright@university.edu', 'STUDENT', 1, TRUE);

INSERT INTO faculty (user_id, designation) VALUES
(2, 'Associate Professor'),
(3, 'Assistant Professor'),
(4, 'Professor');

INSERT INTO students (user_id, roll_number, programme, year_of_study) VALUES
(5, 'CS2025001', 'B.Tech Computer Science', 2),
(6, 'CS2024002', 'B.Tech Computer Science', 3),
(7, 'MA2026003', 'B.Sc Mathematics', 1),
(8, 'EE2025004', 'B.Tech Electrical Engineering', 2),
(9, 'CS2026005', 'B.Tech Computer Science', 1);

INSERT INTO courses (course_id, course_code, course_title, credits, department_id, description) VALUES
(1, 'CS101', 'Introduction to Programming', 4, 1, 'Fundamental concepts of programming using Java and basic algorithms.'),
(2, 'CS201', 'Data Structures and Algorithms', 4, 1, 'Stacks, queues, linked lists, trees, graphs, sorting and searching.'),
(3, 'CS301', 'Database Management Systems', 4, 1, 'Relational models, SQL queries, normalization, transactions and JDBC.'),
(4, 'MATH101', 'Calculus I', 3, 2, 'Differential and integral calculus with engineering applications.'),
(5, 'EE101', 'Basic Circuit Analysis', 3, 3, 'Kirchhoff laws, nodal analysis, AC/DC circuit behaviors and theorems.');

INSERT INTO course_prerequisites (course_id, prerequisite_course_id) VALUES
(2, 1),
(3, 2);

INSERT INTO semesters (semester_id, semester_name, academic_year, start_date, end_date, registration_start, registration_end) VALUES
(1, 'Fall 2026', '2026-2027', '2026-08-01', '2026-12-15', '2026-07-01', '2026-08-10'),
(2, 'Spring 2027', '2026-2027', '2027-01-10', '2027-05-25', '2026-10-01', '2027-03-01');

INSERT INTO course_offerings (offering_id, course_id, semester_id, faculty_id, section, capacity) VALUES
(1, 1, 1, 2, 'A', 30),
(2, 1, 2, 2, 'A', 35),
(3, 2, 2, 2, 'A', 30),
(4, 3, 2, 2, 'A', 25),
(5, 4, 2, 3, 'A', 40),
(6, 5, 2, 4, 'A', 30);

INSERT INTO enrollments (enrollment_id, student_id, offering_id, enrollment_date, status, grade) VALUES
(1, 5, 1, '2026-08-05', 'COMPLETED', 'A'),
(2, 6, 1, '2026-08-05', 'COMPLETED', 'A'),
(3, 8, 1, '2026-08-05', 'COMPLETED', 'B+');
