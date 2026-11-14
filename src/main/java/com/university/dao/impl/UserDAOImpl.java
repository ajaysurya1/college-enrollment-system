package com.university.dao.impl;

import com.university.config.DatabaseConnection;
import com.university.dao.UserDAO;
import com.university.exception.DatabaseException;
import com.university.model.Administrator;
import com.university.model.Faculty;
import com.university.model.Person;
import com.university.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAOImpl implements UserDAO {

    @Override
    public Person findByUsername(String username) {
        String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.role, u.department_id, u.is_active, " +
                "s.roll_number, s.programme, s.year_of_study, f.designation " +
                "FROM users u " +
                "LEFT JOIN students s ON u.user_id = s.user_id " +
                "LEFT JOIN faculty f ON u.user_id = f.user_id " +
                "WHERE u.username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPerson(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding user by username", e);
        }
        return null;
    }

    @Override
    public Person findById(int userId) {
        String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.role, u.department_id, u.is_active, " +
                "s.roll_number, s.programme, s.year_of_study, f.designation " +
                "FROM users u " +
                "LEFT JOIN students s ON u.user_id = s.user_id " +
                "LEFT JOIN faculty f ON u.user_id = f.user_id " +
                "WHERE u.user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPerson(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding user by id", e);
        }
        return null;
    }

    @Override
    public String getPasswordHash(String username) {
        String sql = "SELECT password_hash FROM users WHERE username = ? AND is_active = TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving password hash", e);
        }
        return null;
    }

    @Override
    public boolean updatePassword(int userId, String newHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating password", e);
        }
    }

    @Override
    public int countByRole(String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ? AND is_active = TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting users by role", e);
        }
        return 0;
    }

    private Person mapRowToPerson(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String username = rs.getString("username");
        String fullName = rs.getString("full_name");
        String email = rs.getString("email");
        String role = rs.getString("role");
        Integer departmentId = (Integer) rs.getObject("department_id");
        boolean active = rs.getBoolean("is_active");

        if ("STUDENT".equalsIgnoreCase(role)) {
            String roll = rs.getString("roll_number");
            String prog = rs.getString("programme");
            int year = rs.getInt("year_of_study");
            return new Student(userId, username, fullName, email, departmentId, active, roll, prog, year > 0 ? year : 1);
        } else if ("FACULTY".equalsIgnoreCase(role)) {
            String designation = rs.getString("designation");
            return new Faculty(userId, username, fullName, email, departmentId, active, designation != null ? designation : "Lecturer");
        } else {
            return new Administrator(userId, username, fullName, email, departmentId, active);
        }
    }
}
