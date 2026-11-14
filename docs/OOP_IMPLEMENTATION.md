# Object-Oriented Programming (OOP) Implementation Document

This document details the practical implementation of all six core OOP concepts in the **University Academic & Course Enrollment System**.

---

## 1. Encapsulation

Encapsulation is achieved by making all attributes private and providing controlled access via public getters, setters, and constructors with validation rules.

### Implementations:

- **Class: `com.university.model.Course`**
  - **Private Attributes:**
    - `private int courseId;`
    - `private String courseCode;`
    - `private String courseTitle;`
    - `private int credits;`
    - `private int departmentId;`
    - `private String description;`
  - **Validation in Setters:**
    - `setCredits(int credits)`: Validates that credits are positive (`credits <= 0` throws `IllegalArgumentException("Credits must be a positive integer")`).
    - `setCourseTitle(String title)`: Prevents empty or whitespace-only course titles.

- **Class: `com.university.model.Person`**
  - **Private Attributes:**
    - `private int userId;`
    - `private String username;`
    - `private String fullName;`
    - `private String email;`
    - `private Integer departmentId;`
    - `private boolean active;`
  - **Validation in Setters:**
    - `setEmail(String email)`: Enforces non-null and checks for `@` symbol (`IllegalArgumentException`).
    - `setFullName(String fullName)`: Enforces non-blank names.

- **Class: `com.university.model.Student`**
  - **Private Attributes:** `rollNumber`, `programme`, `yearOfStudy`.
  - **Validation in Setters:**
    - `setYearOfStudy(int year)`: Checks that `yearOfStudy > 0`.

- **Class: `com.university.model.CourseOffering`**
  - **Validation in Setters:**
    - `setCapacity(int capacity)`: Verifies that capacity is strictly positive.

---

## 2. Inheritance

Inheritance represents an "IS-A" relationship that eliminates code duplication and enables domain-driven hierarchies.

### Implementations:

- **Superclass: `com.university.model.Person`** (Abstract)
  - Common attributes: `userId`, `username`, `fullName`, `email`, `departmentId`, `active`.
  - Common methods: `getUserId()`, `getFullName()`, `getEmail()`, `isActive()`, etc.

- **Subclass 1: `com.university.model.Student extends Person`**
  - Inherits common identity attributes from `Person`.
  - Uses `super(userId, username, fullName, email, departmentId, active)` in constructor.
  - Adds specialized student fields: `rollNumber`, `programme`, `yearOfStudy`.

- **Subclass 2: `com.university.model.Faculty extends Person`**
  - Inherits common identity attributes from `Person`.
  - Uses `super(...)` in constructor.
  - Adds specialized faculty field: `designation`.

- **Subclass 3: `com.university.model.Administrator extends Person`**
  - Inherits from `Person`.
  - Uses `super(...)` in constructor.
  - Carries administrator identity.

- **Exception Hierarchy:**
  - `com.university.exception.EnrollmentException extends Exception`
  - `com.university.exception.CourseFullException extends EnrollmentException`
  - `com.university.exception.DuplicateEnrollmentException extends EnrollmentException`
  - `com.university.exception.PrerequisiteNotMetException extends EnrollmentException`
  - `com.university.exception.RegistrationClosedException extends EnrollmentException`

---

## 3. Polymorphism

Runtime polymorphism allows a single interface or reference type to execute different underlying behaviors dynamically based on the actual object at runtime.

### Implementations:

- **Abstract Methods in `Person`:**
  ```java
  public abstract String getRoleName();
  public abstract String getDashboardMessage();
  ```

- **Overridden in `Student`:**
  ```java
  @Override
  public String getRoleName() {
      return "STUDENT";
  }

  @Override
  public String getDashboardMessage() {
      return "Welcome Student " + getFullName() + " | Roll No: " + rollNumber;
  }
  ```

- **Overridden in `Faculty`:**
  ```java
  @Override
  public String getRoleName() {
      return "FACULTY";
  }

  @Override
  public String getDashboardMessage() {
      return "Welcome Faculty " + getFullName() + " (" + designation + ")";
  }
  ```

- **Overridden in `Administrator`:**
  ```java
  @Override
  public String getRoleName() {
      return "ADMIN";
  }

  @Override
  public String getDashboardMessage() {
      return "Welcome Administrator " + getFullName() + " - University Management Portal";
  }
  ```

- **Demonstration at Authentication & Dashboard Routing:**
  In `AuthService` and `LoginFrame`:
  ```java
  Person currentUser = authService.login(username, password);
  // Runtime polymorphic call:
  String message = currentUser.getDashboardMessage();
  String role = currentUser.getRoleName();
  ```
  The specific version of `getDashboardMessage()` is determined dynamically at runtime based on whether the logged-in user is a `Student`, `Faculty`, or `Administrator`.

---

## 4. Abstraction

Abstraction isolates the definition of operations from their underlying implementation details.

### Implementations:

1. **Abstract Class: `com.university.model.Person`**
   - Cannot be instantiated directly (`new Person()` is prohibited).
   - Enforces subclasses to implement concrete polymorphic behavior.

2. **Data Access Object (DAO) Interfaces:**
   - `com.university.dao.CourseDAO`
   - `com.university.dao.StudentDAO`
   - `com.university.dao.FacultyDAO`
   - `com.university.dao.DepartmentDAO`
   - `com.university.dao.SemesterDAO`
   - `com.university.dao.CourseOfferingDAO`
   - `com.university.dao.EnrollmentDAO`
   - `com.university.dao.UserDAO`

3. **Concrete JDBC Implementations:**
   - `com.university.dao.impl.CourseDAOImpl`
   - `com.university.dao.impl.StudentDAOImpl`
   - `com.university.dao.impl.FacultyDAOImpl`
   - `com.university.dao.impl.DepartmentDAOImpl`
   - `com.university.dao.impl.SemesterDAOImpl`
   - `com.university.dao.impl.CourseOfferingDAOImpl`
   - `com.university.dao.impl.EnrollmentDAOImpl`
   - `com.university.dao.impl.UserDAOImpl`

Service classes (e.g. `EnrollmentService`, `StudentService`) depend strictly on the **interfaces** rather than concrete JDBC classes, enabling loose coupling and unit testability with mock/stub implementations.

---

## 5. Packages

Packages provide namespace separation and logical grouping of classes according to their architectural responsibilities.

### Package Mapping:

| Package | Purpose & Responsibility |
| :--- | :--- |
| `com.university` | Contains application entry point `Main.java` and integration tests |
| `com.university.model` | Domain entities, POJOs, and inheritance hierarchies |
| `com.university.dao` | Data Access Object interfaces defining CRUD contracts |
| `com.university.dao.impl` | JDBC implementations executing SQL statements and transactions |
| `com.university.service` | Business logic, validation rules, and service coordination |
| `com.university.exception` | Custom application exceptions |
| `com.university.config` | Database connection management and configuration loading |
| `com.university.util` | Utilities for password hashing (BCrypt) and database seeding |
| `com.university.ui.components` | Common UI themes, colors, button builders, card panels |
| `com.university.ui.auth` | Authentication window (`LoginFrame.java`) |
| `com.university.ui.admin` | Administrator dashboard and management modules |
| `com.university.ui.student` | Student course catalog, enrollment, and grade view |
| `com.university.ui.faculty` | Faculty assigned courses, class rosters, and grade submission |

---

## 6. Exception Handling

Robust exception handling ensures that user mistakes, business rule violations, and database errors are handled cleanly without crashing the desktop application or exposing raw SQL errors.

### Implementations:

1. **Custom Checked & Unchecked Exceptions:**
   - `AuthenticationException`: Thrown on bad credentials, empty input, or deactivated accounts.
   - `EnrollmentException`: Base checked exception for registration errors.
   - `CourseFullException`: Thrown when a course offering has reached capacity.
   - `DuplicateEnrollmentException`: Thrown when a student attempts to re-enroll in the same section.
   - `PrerequisiteNotMetException`: Thrown when required prerequisite courses are not completed with passing grades.
   - `RegistrationClosedException`: Thrown when attempting to enroll outside the semester's registration dates.
   - `DatabaseException`: Unchecked runtime wrapper for low-level `SQLException`.

2. **Try-with-Resources:**
   Used consistently across all DAO implementations to prevent database connection and cursor leaks:
   ```java
   try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
       ...
   }
   ```

3. **Transaction Rollback (`rollback`):**
   In `EnrollmentDAOImpl.enrollStudent(...)`, `StudentDAOImpl.addStudent(...)`, and `FacultyDAOImpl.addFaculty(...)`:
   ```java
   try {
       conn.setAutoCommit(false);
       // Multi-step query / update
       conn.commit();
   } catch (SQLException e) {
       if (conn != null) {
           conn.rollback();
       }
       throw new DatabaseException(...);
   }
   ```
   If any operation fails (such as offering full or duplicate roll number), all preceding writes are safely undone.

