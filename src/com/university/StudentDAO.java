package com.university;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public boolean addStudent(Student student, String password) {
        String insertUserSql = "INSERT INTO users (username, password_hash, full_name, email, role, department_id, is_active) " +
                "VALUES (?, ?, ?, ?, 'STUDENT', ?, ?)";
        String insertStudentSql = "INSERT INTO students (user_id, roll_number, programme, year_of_study) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int userId = 0;
            try (PreparedStatement psUser = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, student.getUsername());
                psUser.setString(2, "$2a$10$KQ2ye8zf7r1L8PEbe6iavumwLi9XpBXJZdqpP4n2xfeuIcXphNO4y");
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
            System.out.println("Error adding student: " + e.getMessage());
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

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.department_id, u.is_active, " +
                "s.roll_number, s.programme, s.year_of_study " +
                "FROM users u JOIN students s ON u.user_id = s.user_id ORDER BY s.roll_number";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        (Integer) rs.getObject("department_id"),
                        rs.getBoolean("is_active"),
                        rs.getString("roll_number"),
                        rs.getString("programme"),
                        rs.getInt("year_of_study")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error getting students: " + e.getMessage());
        }
        return list;
    }

    public Student getStudentById(int userId) {
        String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.department_id, u.is_active, " +
                "s.roll_number, s.programme, s.year_of_study " +
                "FROM users u JOIN students s ON u.user_id = s.user_id WHERE u.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            (Integer) rs.getObject("department_id"),
                            rs.getBoolean("is_active"),
                            rs.getString("roll_number"),
                            rs.getString("programme"),
                            rs.getInt("year_of_study")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding student: " + e.getMessage());
        }
        return null;
    }

    public Student getStudentByRoll(String rollNumber) {
        String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.department_id, u.is_active, " +
                "s.roll_number, s.programme, s.year_of_study " +
                "FROM users u JOIN students s ON u.user_id = s.user_id WHERE s.roll_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rollNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            (Integer) rs.getObject("department_id"),
                            rs.getBoolean("is_active"),
                            rs.getString("roll_number"),
                            rs.getString("programme"),
                            rs.getInt("year_of_study")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding student by roll: " + e.getMessage());
        }
        return null;
    }

    public boolean updateStudent(Student student) {
        String updateUserSql = "UPDATE users SET full_name = ?, email = ?, department_id = ?, is_active = ? WHERE user_id = ?";
        String updateStudentSql = "UPDATE students SET roll_number = ?, programme = ?, year_of_study = ? WHERE user_id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
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
            System.out.println("Error updating student: " + e.getMessage());
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

    public boolean deleteStudent(int userId) {
        String sql = "UPDATE users SET is_active = FALSE WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deactivating student: " + e.getMessage());
            return false;
        }
    }

    public int countStudents() {
        String sql = "SELECT COUNT(*) FROM students s JOIN users u ON s.user_id = u.user_id WHERE u.is_active = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error counting students: " + e.getMessage());
        }
        return 0;
    }

    public void viewStudents() {
        List<Student> students = getAllStudents();
        System.out.println("\n--- STUDENT DIRECTORY ---");
        System.out.printf("%-6s %-12s %-22s %-24s %-6s\n", "ID", "Roll No", "Name", "Email", "Year");
        System.out.println("-------------------------------------------------------------------------");
        for (Student s : students) {
            System.out.printf("%-6d %-12s %-22s %-24s %-6d\n",
                    s.getUserId(), s.getRollNumber(), s.getFullName(), s.getEmail(), s.getYearOfStudy());
        }
    }
}
