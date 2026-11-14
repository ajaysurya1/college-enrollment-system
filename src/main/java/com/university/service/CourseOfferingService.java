package com.university.service;

import com.university.dao.CourseOfferingDAO;
import com.university.dao.impl.CourseOfferingDAOImpl;
import com.university.model.CourseOffering;

import java.util.List;

public class CourseOfferingService {

    private final CourseOfferingDAO offeringDAO;

    public CourseOfferingService() {
        this.offeringDAO = new CourseOfferingDAOImpl();
    }

    public CourseOfferingService(CourseOfferingDAO offeringDAO) {
        this.offeringDAO = offeringDAO;
    }

    public boolean createOffering(int courseId, int semesterId, Integer facultyId, String section, int capacity) {
        if (section == null || section.trim().isEmpty()) {
            section = "A";
        }
        CourseOffering offering = new CourseOffering(0, courseId, semesterId, facultyId, section.trim().toUpperCase(), capacity);
        return offeringDAO.addOffering(offering);
    }

    public CourseOffering getOfferingById(int id) {
        return offeringDAO.getOfferingById(id);
    }

    public List<CourseOffering> getAllOfferings() {
        return offeringDAO.getAllOfferings();
    }

    public List<CourseOffering> getOfferingsBySemester(int semesterId) {
        return offeringDAO.getOfferingsBySemester(semesterId);
    }

    public List<CourseOffering> getOfferingsByFaculty(int facultyId) {
        return offeringDAO.getOfferingsByFaculty(facultyId);
    }

    public boolean updateOffering(CourseOffering offering) {
        if (offering == null || offering.getOfferingId() <= 0) {
            throw new IllegalArgumentException("Invalid offering for update");
        }
        return offeringDAO.updateOffering(offering);
    }

    public boolean assignFaculty(int offeringId, Integer facultyId) {
        CourseOffering off = offeringDAO.getOfferingById(offeringId);
        if (off == null) {
            return false;
        }
        off.setFacultyId(facultyId);
        return offeringDAO.updateOffering(off);
    }

    public boolean deleteOffering(int id) {
        return offeringDAO.deleteOffering(id);
    }
}
