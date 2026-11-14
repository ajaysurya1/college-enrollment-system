package com.university.dao;

import com.university.model.CourseOffering;
import java.util.List;

public interface CourseOfferingDAO {
    boolean addOffering(CourseOffering offering);
    CourseOffering getOfferingById(int id);
    List<CourseOffering> getAllOfferings();
    List<CourseOffering> getOfferingsBySemester(int semesterId);
    List<CourseOffering> getOfferingsByFaculty(int facultyId);
    boolean updateOffering(CourseOffering offering);
    boolean deleteOffering(int id);
    int getEnrolledCount(int offeringId);
}
