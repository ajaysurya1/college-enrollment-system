package com.university.dao.impl;

import com.university.config.DatabaseConnection;
import com.university.dao.EnrollmentDAO;
import com.university.exception.CourseFullException;
import com.university.exception.DatabaseException;
import com.university.exception.DuplicateEnrollmentException;
import com.university.model.Enrollment;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAOImpl implements EnrollmentDAO {

    @Override
    public boolean enrollStudent(int studentId, int offeringId, LocalDate date) {
        String lockOfferingSql = "SELECT capacity FROM course_offerings WHERE offering_id = ? FOR UPDATE";
        String checkEnrolledCountSql = "SELECT COUNT(*) FROM enrollments WHERE offering_id = ? AND status = 'ENROLLED'";
        String checkExistingSql = "SELECT enrollment_id, status FROM enrollments WHERE student_id = ? AND offering_id = ?";
        String insertSql = "INSERT INTO enrollments (student_id, offering_id, enrollment_date, status) VALUES (?, ?, ?, 'ENROLLED') " +
                "ON DUPLICATE KEY UPDATE status = 'ENROLLED', enrollment_date = VALUES(enrollment_date), grade = NULL";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int capacity = 0;
            try (PreparedStatement psLock = conn.prepareStatement(lockOfferingSql)) {
                psLock.setInt(1, offeringId);
                try (ResultSet rs = psLock.executeQuery()) {
                    if (rs.next()) {
                        capacity = rs.getInt("capacity");
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement psCheck = conn.prepareStatement(checkExistingSql)) {
                psCheck.setInt(1, studentId);
                psCheck.setInt(2, offeringId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        String st = rs.getString("status");
                        if ("ENROLLED".equalsIgnoreCase(st)) {
                            conn.rollback();
                            throw new DuplicateEnrollmentException("Student is already enrolled in this course offering.");
                        }
                    }
                }
            } catch (DuplicateEnrollmentException de) {
                throw de;
            }

            int currentEnrolled = 0;
            try (PreparedStatement psCount = conn.prepareStatement(checkEnrolledCountSql)) {
                psCount.setInt(1, offeringId);
                try (ResultSet rs = psCount.executeQuery()) {
                    if (rs.next()) {
                        currentEnrolled = rs.getInt(1);
                    }
                }
            }

            if (currentEnrolled >= capacity) {
                conn.rollback();
                throw new CourseFullException("Cannot enroll: Course offering is at maximum capacity (" + capacity + ").");
            }

            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setInt(1, studentId);
                psInsert.setInt(2, offeringId);
                psInsert.setDate(3, Date.valueOf(date));
                psInsert.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (CourseFullException | DuplicateEnrollmentException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
            throw new RuntimeException(e);
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
            throw new DatabaseException("Enrollment transaction failed", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    @Override
    public boolean isAlreadyEnrolled(int studentId, int offeringId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND offering_id = ? AND status = 'ENROLLED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, offeringId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking enrollment status", e);
        }
        return false;
    }

    @Override
    public boolean hasCompletedCourse(int studentId, int courseId) {
        String sql = "SELECT COUNT(*) FROM enrollments e " +
                "JOIN course_offerings co ON e.offering_id = co.offering_id " +
                "WHERE e.student_id = ? AND co.course_id = ? AND e.status = 'COMPLETED' " +
                "AND e.grade IS NOT NULL AND e.grade <> 'F'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking completed prerequisite", e);
        }
        return false;
    }

    @Override
    public boolean updateStatus(int enrollmentId, String status) {
        String sql = "UPDATE enrollments SET status = ? WHERE enrollment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, enrollmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating enrollment status", e);
        }
    }

    @Override
    public boolean updateGrade(int enrollmentId, String grade, String status) {
        String sql = "UPDATE enrollments SET grade = ?, status = ? WHERE enrollment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, grade);
            ps.setString(2, status);
            ps.setInt(3, enrollmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating student grade", e);
        }
    }

    @Override
    public Enrollment getEnrollmentById(int enrollmentId) {
        String sql = "SELECT e.enrollment_id, e.student_id, e.offering_id, e.enrollment_date, e.status, e.grade, " +
                "s.roll_number, u.full_name AS student_name, c.course_code, c.course_title, c.credits, " +
                "sem.semester_name, co.section, fac.full_name AS faculty_name " +
                "FROM enrollments e " +
                "JOIN students s ON e.student_id = s.user_id " +
                "JOIN users u ON s.user_id = u.user_id " +
                "JOIN course_offerings co ON e.offering_id = co.offering_id " +
                "JOIN courses c ON co.course_id = c.course_id " +
                "JOIN semesters sem ON co.semester_id = sem.semester_id " +
                "LEFT JOIN users fac ON co.faculty_id = fac.user_id " +
                "WHERE e.enrollment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEnrollment(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting enrollment by ID", e);
        }
        return null;
    }

    @Override
    public List<Enrollment> getEnrollmentsByStudent(int studentId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.student_id, e.offering_id, e.enrollment_date, e.status, e.grade, " +
                "s.roll_number, u.full_name AS student_name, c.course_code, c.course_title, c.credits, " +
                "sem.semester_name, co.section, fac.full_name AS faculty_name " +
                "FROM enrollments e " +
                "JOIN students s ON e.student_id = s.user_id " +
                "JOIN users u ON s.user_id = u.user_id " +
                "JOIN course_offerings co ON e.offering_id = co.offering_id " +
                "JOIN courses c ON co.course_id = c.course_id " +
                "JOIN semesters sem ON co.semester_id = sem.semester_id " +
                "LEFT JOIN users fac ON co.faculty_id = fac.user_id " +
                "WHERE e.student_id = ? " +
                "ORDER BY sem.start_date DESC, c.course_code";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToEnrollment(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting enrollments by student", e);
        }
        return list;
    }

    @Override
    public List<Enrollment> getEnrollmentsByOffering(int offeringId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.student_id, e.offering_id, e.enrollment_date, e.status, e.grade, " +
                "s.roll_number, u.full_name AS student_name, c.course_code, c.course_title, c.credits, " +
                "sem.semester_name, co.section, fac.full_name AS faculty_name " +
                "FROM enrollments e " +
                "JOIN students s ON e.student_id = s.user_id " +
                "JOIN users u ON s.user_id = u.user_id " +
                "JOIN course_offerings co ON e.offering_id = co.offering_id " +
                "JOIN courses c ON co.course_id = c.course_id " +
                "JOIN semesters sem ON co.semester_id = sem.semester_id " +
                "LEFT JOIN users fac ON co.faculty_id = fac.user_id " +
                "WHERE e.offering_id = ? " +
                "ORDER BY s.roll_number";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, offeringId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToEnrollment(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting enrollments by offering", e);
        }
        return list;
    }

    @Override
    public List<Enrollment> getAllEnrollments() {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.student_id, e.offering_id, e.enrollment_date, e.status, e.grade, " +
                "s.roll_number, u.full_name AS student_name, c.course_code, c.course_title, c.credits, " +
                "sem.semester_name, co.section, fac.full_name AS faculty_name " +
                "FROM enrollments e " +
                "JOIN students s ON e.student_id = s.user_id " +
                "JOIN users u ON s.user_id = u.user_id " +
                "JOIN course_offerings co ON e.offering_id = co.offering_id " +
                "JOIN courses c ON co.course_id = c.course_id " +
                "JOIN semesters sem ON co.semester_id = sem.semester_id " +
                "LEFT JOIN users fac ON co.faculty_id = fac.user_id " +
                "ORDER BY e.enrollment_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToEnrollment(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting all enrollments", e);
        }
        return list;
    }

    @Override
    public int countActiveEnrollments() {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE status = 'ENROLLED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting active enrollments", e);
        }
        return 0;
    }

    private Enrollment mapRowToEnrollment(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment();
        e.setEnrollmentId(rs.getInt("enrollment_id"));
        e.setStudentId(rs.getInt("student_id"));
        e.setOfferingId(rs.getInt("offering_id"));
        e.setEnrollmentDate(rs.getDate("enrollment_date").toLocalDate());
        e.setStatus(rs.getString("status"));
        e.setGrade(rs.getString("grade"));
        e.setStudentRoll(rs.getString("roll_number"));
        e.setStudentName(rs.getString("student_name"));
        e.setCourseCode(rs.getString("course_code"));
        e.setCourseTitle(rs.getString("course_title"));
        e.setCredits(rs.getInt("credits"));
        e.setSemesterName(rs.getString("semester_name"));
        e.setSection(rs.getString("section"));
        e.setFacultyName(rs.getString("faculty_name"));
        return e;
    }
}
