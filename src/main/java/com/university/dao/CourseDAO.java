package com.university.dao;

import com.university.model.Course;
import java.util.List;

public interface CourseDAO {
    boolean addCourse(Course course);
    Course getCourseById(int courseId);
    Course getCourseByCode(String code);
    List<Course> getAllCourses();
    List<Course> getCoursesByDepartment(int departmentId);
    boolean updateCourse(Course course);
    boolean deleteCourse(int courseId);
    List<Integer> getPrerequisiteIds(int courseId);
    List<Course> getPrerequisiteCourses(int courseId);
    boolean addPrerequisite(int courseId, int prereqCourseId);
    boolean removePrerequisite(int courseId, int prereqCourseId);
    int countCourses();
}
