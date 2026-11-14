package com.university;

public abstract class Person {

    protected int userId;
    protected String username;
    protected String fullName;
    protected String email;
    protected Integer departmentId;
    protected boolean active = true;

    public Person() {
    }

    public Person(int userId, String username, String fullName, String email, Integer departmentId, boolean active) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
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
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
