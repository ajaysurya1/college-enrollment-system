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

CREATE TABLE departments (
    department_id INT PRIMARY KEY AUTO_INCREMENT,
    department_code VARCHAR(20) UNIQUE NOT NULL,
    department_name VARCHAR(100) NOT NULL
);

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

CREATE TABLE students (
    user_id INT PRIMARY KEY,
    roll_number VARCHAR(30) UNIQUE NOT NULL,
    programme VARCHAR(50) NOT NULL,
    year_of_study INT NOT NULL,
    CONSTRAINT chk_student_year CHECK (year_of_study > 0),
    CONSTRAINT fk_students_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE faculty (
    user_id INT PRIMARY KEY,
    designation VARCHAR(50) NOT NULL,
    CONSTRAINT fk_faculty_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

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

CREATE TABLE semesters (
    semester_id INT PRIMARY KEY AUTO_INCREMENT,
    semester_name VARCHAR(50) NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    registration_start DATE NOT NULL,
    registration_end DATE NOT NULL
);

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

CREATE TABLE course_prerequisites (
    course_id INT NOT NULL,
    prerequisite_course_id INT NOT NULL,
    PRIMARY KEY (course_id, prerequisite_course_id),
    CONSTRAINT chk_prereq_not_self CHECK (course_id <> prerequisite_course_id),
    CONSTRAINT fk_prereq_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    CONSTRAINT fk_prereq_required FOREIGN KEY (prerequisite_course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);

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

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_courses_dept ON courses(department_id);
CREATE INDEX idx_offerings_course ON course_offerings(course_id);
CREATE INDEX idx_offerings_semester ON course_offerings(semester_id);
CREATE INDEX idx_offerings_faculty ON course_offerings(faculty_id);
CREATE INDEX idx_enrollments_student ON enrollments(student_id);
CREATE INDEX idx_enrollments_offering ON enrollments(offering_id);
CREATE INDEX idx_enrollments_status ON enrollments(status);
