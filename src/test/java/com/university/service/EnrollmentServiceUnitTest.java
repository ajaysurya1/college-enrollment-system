package com.university.service;

import com.university.dao.*;
import com.university.exception.*;
import com.university.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class EnrollmentServiceUnitTest {

    private EnrollmentService enrollmentService;
    private StubEnrollmentDAO enrollmentDAO;
    private StubCourseOfferingDAO offeringDAO;
    private StubCourseDAO courseDAO;
    private StubStudentDAO studentDAO;
    private StubSemesterDAO semesterDAO;

    @BeforeEach
    public void setUp() {
        enrollmentDAO = new StubEnrollmentDAO();
        offeringDAO = new StubCourseOfferingDAO();
        courseDAO = new StubCourseDAO();
        studentDAO = new StubStudentDAO();
        semesterDAO = new StubSemesterDAO();

        enrollmentService = new EnrollmentService(enrollmentDAO, offeringDAO, courseDAO, studentDAO, semesterDAO);
    }

    @Test
    public void testEnrollmentFailsWhenRegistrationClosed() {
        Semester closedSemester = new Semester(1, "Fall", "2026",
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 12, 1),
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 15));
        semesterDAO.semesters.put(1, closedSemester);

        CourseOffering offering = new CourseOffering(10, 100, 1, 2, "A", 30);
        offeringDAO.offerings.put(10, offering);

        Student student = new Student(5, "alice", "Alice J", "alice@test.edu", 1, true, "S001", "CS", 1);
        studentDAO.students.put(5, student);

        assertThrows(RegistrationClosedException.class, () -> {
            enrollmentService.enroll(5, 10);
        });
    }

    @Test
    public void testEnrollmentFailsWhenDuplicate() {
        Semester openSemester = new Semester(1, "Spring", "2027",
                LocalDate.now().minusDays(10), LocalDate.now().plusMonths(3),
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(20));
        semesterDAO.semesters.put(1, openSemester);

        CourseOffering offering = new CourseOffering(10, 100, 1, 2, "A", 30);
        offeringDAO.offerings.put(10, offering);

        Student student = new Student(5, "alice", "Alice J", "alice@test.edu", 1, true, "S001", "CS", 1);
        studentDAO.students.put(5, student);

        enrollmentDAO.alreadyEnrolled = true;

        assertThrows(DuplicateEnrollmentException.class, () -> {
            enrollmentService.enroll(5, 10);
        });
    }

    @Test
    public void testEnrollmentFailsWhenPrerequisiteNotMet() {
        Semester openSemester = new Semester(1, "Spring", "2027",
                LocalDate.now().minusDays(10), LocalDate.now().plusMonths(3),
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(20));
        semesterDAO.semesters.put(1, openSemester);

        CourseOffering offering = new CourseOffering(10, 201, 1, 2, "A", 30);
        offeringDAO.offerings.put(10, offering);

        Student student = new Student(5, "alice", "Alice J", "alice@test.edu", 1, true, "S001", "CS", 1);
        studentDAO.students.put(5, student);

        courseDAO.prereqIds.put(201, Collections.singletonList(101));
        courseDAO.courses.put(101, new Course(101, "CS101", "Intro to CS", 4, 1, "Desc"));
        enrollmentDAO.completedCourses.clear();

        assertThrows(PrerequisiteNotMetException.class, () -> {
            enrollmentService.enroll(5, 10);
        });
    }

    @Test
    public void testEnrollmentSucceedsWhenAllRequirementsMet() throws Exception {
        Semester openSemester = new Semester(1, "Spring", "2027",
                LocalDate.now().minusDays(10), LocalDate.now().plusMonths(3),
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(20));
        semesterDAO.semesters.put(1, openSemester);

        CourseOffering offering = new CourseOffering(10, 201, 1, 2, "A", 30);
        offeringDAO.offerings.put(10, offering);

        Student student = new Student(5, "alice", "Alice J", "alice@test.edu", 1, true, "S001", "CS", 1);
        studentDAO.students.put(5, student);

        courseDAO.prereqIds.put(201, Collections.singletonList(101));
        courseDAO.courses.put(101, new Course(101, "CS101", "Intro to CS", 4, 1, "Desc"));
        enrollmentDAO.completedCourses.add("5_101");

        boolean result = enrollmentService.enroll(5, 10);
        assertTrue(result);
    }

    @Test
    public void testDropCourseValidation() throws Exception {
        Enrollment enrollment = new Enrollment(1, 5, 10, LocalDate.now(), "ENROLLED", null);
        enrollmentDAO.enrollments.put(1, enrollment);

        assertThrows(EnrollmentException.class, () -> {
            enrollmentService.dropCourse(1, 999);
        });

        boolean dropped = enrollmentService.dropCourse(1, 5);
        assertTrue(dropped);
        assertEquals("DROPPED", enrollment.getStatus());
    }

    @Test
    public void testGradeValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            enrollmentService.submitGradeByAdmin(1, "INVALID_GRADE");
        });
    }

    static class StubStudentDAO implements StudentDAO {
        Map<Integer, Student> students = new HashMap<>();

        public boolean addStudent(Student student, String passwordHash) { return true; }
        public Student getStudentById(int userId) { return students.get(userId); }
        public Student getStudentByRoll(String rollNumber) { return null; }
        public List<Student> getAllStudents() { return new ArrayList<>(students.values()); }
        public boolean updateStudent(Student student) { return true; }
        public boolean deleteStudent(int userId) { return true; }
        public int countStudents() { return students.size(); }
    }

    static class StubCourseDAO implements CourseDAO {
        Map<Integer, Course> courses = new HashMap<>();
        Map<Integer, List<Integer>> prereqIds = new HashMap<>();

        public boolean addCourse(Course course) { return true; }
        public Course getCourseById(int courseId) { return courses.get(courseId); }
        public Course getCourseByCode(String code) { return null; }
        public List<Course> getAllCourses() { return new ArrayList<>(courses.values()); }
        public List<Course> getCoursesByDepartment(int departmentId) { return Collections.emptyList(); }
        public boolean updateCourse(Course course) { return true; }
        public boolean deleteCourse(int courseId) { return true; }
        public List<Integer> getPrerequisiteIds(int courseId) { return prereqIds.getOrDefault(courseId, Collections.emptyList()); }
        public List<Course> getPrerequisiteCourses(int courseId) { return Collections.emptyList(); }
        public boolean addPrerequisite(int courseId, int prereqCourseId) { return true; }
        public boolean removePrerequisite(int courseId, int prereqCourseId) { return true; }
        public int countCourses() { return courses.size(); }
    }

    static class StubCourseOfferingDAO implements CourseOfferingDAO {
        Map<Integer, CourseOffering> offerings = new HashMap<>();

        public boolean addOffering(CourseOffering offering) { return true; }
        public CourseOffering getOfferingById(int id) { return offerings.get(id); }
        public List<CourseOffering> getAllOfferings() { return new ArrayList<>(offerings.values()); }
        public List<CourseOffering> getOfferingsBySemester(int semesterId) { return Collections.emptyList(); }
        public List<CourseOffering> getOfferingsByFaculty(int facultyId) { return Collections.emptyList(); }
        public boolean updateOffering(CourseOffering offering) { return true; }
        public boolean deleteOffering(int id) { return true; }
        public int getEnrolledCount(int offeringId) { return 0; }
    }

    static class StubSemesterDAO implements SemesterDAO {
        Map<Integer, Semester> semesters = new HashMap<>();

        public boolean addSemester(Semester sem) { return true; }
        public Semester getSemesterById(int id) { return semesters.get(id); }
        public List<Semester> getAllSemesters() { return new ArrayList<>(semesters.values()); }
        public Semester getCurrentSemester() { return semesters.values().stream().findFirst().orElse(null); }
        public boolean updateSemester(Semester sem) { return true; }
        public boolean deleteSemester(int id) { return true; }
    }

    static class StubEnrollmentDAO implements EnrollmentDAO {
        Map<Integer, Enrollment> enrollments = new HashMap<>();
        Set<String> completedCourses = new HashSet<>();
        boolean alreadyEnrolled = false;

        public boolean enrollStudent(int studentId, int offeringId, LocalDate date) { return true; }
        public boolean isAlreadyEnrolled(int studentId, int offeringId) { return alreadyEnrolled; }
        public boolean hasCompletedCourse(int studentId, int courseId) { return completedCourses.contains(studentId + "_" + courseId); }
        public boolean updateStatus(int enrollmentId, String status) {
            Enrollment e = enrollments.get(enrollmentId);
            if (e != null) {
                e.setStatus(status);
                return true;
            }
            return false;
        }
        public boolean updateGrade(int enrollmentId, String grade, String status) {
            Enrollment e = enrollments.get(enrollmentId);
            if (e != null) {
                e.setGrade(grade);
                e.setStatus(status);
                return true;
            }
            return false;
        }
        public Enrollment getEnrollmentById(int enrollmentId) { return enrollments.get(enrollmentId); }
        public List<Enrollment> getEnrollmentsByStudent(int studentId) { return Collections.emptyList(); }
        public List<Enrollment> getEnrollmentsByOffering(int offeringId) { return Collections.emptyList(); }
        public List<Enrollment> getAllEnrollments() { return new ArrayList<>(enrollments.values()); }
        public int countActiveEnrollments() { return 0; }
    }
}

