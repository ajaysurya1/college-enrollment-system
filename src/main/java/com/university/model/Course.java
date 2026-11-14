package com.university.model;

public class Course {

    private int courseId;
    private String courseCode;
    private String courseTitle;
    private int credits;
    private int departmentId;
    private String description;
    private String departmentName;

    public Course() {
    }

    public Course(int courseId, String courseCode, String courseTitle, int credits, int departmentId, String description) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        setCourseTitle(courseTitle);
        setCredits(credits);
        this.departmentId = departmentId;
        this.description = description;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        if (courseTitle == null || courseTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Course title cannot be empty");
        }
        this.courseTitle = courseTitle.trim();
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        if (credits <= 0) {
            throw new IllegalArgumentException("Credits must be a positive integer");
        }
        this.credits = credits;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @Override
    public String toString() {
        return courseCode + ": " + courseTitle + " (" + credits + " credits)";
    }
}
