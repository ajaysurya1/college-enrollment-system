package com.university.service;

import com.university.dao.FacultyDAO;
import com.university.dao.impl.FacultyDAOImpl;
import com.university.model.Faculty;
import com.university.util.PasswordUtil;

import java.util.List;

public class FacultyService {

    private final FacultyDAO facultyDAO;

    public FacultyService() {
        this.facultyDAO = new FacultyDAOImpl();
    }

    public FacultyService(FacultyDAO facultyDAO) {
        this.facultyDAO = facultyDAO;
    }

    public boolean registerFaculty(String username, String rawPassword, String fullName, String email,
                                   Integer departmentId, String designation) {

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (rawPassword == null || rawPassword.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long");
        }
        if (designation == null || designation.trim().isEmpty()) {
            designation = "Lecturer";
        }

        Faculty faculty = new Faculty(0, username.trim(), fullName, email, departmentId, true, designation.trim());
        String hash = PasswordUtil.hashPassword(rawPassword);
        return facultyDAO.addFaculty(faculty, hash);
    }

    public Faculty getFacultyById(int userId) {
        return facultyDAO.getFacultyById(userId);
    }

    public List<Faculty> getAllFaculty() {
        return facultyDAO.getAllFaculty();
    }

    public boolean updateFaculty(Faculty faculty) {
        if (faculty == null || faculty.getUserId() <= 0) {
            throw new IllegalArgumentException("Invalid faculty record for update");
        }
        return facultyDAO.updateFaculty(faculty);
    }

    public boolean deactivateFaculty(int userId) {
        return facultyDAO.deleteFaculty(userId);
    }

    public int getFacultyCount() {
        return facultyDAO.countFaculty();
    }
}
