# Database Management System (DBMS) Implementation Document

This document outlines the database design, relational schema, normalization, constraints, SQL queries, transactions, and JDBC integration in the **University Academic & Course Enrollment System**.

---

## 1. Database Schema & Tables

The system uses MySQL database `university_academic_system` with nine tables:

### 1. `departments`
- `department_id` INT AUTO_INCREMENT PRIMARY KEY
- `department_code` VARCHAR(20) UNIQUE NOT NULL
- `department_name` VARCHAR(100) NOT NULL

### 2. `users` (Shared Account Table)
- `user_id` INT AUTO_INCREMENT PRIMARY KEY
- `username` VARCHAR(50) UNIQUE NOT NULL
- `password_hash` VARCHAR(255) NOT NULL
- `full_name` VARCHAR(100) NOT NULL
- `email` VARCHAR(100) UNIQUE NOT NULL
- `role` ENUM('ADMIN', 'STUDENT', 'FACULTY') NOT NULL
- `department_id` INT NULL, FOREIGN KEY REFERENCES `departments(department_id)` ON DELETE SET NULL
- `is_active` BOOLEAN NOT NULL DEFAULT TRUE

### 3. `students` (Table Inheritance Sub-table)
- `user_id` INT PRIMARY KEY, FOREIGN KEY REFERENCES `users(user_id)` ON DELETE CASCADE
- `roll_number` VARCHAR(30) UNIQUE NOT NULL
- `programme` VARCHAR(50) NOT NULL
- `year_of_study` INT NOT NULL, CHECK (`year_of_study` > 0)

### 4. `faculty` (Table Inheritance Sub-table)
- `user_id` INT PRIMARY KEY, FOREIGN KEY REFERENCES `users(user_id)` ON DELETE CASCADE
- `designation` VARCHAR(50) NOT NULL

### 5. `courses`
- `course_id` INT AUTO_INCREMENT PRIMARY KEY
- `course_code` VARCHAR(20) UNIQUE NOT NULL
- `course_title` VARCHAR(150) NOT NULL
- `credits` INT NOT NULL, CHECK (`credits` > 0)
- `department_id` INT NOT NULL, FOREIGN KEY REFERENCES `departments(department_id)`
- `description` TEXT

### 6. `semesters`
- `semester_id` INT AUTO_INCREMENT PRIMARY KEY
- `semester_name` VARCHAR(50) NOT NULL
- `academic_year` VARCHAR(20) NOT NULL
- `start_date` DATE NOT NULL
- `end_date` DATE NOT NULL
- `registration_start` DATE NOT NULL
- `registration_end` DATE NOT NULL

### 7. `course_offerings`
- `offering_id` INT AUTO_INCREMENT PRIMARY KEY
- `course_id` INT NOT NULL, FOREIGN KEY REFERENCES `courses(course_id)`
- `semester_id` INT NOT NULL, FOREIGN KEY REFERENCES `semesters(semester_id)`
- `faculty_id` INT NULL, FOREIGN KEY REFERENCES `faculty(user_id)` ON DELETE SET NULL
- `section` VARCHAR(10) NOT NULL
- `capacity` INT NOT NULL, CHECK (`capacity` > 0)
- **Constraint:** `UNIQUE KEY (course_id, semester_id, section)`

### 8. `course_prerequisites` (Self-Referential Many-to-Many)
- `course_id` INT NOT NULL, FOREIGN KEY REFERENCES `courses(course_id)` ON DELETE CASCADE
- `prerequisite_course_id` INT NOT NULL, FOREIGN KEY REFERENCES `courses(course_id)` ON DELETE CASCADE
- **Primary Key:** `PRIMARY KEY (course_id, prerequisite_course_id)`
- **Constraint:** `CHECK (course_id <> prerequisite_course_id)`

### 9. `enrollments` (Student Course Registrations)
- `enrollment_id` INT AUTO_INCREMENT PRIMARY KEY
- `student_id` INT NOT NULL, FOREIGN KEY REFERENCES `students(user_id)`
- `offering_id` INT NOT NULL, FOREIGN KEY REFERENCES `course_offerings(offering_id)`
- `enrollment_date` DATE NOT NULL
- `status` ENUM('ENROLLED', 'DROPPED', 'COMPLETED') NOT NULL DEFAULT 'ENROLLED'
- `grade` VARCHAR(5) NULL
- **Constraint:** `UNIQUE KEY (student_id, offering_id)`

---

## 2. Relationships & Cardinality

| Entity 1 | Entity 2 | Cardinality | Explanation |
| :--- | :--- | :--- | :--- |
| `departments` | `users` | 1 to Many | One department contains multiple users |
| `departments` | `courses` | 1 to Many | One department offers multiple courses |
| `users` | `students` | 1 to 1 | Sub-table inheritance for student-specific fields |
| `users` | `faculty` | 1 to 1 | Sub-table inheritance for faculty-specific fields |
| `courses` | `course_offerings` | 1 to Many | One course is offered in multiple semesters/sections |
| `semesters` | `course_offerings` | 1 to Many | One semester holds many course offerings |
| `faculty` | `course_offerings` | 1 to Many | One faculty can teach multiple offerings |
| `courses` | `courses` (Prereq) | Many to Many | Implemented via `course_prerequisites` bridge table |
| `students` | `course_offerings` | Many to Many | Implemented via `enrollments` bridge table |

---

## 3. Database Normalization Analysis

### First Normal Form (1NF)
- All columns contain atomic, non-decomposable values.
- No repeating groups or multivalued columns (e.g., prerequisites and enrollments are normalized into separate associative tables).
- Every table has a clearly defined primary key.

### Second Normal Form (2NF)
- The schema is in 1NF.
- In all composite-key tables (`course_prerequisites` with `(course_id, prerequisite_course_id)` and `enrollments` unique constraint), there are no partial dependencies where non-key attributes depend on only part of a candidate key.

### Third Normal Form (3NF)
- The schema is in 2NF.
- There are no transitive functional dependencies ($X \rightarrow Y \rightarrow Z$).
- Department metadata (`department_name`) is stored once in `departments` rather than duplicated in `courses` or `users`. Course metadata (`credits`, `course_title`) is stored once in `courses` rather than copied into `course_offerings` or `enrollments`.

---

## 4. SQL Queries & JOIN Operations

### Multi-Table Query: Resolving Enrolled Course Offerings
```sql
SELECT 
    e.enrollment_id, e.student_id, e.offering_id, e.enrollment_date, e.status, e.grade,
    s.roll_number, u.full_name AS student_name, c.course_code, c.course_title, c.credits,
    sem.semester_name, co.section, fac.full_name AS faculty_name
FROM enrollments e
JOIN students s ON e.student_id = s.user_id
JOIN users u ON s.user_id = u.user_id
JOIN course_offerings co ON e.offering_id = co.offering_id
JOIN courses c ON co.course_id = c.course_id
JOIN semesters sem ON co.semester_id = sem.semester_id
LEFT JOIN users fac ON co.faculty_id = fac.user_id
WHERE e.student_id = ?
ORDER BY sem.start_date DESC, c.course_code;
```

### Prerequisite Completion Check Query
```sql
SELECT COUNT(*) FROM enrollments e
JOIN course_offerings co ON e.offering_id = co.offering_id
WHERE e.student_id = ? 
  AND co.course_id = ? 
  AND e.status = 'COMPLETED'
  AND e.grade IS NOT NULL 
  AND e.grade <> 'F';
```

---

## 5. Transaction Management & Concurrency Control

In `EnrollmentDAOImpl.enrollStudent(studentId, offeringId, date)`:
1. **Disable Auto-Commit:**
   ```java
   conn.setAutoCommit(false);
   ```
2. **Pessimistic Row Lock (`SELECT ... FOR UPDATE`):**
   ```sql
   SELECT capacity FROM course_offerings WHERE offering_id = ? FOR UPDATE;
   ```
   Locks the offering row against concurrent updates.
3. **Count Active Registrations:**
   ```sql
   SELECT COUNT(*) FROM enrollments WHERE offering_id = ? AND status = 'ENROLLED';
   ```
4. **Capacity & Duplicate Checks:**
   If `currentEnrolled >= capacity`:
   ```java
   conn.rollback();
   throw new CourseFullException(...);
   ```
5. **Commit Write:**
   ```sql
   INSERT INTO enrollments (student_id, offering_id, enrollment_date, status) VALUES (?, ?, ?, 'ENROLLED');
   ```
   ```java
   conn.commit();
   ```
6. **Rollback on Any SQLException:**
   Guarantees that a failed insert or network disruption will not leave dangling locks or partial records.

---

## 6. Historical Data Preservation

Academic institutions cannot permanently delete student enrollment records because transcript integrity and prerequisite verification require historical completion data.

- **Account Inactivation:** When an admin deletes a student or faculty, the record is flagged `is_active = FALSE` in `users`, preventing login without destroying enrollment references.
- **Course Dropping:** When a student drops a course, `status` is set to `'DROPPED'` instead of executing a `DELETE` query.
- **Completed Courses:** Once a grade is submitted, `status` is set to `'COMPLETED'`.

