package com.university;

import java.time.LocalDate;

public class Semester {

    private int semesterId;
    private String semesterName;
    private String academicYear;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate registrationStart;
    private LocalDate registrationEnd;

    public Semester() {
    }

    public Semester(int semesterId, String semesterName, String academicYear,
                    LocalDate startDate, LocalDate endDate,
                    LocalDate registrationStart, LocalDate registrationEnd) {
        this.semesterId = semesterId;
        this.semesterName = semesterName;
        this.academicYear = academicYear;
        this.startDate = startDate;
        this.endDate = endDate;
        this.registrationStart = registrationStart;
        this.registrationEnd = registrationEnd;
    }

    public boolean isRegistrationOpen() {
        if (registrationStart == null || registrationEnd == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return (!today.isBefore(registrationStart)) && (!today.isAfter(registrationEnd));
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getRegistrationStart() {
        return registrationStart;
    }

    public void setRegistrationStart(LocalDate registrationStart) {
        this.registrationStart = registrationStart;
    }

    public LocalDate getRegistrationEnd() {
        return registrationEnd;
    }

    public void setRegistrationEnd(LocalDate registrationEnd) {
        this.registrationEnd = registrationEnd;
    }

    @Override
    public String toString() {
        return semesterName + " (" + academicYear + ")";
    }
}
