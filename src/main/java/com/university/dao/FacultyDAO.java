package com.university.dao;

import com.university.model.Faculty;
import java.util.List;

public interface FacultyDAO {
    boolean addFaculty(Faculty faculty, String passwordHash);
    Faculty getFacultyById(int userId);
    List<Faculty> getAllFaculty();
    boolean updateFaculty(Faculty faculty);
    boolean deleteFaculty(int userId);
    int countFaculty();
}
