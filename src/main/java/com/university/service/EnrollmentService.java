package com.university.service;

import com.university.dao.CourseDAO;
import com.university.dao.CourseOfferingDAO;
import com.university.dao.EnrollmentDAO;
import com.university.dao.SemesterDAO;
import com.university.dao.StudentDAO;
import com.university.dao.impl.CourseDAOImpl;
import com.university.dao.impl.CourseOfferingDAOImpl;
import com.university.dao.impl.EnrollmentDAOImpl;
import com.university.dao.impl.SemesterDAOImpl;
import com.university.dao.impl.StudentDAOImpl;
import com.university.exception.CourseFullException;
import com.university.exception.DuplicateEnrollmentException;
import com.university.exception.EnrollmentException;
import com.university.exception.PrerequisiteNotMetException;
import com.university.exception.RegistrationClosedException;
import com.university.model.Course;
import com.university.model.CourseOffering;
import com.university.model.Enrollment;
import com.university.model.Semester;
import com.university.model.Student;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class EnrollmentService {

    private static final List<String> VALID_GRADES = Arrays.asList("A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F");

    private final EnrollmentDAO enrollmentDAO;
    private final CourseOfferingDAO offeringDAO;
    private final CourseDAO courseDAO;
    private final StudentDAO studentDAO;
    private final SemesterDAO semesterDAO;

    public EnrollmentService() {
        this.enrollmentDAO = new EnrollmentDAOImpl();
        this.offeringDAO = new CourseOfferingDAOImpl();
        this.courseDAO = new CourseDAOImpl();
        this.studentDAO = new StudentDAOImpl();
        this.semesterDAO = new SemesterDAOImpl();
    }

    public EnrollmentService(EnrollmentDAO enrollmentDAO, CourseOfferingDAO offeringDAO,
                             CourseDAO courseDAO, StudentDAO studentDAO, SemesterDAO semesterDAO) {
        this.enrollmentDAO = enrollmentDAO;
        this.offeringDAO = offeringDAO;
        this.courseDAO = courseDAO;
        this.studentDAO = studentDAO;
        this.semesterDAO = semesterDAO;
    }

    public boolean enroll(int studentId, int offeringId) throws EnrollmentException {
        Student student = studentDAO.getStudentById(studentId);
        if (student == null || !student.isActive()) {
            throw new EnrollmentException("Student does not exist or is inactive");
        }

        CourseOffering offering = offeringDAO.getOfferingById(offeringId);
        if (offering == null) {
            throw new EnrollmentException("Course offering not found");
        }

        Semester semester = semesterDAO.getSemesterById(offering.getSemesterId());
        if (semester == null || !semester.isRegistrationOpen()) {
            throw new RegistrationClosedException("Registration is closed for semester: " +
                    (semester != null ? semester.getSemesterName() : "Unknown"));
        }

        if (enrollmentDAO.isAlreadyEnrolled(studentId, offeringId)) {
            throw new DuplicateEnrollmentException("Student is already enrolled in this course offering");
        }

        List<Integer> prereqIds = courseDAO.getPrerequisiteIds(offering.getCourseId());
        for (Integer prereqId : prereqIds) {
            if (!enrollmentDAO.hasCompletedCourse(studentId, prereqId)) {
                Course prereqCourse = courseDAO.getCourseById(prereqId);
                String prereqName = prereqCourse != null ? prereqCourse.getCourseCode() : ("ID " + prereqId);
                throw new PrerequisiteNotMetException("Prerequisite not met: You must pass " + prereqName + " before enrolling.");
            }
        }

        try {
            return enrollmentDAO.enrollStudent(studentId, offeringId, LocalDate.now());
        } catch (RuntimeException re) {
            if (re.getCause() instanceof CourseFullException) {
                throw (CourseFullException) re.getCause();
            }
            if (re.getCause() instanceof DuplicateEnrollmentException) {
                throw (DuplicateEnrollmentException) re.getCause();
            }
            throw re;
        }
    }

    public boolean dropCourse(int enrollmentId, int studentId) throws EnrollmentException {
        Enrollment enrollment = enrollmentDAO.getEnrollmentById(enrollmentId);
        if (enrollment == null) {
            throw new EnrollmentException("Enrollment record not found");
        }
        if (enrollment.getStudentId() != studentId) {
            throw new EnrollmentException("You can only drop your own enrolled courses");
        }
        if (!"ENROLLED".equalsIgnoreCase(enrollment.getStatus())) {
            throw new EnrollmentException("Only currently enrolled courses can be dropped");
        }

        return enrollmentDAO.updateStatus(enrollmentId, "DROPPED");
    }

    public boolean submitGrade(int enrollmentId, int facultyUserId, String grade) throws EnrollmentException {
        if (grade == null || !VALID_GRADES.contains(grade.trim().toUpperCase())) {
            throw new IllegalArgumentException("Invalid grade. Allowed grades: " + VALID_GRADES);
        }

        Enrollment enrollment = enrollmentDAO.getEnrollmentById(enrollmentId);
        if (enrollment == null) {
            throw new EnrollmentException("Enrollment record not found");
        }

        CourseOffering offering = offeringDAO.getOfferingById(enrollment.getOfferingId());
        if (offering == null) {
            throw new EnrollmentException("Course offering not found");
        }

        if (offering.getFacultyId() == null || offering.getFacultyId() != facultyUserId) {
            throw new EnrollmentException("Unauthorized: Only the assigned faculty can enter grades for this course offering");
        }

        return enrollmentDAO.updateGrade(enrollmentId, grade.trim().toUpperCase(), "COMPLETED");
    }

    public boolean submitGradeByAdmin(int enrollmentId, String grade) throws EnrollmentException {
        if (grade == null || !VALID_GRADES.contains(grade.trim().toUpperCase())) {
            throw new IllegalArgumentException("Invalid grade. Allowed grades: " + VALID_GRADES);
        }

        Enrollment enrollment = enrollmentDAO.getEnrollmentById(enrollmentId);
        if (enrollment == null) {
            throw new EnrollmentException("Enrollment record not found");
        }

        return enrollmentDAO.updateGrade(enrollmentId, grade.trim().toUpperCase(), "COMPLETED");
    }

    public List<Enrollment> getStudentEnrollments(int studentId) {
        return enrollmentDAO.getEnrollmentsByStudent(studentId);
    }

    public List<Enrollment> getOfferingEnrollments(int offeringId) {
        return enrollmentDAO.getEnrollmentsByOffering(offeringId);
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentDAO.getAllEnrollments();
    }

    public int getActiveEnrollmentCount() {
        return enrollmentDAO.countActiveEnrollments();
    }
}
