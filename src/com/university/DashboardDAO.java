package com.university;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardDAO {

    public Map<String, Integer> getSystemStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("students", 0);
        stats.put("faculty", 0);
        stats.put("courses", 0);
        stats.put("enrollments", 0);
        stats.put("offerings", 0);
        stats.put("departments", 0);

        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM students s JOIN users u ON s.user_id = u.user_id WHERE u.is_active = TRUE");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats.put("students", rs.getInt(1));
            }

            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM faculty f JOIN users u ON f.user_id = u.user_id WHERE u.is_active = TRUE");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats.put("faculty", rs.getInt(1));
            }

            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM courses");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats.put("courses", rs.getInt(1));
            }

            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM enrollments WHERE status = 'ENROLLED'");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats.put("enrollments", rs.getInt(1));
            }

            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM course_offerings");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats.put("offerings", rs.getInt(1));
            }

            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM departments");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats.put("departments", rs.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching dashboard stats: " + e.getMessage());
        }

        return stats;
    }

    public List<String> getRecentActivity() {
        List<String> activities = new ArrayList<>();
        String sql = "SELECT e.enrollment_date, u.full_name, c.course_code, e.status " +
                "FROM enrollments e " +
                "JOIN users u ON e.student_id = u.user_id " +
                "JOIN course_offerings co ON e.offering_id = co.offering_id " +
                "JOIN courses c ON co.course_id = c.course_id " +
                "ORDER BY e.enrollment_id DESC LIMIT 6";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String act = "[" + rs.getDate("enrollment_date") + "] " +
                        rs.getString("full_name") + " -> " +
                        rs.getString("course_code") + " (" + rs.getString("status") + ")";
                activities.add(act);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching recent activity: " + e.getMessage());
        }
        return activities;
    }
}
