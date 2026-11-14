package com.university.service;

import com.university.dao.CourseDAO;
import com.university.dao.impl.CourseDAOImpl;
import com.university.model.Course;

import java.util.List;

public class CourseService {

    private final CourseDAO courseDAO;

    public CourseService() {
        this.courseDAO = new CourseDAOImpl();
    }

    public CourseService(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    public boolean createCourse(String code, String title, int credits, int departmentId, String description) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty");
        }
        Course course = new Course(0, code.trim().toUpperCase(), title, credits, departmentId, description);
        return courseDAO.addCourse(course);
    }

    public Course getCourseById(int courseId) {
        return courseDAO.getCourseById(courseId);
    }

    public List<Course> getAllCourses() {
        return courseDAO.getAllCourses();
    }

    public List<Course> getCoursesByDepartment(int departmentId) {
        return courseDAO.getCoursesByDepartment(departmentId);
    }

    public boolean updateCourse(Course course) {
        if (course == null || course.getCourseId() <= 0) {
            throw new IllegalArgumentException("Invalid course for update");
        }
        return courseDAO.updateCourse(course);
    }

    public boolean deleteCourse(int courseId) {
        return courseDAO.deleteCourse(courseId);
    }

    public List<Course> getPrerequisites(int courseId) {
        return courseDAO.getPrerequisiteCourses(courseId);
    }

    public boolean addPrerequisite(int courseId, int prereqCourseId) {
        if (courseId == prereqCourseId) {
            throw new IllegalArgumentException("A course cannot be its own prerequisite");
        }
        return courseDAO.addPrerequisite(courseId, prereqCourseId);
    }

    public boolean removePrerequisite(int courseId, int prereqCourseId) {
        return courseDAO.removePrerequisite(courseId, prereqCourseId);
    }

    public int getCourseCount() {
        return courseDAO.countCourses();
    }
}
