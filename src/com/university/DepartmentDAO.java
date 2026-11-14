package com.university;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public boolean addDepartment(Department dept) {
        String sql = "INSERT INTO departments (department_code, department_name) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, dept.getDepartmentCode());
            ps.setString(2, dept.getDepartmentName());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        dept.setDepartmentId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error adding department: " + e.getMessage());
        }
        return false;
    }

    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM departments ORDER BY department_code";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Department(
                        rs.getInt("department_id"),
                        rs.getString("department_code"),
                        rs.getString("department_name")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching departments: " + e.getMessage());
        }
        return list;
    }

    public Department getDepartmentById(int id) {
        String sql = "SELECT * FROM departments WHERE department_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Department(
                            rs.getInt("department_id"),
                            rs.getString("department_code"),
                            rs.getString("department_name")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting department: " + e.getMessage());
        }
        return null;
    }
}
