package com.university.model;

public class Faculty extends Person {

    private String designation;

    public Faculty() {
        super();
    }

    public Faculty(int userId, String username, String fullName, String email, Integer departmentId, boolean active,
                   String designation) {
        super(userId, username, fullName, email, departmentId, active);
        this.designation = designation;
    }

    @Override
    public String getRoleName() {
        return "FACULTY";
    }

    @Override
    public String getDashboardMessage() {
        return "Welcome Faculty " + getFullName() + " (" + designation + ")";
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
