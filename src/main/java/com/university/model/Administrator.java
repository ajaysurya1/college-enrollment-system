package com.university.model;

public class Administrator extends Person {

    public Administrator() {
        super();
    }

    public Administrator(int userId, String username, String fullName, String email, Integer departmentId, boolean active) {
        super(userId, username, fullName, email, departmentId, active);
    }

    @Override
    public String getRoleName() {
        return "ADMIN";
    }

    @Override
    public String getDashboardMessage() {
        return "Welcome Administrator " + getFullName() + " - University Management Portal";
    }
}
