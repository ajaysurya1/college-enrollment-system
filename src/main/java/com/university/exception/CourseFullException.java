package com.university.exception;

public class CourseFullException extends EnrollmentException {
    public CourseFullException(String message) {
        super(message);
    }
}
