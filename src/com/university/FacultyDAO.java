package com.university;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FacultyDAO {

    public boolean addFaculty(Faculty faculty, String password) {
        String insertUserSql = "INSERT INTO users (username, password_hash, full_name, email, role, department_id, is_active) " +
                "VALUES (?, ?, ?, ?, 'FACULTY', ?, ?)";
        String insertFacultySql = "INSERT INTO faculty (user_id, designation) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int userId = 0;
            try (PreparedStatement psUser = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, faculty.getUsername());
                psUser.setString(2, "$2a$10$QjmeBmqAvygkHclaXGqqces1wSFhscH388jWeBESAs.Debt0/P7Xi");
                psUser.setString(3, faculty.getFullName());
                psUser.setString(4, faculty.getEmail());
                if (faculty.getDepartmentId() != null) {
                    psUser.setInt(5, faculty.getDepartmentId());
                } else {
                    psUser.setNull(5, java.sql.Types.INTEGER);
                }
                psUser.setBoolean(6, faculty.isActive());
                psUser.executeUpdate();

                try (ResultSet rs = psUser.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getInt(1);
                        faculty.setUserId(userId);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement psFaculty = conn.prepareStatement(insertFacultySql)) {
                psFaculty.setInt(1, userId);
                psFaculty.setString(2, faculty.getDesignation());
                psFaculty.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
            System.out.println("Error adding faculty: " + e.getMessage());
            return false;
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

    public List<Faculty> getAllFaculty() {
        List<Faculty> list = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.department_id, u.is_active, f.designation " +
                "FROM users u JOIN faculty f ON u.user_id = f.user_id ORDER BY u.full_name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Faculty(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        (Integer) rs.getObject("department_id"),
                        rs.getBoolean("is_active"),
                        rs.getString("designation")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error getting faculty: " + e.getMessage());
        }
        return list;
    }

    public Faculty getFacultyById(int userId) {
        String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.department_id, u.is_active, f.designation " +
                "FROM users u JOIN faculty f ON u.user_id = f.user_id WHERE u.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Faculty(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            (Integer) rs.getObject("department_id"),
                            rs.getBoolean("is_active"),
                            rs.getString("designation")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding faculty: " + e.getMessage());
        }
        return null;
    }

    public boolean updateFaculty(Faculty faculty) {
        String updateUserSql = "UPDATE users SET full_name = ?, email = ?, department_id = ?, is_active = ? WHERE user_id = ?";
        String updateFacultySql = "UPDATE faculty SET designation = ? WHERE user_id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psUser = conn.prepareStatement(updateUserSql)) {
                psUser.setString(1, faculty.getFullName());
                psUser.setString(2, faculty.getEmail());
                if (faculty.getDepartmentId() != null) {
                    psUser.setInt(3, faculty.getDepartmentId());
                } else {
                    psUser.setNull(3, java.sql.Types.INTEGER);
                }
                psUser.setBoolean(4, faculty.isActive());
                psUser.setInt(5, faculty.getUserId());
                psUser.executeUpdate();
            }

            try (PreparedStatement psFaculty = conn.prepareStatement(updateFacultySql)) {
                psFaculty.setString(1, faculty.getDesignation());
                psFaculty.setInt(2, faculty.getUserId());
                psFaculty.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
            System.out.println("Error updating faculty: " + e.getMessage());
            return false;
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

    public boolean deleteFaculty(int userId) {
        String sql = "UPDATE users SET is_active = FALSE WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deactivating faculty: " + e.getMessage());
            return false;
        }
    }

    public int countFaculty() {
        String sql = "SELECT COUNT(*) FROM faculty f JOIN users u ON f.user_id = u.user_id WHERE u.is_active = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error counting faculty: " + e.getMessage());
        }
        return 0;
    }

    public void viewFaculty() {
        List<Faculty> list = getAllFaculty();
        System.out.println("\n--- FACULTY DIRECTORY ---");
        System.out.printf("%-6s %-22s %-24s %-22s\n", "ID", "Name", "Email", "Designation");
        System.out.println("--------------------------------------------------------------------------");
        for (Faculty f : list) {
            System.out.printf("%-6d %-22s %-24s %-22s\n",
                    f.getUserId(), f.getFullName(), f.getEmail(), f.getDesignation());
        }
    }
}
