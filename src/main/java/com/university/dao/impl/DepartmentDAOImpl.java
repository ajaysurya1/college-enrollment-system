package com.university.dao.impl;

import com.university.config.DatabaseConnection;
import com.university.dao.DepartmentDAO;
import com.university.exception.DatabaseException;
import com.university.model.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAOImpl implements DepartmentDAO {

    @Override
    public boolean addDepartment(Department dept) {
        String sql = "INSERT INTO departments (department_code, department_name) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, dept.getDepartmentCode());
            ps.setString(2, dept.getDepartmentName());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        dept.setDepartmentId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error adding department", e);
        }
        return false;
    }

    @Override
    public Department getDepartmentById(int id) {
        String sql = "SELECT department_id, department_code, department_name FROM departments WHERE department_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Department(rs.getInt("department_id"), rs.getString("department_code"), rs.getString("department_name"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting department by id", e);
        }
        return null;
    }

    @Override
    public Department getDepartmentByCode(String code) {
        String sql = "SELECT department_id, department_code, department_name FROM departments WHERE department_code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Department(rs.getInt("department_id"), rs.getString("department_code"), rs.getString("department_name"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting department by code", e);
        }
        return null;
    }

    @Override
    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT department_id, department_code, department_name FROM departments ORDER BY department_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Department(rs.getInt("department_id"), rs.getString("department_code"), rs.getString("department_name")));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching departments", e);
        }
        return list;
    }

    @Override
    public boolean updateDepartment(Department dept) {
        String sql = "UPDATE departments SET department_code = ?, department_name = ? WHERE department_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dept.getDepartmentCode());
            ps.setString(2, dept.getDepartmentName());
            ps.setInt(3, dept.getDepartmentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating department", e);
        }
    }

    @Override
    public boolean deleteDepartment(int id) {
        String sql = "DELETE FROM departments WHERE department_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting department", e);
        }
    }
}
