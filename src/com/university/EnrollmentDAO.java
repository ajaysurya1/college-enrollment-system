package com.university;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    public String enrollStudent(int studentId, int offeringId) {
        String lockOfferingSql = "SELECT co.capacity, s.registration_start, s.registration_end, s.semester_name, co.course_id " +
                "FROM course_offerings co JOIN semesters s ON co.semester_id = s.semester_id " +
                "WHERE co.offering_id = ? FOR UPDATE";
        String checkDuplicateSql = "SELECT enrollment_id, status FROM enrollments WHERE student_id = ? AND offering_id = ?";
        String checkPrereqSql = "SELECT prerequisite_course_id FROM course_prerequisites WHERE course_id = ?";
        String checkPassedPrereqSql = "SELECT COUNT(*) FROM enrollments e " +
                "JOIN course_offerings co ON e.offering_id = co.offering_id " +
                "WHERE e.student_id = ? AND co.course_id = ? AND e.status = 'COMPLETED' AND (e.grade IS NOT NULL AND e.grade <> 'F')";
        String checkEnrolledCountSql = "SELECT COUNT(*) FROM enrollments WHERE offering_id = ? AND status = 'ENROLLED'";
        String insertSql = "INSERT INTO enrollments (student_id, offering_id, enrollment_date, status) VALUES (?, ?, ?, 'ENROLLED') " +
                "ON DUPLICATE KEY UPDATE status = 'ENROLLED', enrollment_date = VALUES(enrollment_date), grade = NULL";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int capacity = 0;
            int courseId = 0;
            LocalDate regStart = null;
            LocalDate regEnd = null;
            String semName = "";

            try (PreparedStatement psLock = conn.prepareStatement(lockOfferingSql)) {
                psLock.setInt(1, offeringId);
                try (ResultSet rs = psLock.executeQuery()) {
                    if (rs.next()) {
                        capacity = rs.getInt("capacity");
                        courseId = rs.getInt("course_id");
                        regStart = rs.getDate("registration_start").toLocalDate();
                        regEnd = rs.getDate("registration_end").toLocalDate();
                        semName = rs.getString("semester_name");
                    } else {
                        conn.rollback();
                        return "Course offering not found.";
                    }
                }
            }

            LocalDate today = LocalDate.now();
            if (today.isBefore(regStart) || today.isAfter(regEnd)) {
                conn.rollback();
                return "Registration is closed for semester: " + semName;
            }

            try (PreparedStatement psDup = conn.prepareStatement(checkDuplicateSql)) {
                psDup.setInt(1, studentId);
                psDup.setInt(2, offeringId);
                try (ResultSet rs = psDup.executeQuery()) {
                    if (rs.next()) {
                        String st = rs.getString("status");
                        if ("ENROLLED".equalsIgnoreCase(st)) {
                            conn.rollback();
                            return "Student is already enrolled in this course offering.";
                        }
                    }
                }
            }

            List<Integer> prereqs = new ArrayList<>();
            try (PreparedStatement psPrereq = conn.prepareStatement(checkPrereqSql)) {
                psPrereq.setInt(1, courseId);
                try (ResultSet rs = psPrereq.executeQuery()) {
                    while (rs.next()) {
                        prereqs.add(rs.getInt("prerequisite_course_id"));
                    }
                }
            }

            for (int pId : prereqs) {
                try (PreparedStatement psPass = conn.prepareStatement(checkPassedPrereqSql)) {
                    psPass.setInt(1, studentId);
                    psPass.setInt(2, pId);
                    try (ResultSet rs = psPass.executeQuery()) {
                        if (!rs.next() || rs.getInt(1) == 0) {
                            conn.rollback();
                            return "Prerequisite not met: You must pass course prerequisite (ID " + pId + ") before enrolling.";
                        }
                    }
                }
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
                return "Cannot enroll: Course offering is at maximum capacity (" + capacity + ").";
            }

            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setInt(1, studentId);
                psInsert.setInt(2, offeringId);
                psInsert.setDate(3, Date.valueOf(LocalDate.now()));
                psInsert.executeUpdate();
            }

            conn.commit();
            return "SUCCESS";

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
            return "Database transaction error: " + e.getMessage();
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

    public boolean dropCourse(int enrollmentId, int studentId) {
        String sql = "UPDATE enrollments SET status = 'DROPPED' WHERE enrollment_id = ? AND student_id = ? AND status = 'ENROLLED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error dropping course: " + e.getMessage());
            return false;
        }
    }

    public boolean submitGrade(int enrollmentId, String grade) {
        String sql = "UPDATE enrollments SET grade = ?, status = 'COMPLETED' WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, grade);
            ps.setInt(2, enrollmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error submitting grade: " + e.getMessage());
            return false;
        }
    }

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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToEnrollment(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting student enrollments: " + e.getMessage());
        }
        return list;
    }

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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, offeringId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToEnrollment(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting offering enrollments: " + e.getMessage());
        }
        return list;
    }

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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToEnrollment(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting all enrollments: " + e.getMessage());
        }
        return list;
    }

    public int countActiveEnrollments() {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE status = 'ENROLLED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error counting active enrollments: " + e.getMessage());
        }
        return 0;
    }

    public void viewEnrollments() {
        List<Enrollment> list = getAllEnrollments();
        System.out.println("\n--- ENROLLMENT RECORDS ---");
        System.out.printf("%-6s %-12s %-20s %-10s %-12s %-12s %-8s\n", "ID", "Roll No", "Student Name", "Course", "Semester", "Status", "Grade");
        System.out.println("-----------------------------------------------------------------------------------------");
        for (Enrollment e : list) {
            System.out.printf("%-6d %-12s %-20s %-10s %-12s %-12s %-8s\n",
                    e.getEnrollmentId(), e.getStudentRoll(), e.getStudentName(), e.getCourseCode(),
                    e.getSemesterName(), e.getStatus(), e.getGrade() != null ? e.getGrade() : "-");
        }
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
