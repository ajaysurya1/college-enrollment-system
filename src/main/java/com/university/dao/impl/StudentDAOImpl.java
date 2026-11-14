package com.university.dao.impl;

import com.university.config.DatabaseConnection;
import com.university.dao.StudentDAO;
import com.university.exception.DatabaseException;
import com.university.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentDAOImpl implements StudentDAO {

    @Override
    public boolean addStudent(Student student, String passwordHash) {
        String insertUserSql = "INSERT INTO users (username, password_hash, full_name, email, role, department_id, is_active) " +
                "VALUES (?, ?, ?, ?, 'STUDENT', ?, ?)";
        String insertStudentSql = "INSERT INTO students (user_id, roll_number, programme, year_of_study) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int userId;
            try (PreparedStatement psUser = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, student.getUsername());
                psUser.setString(2, passwordHash);
                psUser.setString(3, student.getFullName());
                psUser.setString(4, student.getEmail());
                if (student.getDepartmentId() != null) {
                    psUser.setInt(5, student.getDepartmentId());
                } else {
                    psUser.setNull(5, java.sql.Types.INTEGER);
                }
                psUser.setBoolean(6, student.isActive());
                psUser.executeUpdate();

                try (ResultSet rs = psUser.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getInt(1);
                        student.setUserId(userId);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement psStudent = conn.prepareStatement(insertStudentSql)) {
                psStudent.setInt(1, userId);
                psStudent.setString(2, student.getRollNumber());
                psStudent.setString(3, student.getProgramme());
                psStudent.setInt(4, student.getYearOfStudy());
                psStudent.executeUpdate();
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
            throw new DatabaseException("Failed to add student", e);
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
    public Student getStudentById(int userId) {
        String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.department_id, u.is_active, " +
                "s.roll_number, s.programme, s.year_of_study " +
                "FROM users u JOIN students s ON u.user_id = s.user_id WHERE u.user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudent(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding student by ID", e);
        }
        return null;
    }

    @Override
    public Student getStudentByRoll(String rollNumber) {
        String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.department_id, u.is_active, " +
                "s.roll_number, s.programme, s.year_of_study " +
                "FROM users u JOIN students s ON u.user_id = s.user_id WHERE s.roll_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rollNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudent(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding student by roll number", e);
        }
        return null;
    }

    @Override
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.department_id, u.is_active, " +
                "s.roll_number, s.programme, s.year_of_study " +
                "FROM users u JOIN students s ON u.user_id = s.user_id ORDER BY s.roll_number";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToStudent(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching all students", e);
        }
        return list;
    }

    @Override
    public boolean updateStudent(Student student) {
        String updateUserSql = "UPDATE users SET full_name = ?, email = ?, department_id = ?, is_active = ? WHERE user_id = ?";
        String updateStudentSql = "UPDATE students SET roll_number = ?, programme = ?, year_of_study = ? WHERE user_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psUser = conn.prepareStatement(updateUserSql)) {
                psUser.setString(1, student.getFullName());
                psUser.setString(2, student.getEmail());
                if (student.getDepartmentId() != null) {
                    psUser.setInt(3, student.getDepartmentId());
                } else {
                    psUser.setNull(3, java.sql.Types.INTEGER);
                }
                psUser.setBoolean(4, student.isActive());
                psUser.setInt(5, student.getUserId());
                psUser.executeUpdate();
            }

            try (PreparedStatement psStudent = conn.prepareStatement(updateStudentSql)) {
                psStudent.setString(1, student.getRollNumber());
                psStudent.setString(2, student.getProgramme());
                psStudent.setInt(3, student.getYearOfStudy());
                psStudent.setInt(4, student.getUserId());
                psStudent.executeUpdate();
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
            throw new DatabaseException("Error updating student", e);
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
    public boolean deleteStudent(int userId) {
        String sql = "UPDATE users SET is_active = FALSE WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deactivating student", e);
        }
    }

    @Override
    public int countStudents() {
        String sql = "SELECT COUNT(*) FROM students s JOIN users u ON s.user_id = u.user_id WHERE u.is_active = TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting students", e);
        }
        return 0;
    }

    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String username = rs.getString("username");
        String fullName = rs.getString("full_name");
        String email = rs.getString("email");
        Integer deptId = (Integer) rs.getObject("department_id");
        boolean active = rs.getBoolean("is_active");
        String rollNumber = rs.getString("roll_number");
        String programme = rs.getString("programme");
        int year = rs.getInt("year_of_study");
        return new Student(userId, username, fullName, email, deptId, active, rollNumber, programme, year > 0 ? year : 1);
    }
}
