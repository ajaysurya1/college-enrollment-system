package com.university.service;

import com.university.dao.DepartmentDAO;
import com.university.dao.impl.DepartmentDAOImpl;
import com.university.model.Department;

import java.util.List;

public class DepartmentService {

    private final DepartmentDAO departmentDAO;

    public DepartmentService() {
        this.departmentDAO = new DepartmentDAOImpl();
    }

    public DepartmentService(DepartmentDAO departmentDAO) {
        this.departmentDAO = departmentDAO;
    }

    public boolean createDepartment(String code, String name) {
        if (code == null || code.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Department code and name cannot be empty");
        }
        Department dept = new Department(0, code.trim().toUpperCase(), name.trim());
        return departmentDAO.addDepartment(dept);
    }

    public Department getDepartmentById(int id) {
        return departmentDAO.getDepartmentById(id);
    }

    public List<Department> getAllDepartments() {
        return departmentDAO.getAllDepartments();
    }

    public boolean updateDepartment(int id, String code, String name) {
        if (code == null || code.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Department code and name cannot be empty");
        }
        Department dept = new Department(id, code.trim().toUpperCase(), name.trim());
        return departmentDAO.updateDepartment(dept);
    }

    public boolean deleteDepartment(int id) {
        return departmentDAO.deleteDepartment(id);
    }
}
