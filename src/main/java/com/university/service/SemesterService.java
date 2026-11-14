package com.university.service;

import com.university.dao.SemesterDAO;
import com.university.dao.impl.SemesterDAOImpl;
import com.university.model.Semester;

import java.time.LocalDate;
import java.util.List;

public class SemesterService {

    private final SemesterDAO semesterDAO;

    public SemesterService() {
        this.semesterDAO = new SemesterDAOImpl();
    }

    public SemesterService(SemesterDAO semesterDAO) {
        this.semesterDAO = semesterDAO;
    }

    public boolean createSemester(String name, String year, LocalDate start, LocalDate end,
                                  LocalDate regStart, LocalDate regEnd) {

        if (name == null || name.trim().isEmpty() || year == null || year.trim().isEmpty()) {
            throw new IllegalArgumentException("Semester name and year are required");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        if (regStart.isAfter(regEnd)) {
            throw new IllegalArgumentException("Registration start cannot be after registration end");
        }

        Semester sem = new Semester(0, name.trim(), year.trim(), start, end, regStart, regEnd);
        return semesterDAO.addSemester(sem);
    }

    public Semester getSemesterById(int id) {
        return semesterDAO.getSemesterById(id);
    }

    public List<Semester> getAllSemesters() {
        return semesterDAO.getAllSemesters();
    }

    public Semester getCurrentSemester() {
        return semesterDAO.getCurrentSemester();
    }

    public boolean updateSemester(Semester sem) {
        if (sem == null || sem.getSemesterId() <= 0) {
            throw new IllegalArgumentException("Invalid semester for update");
        }
        return semesterDAO.updateSemester(sem);
    }

    public boolean deleteSemester(int id) {
        return semesterDAO.deleteSemester(id);
    }
}
