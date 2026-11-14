package com.university.model;

public class CourseOffering {

    private int offeringId;
    private int courseId;
    private int semesterId;
    private Integer facultyId;
    private String section;
    private int capacity;

    private String courseCode;
    private String courseTitle;
    private int credits;
    private String semesterName;
    private String facultyName;
    private int enrolledCount;

    public CourseOffering() {
    }

    public CourseOffering(int offeringId, int courseId, int semesterId, Integer facultyId, String section, int capacity) {
        this.offeringId = offeringId;
        this.courseId = courseId;
        this.semesterId = semesterId;
        this.facultyId = facultyId;
        this.section = section;
        setCapacity(capacity);
    }

    public int getAvailableSeats() {
        int seats = capacity - enrolledCount;
        return Math.max(seats, 0);
    }

    public int getOfferingId() {
        return offeringId;
    }

    public void setOfferingId(int offeringId) {
        this.offeringId = offeringId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public Integer getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(Integer facultyId) {
        this.facultyId = facultyId;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }
        this.capacity = capacity;
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
        this.courseTitle = courseTitle;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public int getEnrolledCount() {
        return enrolledCount;
    }

    public void setEnrolledCount(int enrolledCount) {
        this.enrolledCount = enrolledCount;
    }

    @Override
    public String toString() {
        return (courseCode != null ? courseCode : "Course " + courseId) + " - Sec " + section + " (" + (semesterName != null ? semesterName : "Sem " + semesterId) + ")";
    }
}
