# University Academic & Course Enrollment System

A full-featured desktop academic and course enrollment management system built with **Java Swing**, **JDBC**, and **MySQL**, designed for an academic software engineering and DBMS evaluation.

---

## 1. Project Overview & Problem Statement

University academic administration requires coordination across student registrations, course catalogs, prerequisites, class section capacities, and faculty grading. This application provides a native desktop solution featuring role-based access control for **Administrators**, **Faculty**, and **Students**.

The project fulfills all requirements for college-level evaluation, implementing the **six core OOP concepts** (Encapsulation, Inheritance, Polymorphism, Abstraction, Packages, and Exception Handling) alongside a **normalized MySQL relational database** managed via transactional JDBC.

---

## 2. Technology Stack

- **Language:** Java 17+
- **User Interface:** Java Swing (with FlatLaf Light Look & Feel)
- **Database:** MySQL 8.0+
- **Connectivity:** JDBC (Java Database Connectivity) with `PreparedStatement`
- **Security:** BCrypt (`org.mindrot:jbcrypt`) for salted password hashing
- **Build Tool:** Apache Maven
- **Testing:** JUnit 5 (Jupiter)

---

## 3. System Architecture

The application is structured into four distinct layers:

```
+-----------------------------------------------------------+
|                   Presentation Layer                      |
| (Swing Frames: LoginFrame, AdminDashboard, Student, etc.) |
+-----------------------------------------------------------+
                             |
                             v
+-----------------------------------------------------------+
|                     Service Layer                         |
| (AuthService, EnrollmentService, CourseService, etc.)     |
+-----------------------------------------------------------+
                             |
                             v
+-----------------------------------------------------------+
|                   DAO / Repository Layer                  |
| (CourseDAO, StudentDAO, CourseOfferingDAO, etc. + Impls)  |
+-----------------------------------------------------------+
                             |
                             v
+-----------------------------------------------------------+
|                     Database Layer                        |
| (MySQL 8+: 9 Normalized Tables, Constraints, Transactions)|
+-----------------------------------------------------------+
```

---

## 4. Package Structure

```
com.university/
├── Main.java                          # Application entry point
├── config/
│   └── DatabaseConnection.java        # JDBC connection factory
├── model/
│   ├── Person.java                    # Abstract base class
│   ├── Student.java                   # Extends Person
│   ├── Faculty.java                   # Extends Person
│   ├── Administrator.java             # Extends Person
│   ├── Department.java                # Department entity
│   ├── Course.java                    # Course entity
│   ├── Semester.java                  # Semester & registration dates
│   ├── CourseOffering.java            # Course sections & seats
│   └── Enrollment.java                # Student enrollments & grades
├── dao/
│   ├── UserDAO.java
│   ├── DepartmentDAO.java
│   ├── StudentDAO.java
│   ├── FacultyDAO.java
│   ├── CourseDAO.java
│   ├── SemesterDAO.java
│   ├── CourseOfferingDAO.java
│   └── EnrollmentDAO.java
├── dao/impl/
│   ├── UserDAOImpl.java
│   ├── DepartmentDAOImpl.java
│   ├── StudentDAOImpl.java
│   ├── FacultyDAOImpl.java
│   ├── CourseDAOImpl.java
│   ├── SemesterDAOImpl.java
│   ├── CourseOfferingDAOImpl.java
│   └── EnrollmentDAOImpl.java
├── service/
│   ├── AuthService.java
│   ├── DepartmentService.java
│   ├── StudentService.java
│   ├── FacultyService.java
│   ├── CourseService.java
│   ├── SemesterService.java
│   ├── CourseOfferingService.java
│   └── EnrollmentService.java
├── exception/
│   ├── AuthenticationException.java
│   ├── DatabaseException.java
│   ├── EnrollmentException.java
│   ├── CourseFullException.java
│   ├── DuplicateEnrollmentException.java
│   ├── PrerequisiteNotMetException.java
│   └── RegistrationClosedException.java
├── ui/
│   ├── auth/
│   │   └── LoginFrame.java
│   ├── admin/
│   │   └── AdminDashboard.java
│   ├── student/
│   │   └── StudentDashboard.java
│   ├── faculty/
│   │   └── FacultyDashboard.java
│   └── components/
│       └── UITheme.java
└── util/
    ├── PasswordUtil.java              # BCrypt utility
    └── DatabaseSeeder.java            # Database setup utility
```

---

## 5. Database Schema & Tables

The database `university_academic_system` includes 9 relational tables:

1. **`departments`**: `department_id`, `department_code`, `department_name`
2. **`users`**: `user_id`, `username`, `password_hash`, `full_name`, `email`, `role`, `department_id`, `is_active`
3. **`students`**: `user_id` (PK/FK), `roll_number`, `programme`, `year_of_study`
4. **`faculty`**: `user_id` (PK/FK), `designation`
5. **`courses`**: `course_id`, `course_code`, `course_title`, `credits`, `department_id`, `description`
6. **`semesters`**: `semester_id`, `semester_name`, `academic_year`, `start_date`, `end_date`, `registration_start`, `registration_end`
7. **`course_offerings`**: `offering_id`, `course_id`, `semester_id`, `faculty_id`, `section`, `capacity` (Unique across course, semester, section)
8. **`course_prerequisites`**: `course_id`, `prerequisite_course_id` (Composite PK)
9. **`enrollments`**: `enrollment_id`, `student_id`, `offering_id`, `enrollment_date`, `status`, `grade` (Unique across student and offering)

---

## 6. Demo Login Credentials

All passwords are encrypted with BCrypt in the database.

| Role | Username | Password | Full Name / Description |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` | System Administrator |
| **Faculty** | `prof.smith` | `faculty123` | Prof. John Smith (Computer Science) |
| **Faculty** | `dr.watson` | `faculty123` | Dr. Emma Watson (Mathematics) |
| **Faculty** | `prof.alan` | `faculty123` | Prof. Alan Turing (Electrical Eng.) |
| **Student** | `student.alice` | `student123` | Alice Johnson (Roll: `CS2025001`, Completed CS101) |
| **Student** | `student.bob` | `student123` | Bob Williams (Roll: `CS2024002`, Completed CS101, CS201) |
| **Student** | `student.charlie` | `student123` | Charlie Brown (Roll: `MA2026003`, Year 1 Math) |
| **Student** | `student.diana` | `student123` | Diana Prince (Roll: `EE2025004`, Year 2 EE) |
| **Student** | `student.evan` | `student123` | Evan Wright (Roll: `CS2026005`, Year 1 CS - No prereqs completed) |

---

## 7. Setup & Execution Instructions

### Prerequisites
1. Java Development Kit (JDK 17 or higher)
2. Apache Maven 3.8+
3. MySQL Server 8.0+ running on port 3306

### Step 1: Database Configuration
Ensure MySQL is started. You can configure credentials in `src/main/resources/application.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/university_academic_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=your_mysql_password
```
*(Alternatively, set the environment variables `DB_URL`, `DB_USER`, and `DB_PASSWORD`)*.

### Step 2: Initialize Database Tables & Seed Data
Execute the SQL scripts via MySQL CLI:
```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed.sql
```
*Or execute the Java seeder directly:*
```bash
mvn compile exec:java -Dexec.mainClass="com.university.util.DatabaseSeeder"
```

### Step 3: Compile & Run the Desktop Application
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.university.Main"
```

### Step 4: Run Tests
```bash
mvn test
```

---

## 8. OOP Concepts Implementation Summary

| OOP Concept | Project Implementation |
| :--- | :--- |
| **1. Encapsulation** | Private variables across all model classes (`Person`, `Course`, `Student`, etc.) with public getters/setters and validation (positive credits, valid email formatting, positive year of study). |
| **2. Inheritance** | Abstract base class `Person` extended by `Student`, `Faculty`, and `Administrator` with proper `super()` constructor calls and specialized fields. |
| **3. Polymorphism** | Abstract methods `getRoleName()` and `getDashboardMessage()` declared in `Person` and dynamically resolved at runtime based on database role. |
| **4. Abstraction** | Abstract class `Person` and DAO interfaces (`CourseDAO`, `StudentDAO`, `EnrollmentDAO`, etc.) decoupled from their JDBC implementations. |
| **5. Packages** | Strict modular organization under `com.university`: `model`, `dao`, `dao.impl`, `service`, `exception`, `ui`, `config`, `util`. |
| **6. Exception Handling** | Custom checked and unchecked exceptions (`CourseFullException`, `PrerequisiteNotMetException`, `DuplicateEnrollmentException`, etc.) and database transaction rollback (`conn.rollback()`). |

For deeper technical documentation, see the documents inside the [`docs/`](docs/) folder:
- [`docs/OOP_IMPLEMENTATION.md`](docs/OOP_IMPLEMENTATION.md)
- [`docs/DBMS_IMPLEMENTATION.md`](docs/DBMS_IMPLEMENTATION.md)
- [`docs/DEMO_GUIDE.md`](docs/DEMO_GUIDE.md)

