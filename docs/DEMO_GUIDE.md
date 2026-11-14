# Faculty Evaluation & Live Demonstration Guide

Follow this sequence to present the **University Academic & Course Enrollment System** during academic viva or project evaluation.

---

## 1. Preparation & Launch

### Step 1: Confirm Database is Running & Seeded
1. Ensure MySQL is running on port 3306.
2. If not already executed, run the database setup:
   ```bash
   mysql -u root -p < database/schema.sql
   mysql -u root -p < database/seed.sql
   ```
3. Run the application:
   ```bash
   mvn compile exec:java -Dexec.mainClass="com.university.Main"
   ```

---

## 2. Demonstration Flow

### Scene 1: Administrator Operations & Dashboard
1. **Login as Administrator:**
   - **Username:** `admin`
   - **Password:** `admin123`
   - Point out: Shows Welcome Administrator card with role-based routing (polymorphism).
2. **Review Dashboard Statistics:**
   - Total Students: 5
   - Total Faculty: 3
   - Total Courses: 5
   - Active Enrollments: 3
3. **Department & Course Management:**
   - Go to **Courses** tab.
   - Click **Add Course** -> Enter Code: `CS401`, Title: `Software Engineering`, Credits: `4`, Department: `CS`, Description: `Software design patterns & architectures`.
   - Click **OK**. Notice the table updates immediately from the database.
4. **Create a Course Offering & Assign Faculty:**
   - Go to **Course Offerings** tab.
   - Click **Create Course Offering**.
   - Select Course: `CS401 - Software Engineering`, Semester: `Spring 2027`, Section: `A`, Capacity: `30`, Assign Faculty: `Prof. John Smith`.
   - Click **OK**. The offering is now open for student enrollments.
5. Click **Logout**.

---

### Scene 2: Student Enrollment & Business Rule Validation
1. **Login as Evan Wright (First Year Student):**
   - **Username:** `student.evan`
   - **Password:** `student123`
   - Point out: Dynamic student message displayed: `Welcome Student Evan Wright | Roll No: CS2026005`.
2. **Demonstrate Prerequisite Validation:**
   - Go to **Available Courses & Enrollment** tab.
   - Select `Spring 2027` semester.
   - Select `CS201 - Data Structures and Algorithms` (Notice the Prerequisites column shows `CS101`).
   - Click **Enroll in Selected Course** and confirm.
   - **Result:** System blocks the enrollment with an error dialog:
     > *"Prerequisite not met: You must pass CS101 before enrolling."*
   - This proves the prerequisite business logic is actively enforced.
3. **Enroll in an Eligible Course:**
   - Select `CS101 - Introduction to Programming` (No prerequisites).
   - Click **Enroll in Selected Course** and confirm.
   - **Result:** Success dialog: *"Successfully enrolled in CS101!"*.
   - Available seats count decrements in real-time.
4. **Demonstrate Duplicate Enrollment Prevention:**
   - Select `CS101` again.
   - Click **Enroll in Selected Course**.
   - **Result:** Blocked with: *"Student is already enrolled in this course offering"*.
5. **View Enrolled Courses & Dropping:**
   - Switch to **My Enrolled Courses** tab.
   - Notice `CS101` is listed with status `ENROLLED`.
   - Switch to **Grades & History** tab -> Notice `CS101` status is `ENROLLED`, Grade is `Pending`.
   - Click **Logout**.

---

### Scene 3: Student with Completed Prerequisites (Alice)
1. **Login as Alice Johnson:**
   - **Username:** `student.alice`
   - **Password:** `student123`
2. **Check Academic History:**
   - Go to **Grades & History** tab.
   - Notice Alice has previously completed `CS101` with grade `'A'` in Fall 2026.
3. **Enroll in Advanced Course:**
   - Go to **Available Courses & Enrollment** tab.
   - Select `CS201 - Data Structures and Algorithms`.
   - Click **Enroll in Selected Course**.
   - **Result:** Successfully enrolled because Alice meets the `CS101` prerequisite requirement.
4. Click **Logout**.

---

### Scene 4: Faculty Roster & Grade Submission
1. **Login as Prof. John Smith:**
   - **Username:** `prof.smith`
   - **Password:** `faculty123`
   - Point out: Shows Faculty portal with assigned courses.
2. **Inspect Assigned Courses & Rosters:**
   - Under **My Assigned Course Offerings**, select `CS101` (or `CS201`).
   - The lower table displays the **Class Roster** containing all enrolled students (`student.evan`, etc.).
3. **Grade Evaluation & Submission:**
   - Click on `Evan Wright` in the roster.
   - Select Grade from dropdown: `A`.
   - Click **Submit Grade**.
   - **Result:** Evan's grade updates to `A`, and status changes to `COMPLETED`.
4. Click **Logout**.

---

### Scene 5: Confirming Historical Completion & Grade Reflection
1. Log back in as `student.evan` (`student.evan` / `student123`).
2. Go to **Grades & History** tab.
3. Notice `CS101` now shows:
   - Status: `COMPLETED`
   - Grade: `A`
4. Now go to **Available Courses** and enroll in `CS201`.
5. **Result:** Enrollment now succeeds because Evan has now passed `CS101`!

---

## 3. Explaining OOP Concepts to the Examiner

When the examiner asks how OOP is implemented:

1. **Encapsulation:**
   - Open `com.university.model.Course.java`.
   - Show private fields (`courseId`, `credits`, `courseTitle`).
   - Show validation logic inside `setCredits(...)` and `setCourseTitle(...)` preventing negative or empty values.

2. **Inheritance:**
   - Open `com.university.model.Person.java` and `com.university.model.Student.java`.
   - Show how `Student extends Person` and calls `super(...)`.

3. **Polymorphism:**
   - Show `Person.java` abstract methods `getRoleName()` and `getDashboardMessage()`.
   - Show their distinct overrides in `Student.java`, `Faculty.java`, and `Administrator.java`.
   - Show how `AuthService.login(...)` returns a `Person` reference and executes the corresponding subclass method dynamically.

4. **Abstraction:**
   - Show `com.university.dao.EnrollmentDAO.java` interface.
   - Explain that services depend on the interface rather than the concrete SQL implementation in `EnrollmentDAOImpl.java`.

5. **Packages:**
   - Point to the clean directory tree under `com.university`: `model`, `dao`, `service`, `exception`, `ui`, `config`.

6. **Exception Handling:**
   - Show `com.university.exception.CourseFullException` and `PrerequisiteNotMetException`.
   - Show `conn.setAutoCommit(false)`, `conn.commit()`, and `conn.rollback()` inside `EnrollmentDAOImpl.java`.

