package com.university.dao;

import com.university.model.Enrollment;
import java.time.LocalDate;
import java.util.List;

public interface EnrollmentDAO {
    boolean enrollStudent(int studentId, int offeringId, LocalDate date);
    boolean isAlreadyEnrolled(int studentId, int offeringId);
    boolean hasCompletedCourse(int studentId, int courseId);
    boolean updateStatus(int enrollmentId, String status);
    boolean updateGrade(int enrollmentId, String grade, String status);
    Enrollment getEnrollmentById(int enrollmentId);
    List<Enrollment> getEnrollmentsByStudent(int studentId);
    List<Enrollment> getEnrollmentsByOffering(int offeringId);
    List<Enrollment> getAllEnrollments();
    int countActiveEnrollments();
}
