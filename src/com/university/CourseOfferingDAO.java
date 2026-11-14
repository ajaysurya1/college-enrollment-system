package com.university;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CourseOfferingDAO {

    public boolean addOffering(CourseOffering offering) {
        String sql = "INSERT INTO course_offerings (course_id, semester_id, faculty_id, section, capacity) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, offering.getCourseId());
            ps.setInt(2, offering.getSemesterId());
            if (offering.getFacultyId() != null) {
                ps.setInt(3, offering.getFacultyId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setString(4, offering.getSection());
            ps.setInt(5, offering.getCapacity());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        offering.setOfferingId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error adding offering: " + e.getMessage());
        }
        return false;
    }

    public CourseOffering getOfferingById(int id) {
        String sql = "SELECT co.offering_id, co.course_id, co.semester_id, co.faculty_id, co.section, co.capacity, " +
                "c.course_code, c.course_title, c.credits, s.semester_name, u.full_name AS faculty_name, " +
                "(SELECT COUNT(*) FROM enrollments e WHERE e.offering_id = co.offering_id AND e.status = 'ENROLLED') AS enrolled_count " +
                "FROM course_offerings co " +
                "JOIN courses c ON co.course_id = c.course_id " +
                "JOIN semesters s ON co.semester_id = s.semester_id " +
                "LEFT JOIN users u ON co.faculty_id = u.user_id " +
                "WHERE co.offering_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToOffering(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting offering: " + e.getMessage());
        }
        return null;
    }

    public List<CourseOffering> getAllOfferings() {
        List<CourseOffering> list = new ArrayList<>();
        String sql = "SELECT co.offering_id, co.course_id, co.semester_id, co.faculty_id, co.section, co.capacity, " +
                "c.course_code, c.course_title, c.credits, s.semester_name, u.full_name AS faculty_name, " +
                "(SELECT COUNT(*) FROM enrollments e WHERE e.offering_id = co.offering_id AND e.status = 'ENROLLED') AS enrolled_count " +
                "FROM course_offerings co " +
                "JOIN courses c ON co.course_id = c.course_id " +
                "JOIN semesters s ON co.semester_id = s.semester_id " +
                "LEFT JOIN users u ON co.faculty_id = u.user_id " +
                "ORDER BY s.start_date DESC, c.course_code, co.section";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToOffering(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting offerings: " + e.getMessage());
        }
        return list;
    }

    public List<CourseOffering> getOfferingsBySemester(int semesterId) {
        List<CourseOffering> list = new ArrayList<>();
        String sql = "SELECT co.offering_id, co.course_id, co.semester_id, co.faculty_id, co.section, co.capacity, " +
                "c.course_code, c.course_title, c.credits, s.semester_name, u.full_name AS faculty_name, " +
                "(SELECT COUNT(*) FROM enrollments e WHERE e.offering_id = co.offering_id AND e.status = 'ENROLLED') AS enrolled_count " +
                "FROM course_offerings co " +
                "JOIN courses c ON co.course_id = c.course_id " +
                "JOIN semesters s ON co.semester_id = s.semester_id " +
                "LEFT JOIN users u ON co.faculty_id = u.user_id " +
                "WHERE co.semester_id = ? " +
                "ORDER BY c.course_code, co.section";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, semesterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToOffering(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting offerings for semester: " + e.getMessage());
        }
        return list;
    }

    public List<CourseOffering> getOfferingsByFaculty(int facultyId) {
        List<CourseOffering> list = new ArrayList<>();
        String sql = "SELECT co.offering_id, co.course_id, co.semester_id, co.faculty_id, co.section, co.capacity, " +
                "c.course_code, c.course_title, c.credits, s.semester_name, u.full_name AS faculty_name, " +
                "(SELECT COUNT(*) FROM enrollments e WHERE e.offering_id = co.offering_id AND e.status IN ('ENROLLED', 'COMPLETED')) AS enrolled_count " +
                "FROM course_offerings co " +
                "JOIN courses c ON co.course_id = c.course_id " +
                "JOIN semesters s ON co.semester_id = s.semester_id " +
                "LEFT JOIN users u ON co.faculty_id = u.user_id " +
                "WHERE co.faculty_id = ? " +
                "ORDER BY s.start_date DESC, c.course_code";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, facultyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToOffering(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting offerings by faculty: " + e.getMessage());
        }
        return list;
    }

    public boolean updateOffering(CourseOffering offering) {
        String sql = "UPDATE course_offerings SET course_id = ?, semester_id = ?, faculty_id = ?, section = ?, capacity = ? " +
                "WHERE offering_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, offering.getCourseId());
            ps.setInt(2, offering.getSemesterId());
            if (offering.getFacultyId() != null) {
                ps.setInt(3, offering.getFacultyId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setString(4, offering.getSection());
            ps.setInt(5, offering.getCapacity());
            ps.setInt(6, offering.getOfferingId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating offering: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteOffering(int id) {
        String sql = "DELETE FROM course_offerings WHERE offering_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting offering: " + e.getMessage());
            return false;
        }
    }

    public void viewOfferings() {
        List<CourseOffering> list = getAllOfferings();
        System.out.println("\n--- COURSE OFFERINGS ---");
        System.out.printf("%-6s %-10s %-28s %-14s %-8s %-10s %-10s\n", "ID", "Code", "Title", "Semester", "Section", "Capacity", "Enrolled");
        System.out.println("--------------------------------------------------------------------------------------------------");
        for (CourseOffering o : list) {
            System.out.printf("%-6d %-10s %-28s %-14s %-8s %-10d %-10d\n",
                    o.getOfferingId(), o.getCourseCode(), o.getCourseTitle(), o.getSemesterName(),
                    o.getSection(), o.getCapacity(), o.getEnrolledCount());
        }
    }

    private CourseOffering mapRowToOffering(ResultSet rs) throws SQLException {
        CourseOffering o = new CourseOffering();
        o.setOfferingId(rs.getInt("offering_id"));
        o.setCourseId(rs.getInt("course_id"));
        o.setSemesterId(rs.getInt("semester_id"));
        o.setFacultyId((Integer) rs.getObject("faculty_id"));
        o.setSection(rs.getString("section"));
        o.setCapacity(rs.getInt("capacity"));
        o.setCourseCode(rs.getString("course_code"));
        o.setCourseTitle(rs.getString("course_title"));
        o.setCredits(rs.getInt("credits"));
        o.setSemesterName(rs.getString("semester_name"));
        o.setFacultyName(rs.getString("faculty_name"));
        o.setEnrolledCount(rs.getInt("enrolled_count"));
        return o;
    }
}
