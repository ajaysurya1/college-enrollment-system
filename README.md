# University Academic & Course Enrollment Management System 🎓

**A Modern Java Swing & MySQL Academic Management & Course Registration Desktop Application**

UniPortal is an end-to-end university academic management system designed to connect administrators, faculty, and students. Featuring a clean, high-contrast dashboard, dynamic split-pane workflows, and strict ACID transactional controls, UniPortal streamlines course catalog management, faculty assignments, and real-time student course enrollment while preventing over-subscription when course capacities reach their limits.

---

## 🌟 Key Features

### 1. Modern Desktop GUI (Java Swing)
- **Interactive Dashboard**: Real-time statistical metrics derived dynamically from MySQL (Total Students, Total Faculty, Total Courses, Active Enrollments, Course Offerings, Academic Departments) and recent course registration feeds.
- **Student Directory**: Manage registered students, view academic roll numbers, assign degree programmes (e.g. B.Tech Computer Science), track study years, search by name/roll, and safely add/deactivate student accounts.
- **Faculty Directory**: Manage academic professors, associate professors, assistant professors, and lecturers linked with academic departments.
- **Course Catalog & Prerequisites**: Full course directory with credits and descriptions. Interactive prerequisite management preventing students from enrolling without passing prerequisite courses.
- **Course Offerings Schedule**: Manage semester-wise course offerings, sections (Section A, B), faculty instructor assignments, and maximum seat capacities.
- **Enrollment Records & Grading**: View all enrollment records across semesters, track enrollment statuses (`ENROLLED`, `DROPPED`, `COMPLETED`), and perform administrative grade overrides.
- **Student Self-Service Portal**: Interactive student view allowing students to select semesters, browse available course offerings with live available seats remaining (e.g., `35 left`), view prerequisites, and enroll in 1-click or drop enrolled courses.
- **Faculty Grading Portal**: Dedicated faculty view where instructors select their profile, view assigned course offerings, inspect student class rosters, and submit letter grades (`A+` to `F`).
- **About Page**: System specifications, OOP architectural highlights, and technology stack credits.

### 2. Dual Compatibility: GUI & Console
- **Swing Desktop GUI**: Default interface launched when running the application.
- **Terminal CLI Mode**: Fully retained interactive console application accessible via the `--cli` flag.

---

## 🛠️ Technologies Used

- **Java SE 17+ / 21+**: Object-Oriented Programming (OOP), encapsulation, inheritance, polymorphism, and abstraction.
- **Java Swing & AWT**: Responsive layouts (`BorderLayout`, `CardLayout`, `GridBagLayout`, `BoxLayout`, `JSplitPane`), custom cards, badges, and FlatLaf modern UI styling.
- **MySQL 8.0+**: Relational database storage with InnoDB engine, foreign keys, unique keys, and check constraints.
- **JDBC (Java Database Connectivity)**: Parameterized queries with `PreparedStatement` preventing SQL injection.
- **ACID Transactions**: Row-level locking (`SELECT ... FOR UPDATE`) ensuring zero race conditions during peak seat registration.
- **BCrypt**: Secure password encryption for user accounts.
- **MySQL Connector/J**: High-performance database driver.

---

## 📁 Project Structure

```text
oops/
├── .gitignore
├── db.properties                         # Local MySQL credentials (git-ignored)
├── db.properties.example                 # Template for database configuration
├── prompt.txt                            # Requirement specifications
├── README.md                             # Project documentation
├── run.bat                               # One-click launch script for Windows
│
├── database/
│   ├── university.sql                    # Complete database schema + seed data
│   ├── schema.sql                        # Schema definition
│   └── seed.sql                          # Initial records seed
│
├── lib/
│   ├── mysql-connector-j-8.3.0.jar       # MySQL JDBC Driver
│   ├── flatlaf-3.4.jar                   # Modern FlatLaf Swing Look & Feel
│   └── jbcrypt-0.4.jar                   # BCrypt password hashing library
│
├── bin/                                  # Compiled .class bytecode
│
└── src/
    └── com/
        └── university/
            ├── Main.java                 # Main entrypoint (GUI by default / CLI with --cli)
            ├── DBConnection.java         # JDBC Connection provider (loads db.properties)
            │
            ├── Person.java               # Abstract base model (Inheritance & Polymorphism)
            ├── Student.java              # Student model (extends Person)
            ├── Faculty.java              # Faculty model (extends Person)
            ├── Administrator.java        # Administrator model (extends Person)
            ├── Course.java               # Course catalog model
            ├── Semester.java             # Academic semester model
            ├── CourseOffering.java       # Course section offering model
            ├── Enrollment.java           # Student enrollment record model
            ├── Department.java           # Academic department model
            │
            ├── StudentDAO.java           # Student Data Access Object
            ├── FacultyDAO.java           # Faculty Data Access Object
            ├── CourseDAO.java            # Course & Prerequisite Data Access Object
            ├── CourseOfferingDAO.java   # Course Offering Data Access Object
            ├── EnrollmentDAO.java        # Enrollment Data Access Object (ACID Transactional)
            ├── SemesterDAO.java          # Semester Data Access Object
            ├── DepartmentDAO.java        # Department Data Access Object
            ├── DashboardDAO.java         # Real-time statistics & activity feeds
            │
            └── ui/
                ├── UniversityApp.java    # Main JFrame desktop application
                ├── UITheme.java          # Theme colors, fonts, styled cards and buttons
                ├── SidebarPanel.java     # Collapsible navigation sidebar
                ├── HeaderPanel.java      # Dynamic breadcrumb title & live clock header
                ├── DashboardPanel.java   # Stat cards & recent activity dashboard
                ├── StudentsPanel.java    # Student directory & registration panel
                ├── FacultyPanel.java     # Faculty directory & management panel
                ├── CoursesPanel.java     # Course catalog & prerequisite panel
                ├── CourseOfferingsPanel.java # Course offerings & schedule panel
                ├── EnrollmentsPanel.java # Enrollment management & grade override panel
                ├── StudentPortalPanel.java   # Student course registration & drop panel
                ├── FacultyPortalPanel.java   # Faculty roster & grading panel
                └── AboutPanel.java       # System specifications & OOP implementation
```

---

## 🚀 Setup & Execution Instructions

### Prerequisites
1. **Java JDK 17+ or 21+** installed (`java -version`, `javac -version`).
2. **MySQL Server 8.0+** running locally.
3. **JAR files** present in the `lib/` directory (`mysql-connector-j-8.3.0.jar`, `flatlaf-3.4.jar`, `jbcrypt-0.4.jar`).

### 1. Database Setup
Execute the SQL script in MySQL Workbench or the MySQL command line:
```sql
SOURCE database/university.sql;
```

### 2. Configure Database Credentials
Create or verify `db.properties` in the project root:
```properties
db.url=jdbc:mysql://localhost:3306/university_academic_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=YOUR_MYSQL_PASSWORD
```
*(Note: `db.properties` is protected by `.gitignore` so your personal credentials are never exposed.)*

### 3. Compile the Complete Project
From the project root directory, run:

**On Windows (PowerShell / CMD):**
```powershell
javac -encoding UTF-8 -cp "lib/*" -d bin src\com\university\*.java src\com\university\ui\*.java
```

**On Linux / macOS:**
```bash
javac -encoding UTF-8 -cp "lib/*" -d bin src/com/university/*.java src/com/university/ui/*.java
```

### 4. Run the Application

#### A. Launch the Modern Java Swing Desktop Application (Default):
**On Windows:**
```powershell
java -cp "bin;lib/*" com.university.Main
```
*Alternatively, run the one-click script:*
```powershell
.\run.bat
```

**On Linux / macOS:**
```bash
java -cp "bin:lib/*" com.university.Main
```

#### B. Launch the Interactive Terminal CLI:
If you wish to use the terminal interface:
**On Windows:**
```powershell
java -cp "bin;lib/*" com.university.Main --cli
```

**On Linux / macOS:**
```bash
java -cp "bin:lib/*" com.university.Main --cli
```

---

## 🗄️ Database Tables & Relationships

| Table | Description | Relationships |
|---|---|---|
| `departments` | Stores academic departments | Referenced by `users.department_id` and `courses.department_id` |
| `users` | Base credentials table storing usernames, roles, and BCrypt hashes | Referenced by `students(user_id)` and `faculty(user_id)` |
| `students` | Stores roll numbers, programmes, and year of study | Inherits `users` via 1-to-1 foreign key |
| `faculty` | Stores academic designations and qualifications | Inherits `users` via 1-to-1 foreign key |
| `courses` | Stores course code, title, credits, department, and description | Foreign Key to `departments(department_id)` |
| `course_prerequisites` | Composite table mapping course prerequisite dependencies | Foreign Keys to `courses(course_id)` and `courses(prerequisite_course_id)` |
| `semesters` | Academic calendar dates and registration time windows | Referenced by `course_offerings.semester_id` |
| `course_offerings` | Section schedules, assigned faculty, and seat capacities | Foreign Keys to `courses`, `semesters`, and `faculty` |
| `enrollments` | Tracks registrations, statuses (`ENROLLED`, `DROPPED`, `COMPLETED`), and grades | Foreign Keys to `students(user_id)` and `course_offerings(offering_id)` |

---

## 💡 Business Logic & Transactional Safety

- **ACID Transactional Enrollment**: Course registration in `EnrollmentDAO.java` initiates `conn.setAutoCommit(false)` and uses `SELECT ... FOR UPDATE` row locking on the `course_offerings` row.
- **Seat Capacity Protection**: Ensures that if concurrent students attempt to register for the final seat simultaneously, only one transaction commits; subsequent transactions identify `currentEnrolled >= capacity` and rollback with a descriptive message.
- **Prerequisite Validation**: Before enrolling, the system queries whether the student has completed and passed all prerequisite courses (grade not null and not `F`).
- **Registration Period Enforcement**: Validates that today's date falls strictly between `registration_start` and `registration_end` for the offering's semester.
- **Duplicate Registration Prevention**: Unique constraint `uq_student_offering` and query checks prevent duplicate enrollments for the same course offering.

---

## 🧩 Object-Oriented Programming (OOP) Implementation

| Concept | Implementation in Codebase |
|---|---|
| **Encapsulation** | All fields across `Student`, `Faculty`, `Course`, `CourseOffering`, `Enrollment`, and `Semester` are private/protected and accessed via getter and setter methods. |
| **Inheritance** | Abstract class `Person` encapsulates common user attributes (`userId`, `username`, `fullName`, `email`, `departmentId`, `active`), extended by `Student`, `Faculty`, and `Administrator`. |
| **Polymorphism** | Abstract methods `getRoleName()` and `getDashboardMessage()` in `Person` are overridden with persona-specific implementations across `Student`, `Faculty`, and `Administrator`. |
| **Abstraction** | Data Access Objects (`StudentDAO`, `FacultyDAO`, `CourseDAO`, `CourseOfferingDAO`, `EnrollmentDAO`, `DashboardDAO`) isolate database queries from the presentation UI layer. |

---

## 👥 Demo Logins & Seeded Personas

| Persona | Username | Role / Roll No | Purpose |
|---|---|---|---|
| Administrator | `admin` | System Administrator | Full access to directory management, course catalog & offerings |
| Faculty Member | `prof.smith` | Associate Professor | Instructor assigned to CS101, CS201, CS301 |
| Student | `CS2025001` | Alice Johnson (Year 2) | Eligible for advanced CS courses (passed CS101) |
| Student | `250701030` | Ajay Surya A (Year 4) | Student enrollment and self-service registration testing |
