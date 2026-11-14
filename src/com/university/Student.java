package com.university;

public class Student extends Person {

    private String rollNumber;
    private String programme;
    private int yearOfStudy;

    public Student() {
        super();
    }

    public Student(int userId, String username, String fullName, String email, Integer departmentId, boolean active,
                   String rollNumber, String programme, int yearOfStudy) {
        super(userId, username, fullName, email, departmentId, active);
        this.rollNumber = rollNumber;
        this.programme = programme;
        this.yearOfStudy = yearOfStudy;
    }

    @Override
    public String getRoleName() {
        return "STUDENT";
    }

    @Override
    public String getDashboardMessage() {
        return "Welcome Student " + getFullName() + " | Roll No: " + rollNumber;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }
}
