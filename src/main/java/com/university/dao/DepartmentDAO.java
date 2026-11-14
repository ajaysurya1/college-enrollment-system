package com.university.dao;

import com.university.model.Department;
import java.util.List;

public interface DepartmentDAO {
    boolean addDepartment(Department dept);
    Department getDepartmentById(int id);
    Department getDepartmentByCode(String code);
    List<Department> getAllDepartments();
    boolean updateDepartment(Department dept);
    boolean deleteDepartment(int id);
}
