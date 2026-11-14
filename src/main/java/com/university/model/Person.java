package com.university.model;

public abstract class Person {

    private int userId;
    private String username;
    private String fullName;
    private String email;
    private Integer departmentId;
    private boolean active = true;

    public Person() {
    }

    public Person(int userId, String username, String fullName, String email, Integer departmentId, boolean active) {
        this.userId = userId;
        this.username = username;
        setFullName(fullName);
        setEmail(email);
        this.departmentId = departmentId;
        this.active = active;
    }

    public abstract String getRoleName();

    public abstract String getDashboardMessage();

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username != null) {
            this.username = username.trim();
        } else {
            this.username = "";
        }
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        this.fullName = fullName.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address: " + email);
        }
        this.email = email.trim();
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return fullName + " (" + getRoleName() + ")";
    }
}
