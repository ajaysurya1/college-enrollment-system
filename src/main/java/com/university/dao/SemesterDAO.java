package com.university.dao;

import com.university.model.Semester;
import java.util.List;

public interface SemesterDAO {
    boolean addSemester(Semester sem);
    Semester getSemesterById(int id);
    List<Semester> getAllSemesters();
    Semester getCurrentSemester();
    boolean updateSemester(Semester sem);
    boolean deleteSemester(int id);
}
