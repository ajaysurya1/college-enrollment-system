package com.university.dao.impl;

import com.university.config.DatabaseConnection;
import com.university.dao.CourseDAO;
import com.university.exception.DatabaseException;
import com.university.model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CourseDAOImpl implements CourseDAO {

    @Override
    public boolean addCourse(Course course) {
        String sql = "INSERT INTO courses (course_code, course_title, credits, department_id, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getCourseTitle());
            ps.setInt(3, course.getCredits());
            ps.setInt(4, course.getDepartmentId());
            ps.setString(5, course.getDescription());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        course.setCourseId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error adding course", e);
        }
        return false;
    }

    @Override
    public Course getCourseById(int courseId) {
        String sql = "SELECT c.course_id, c.course_code, c.course_title, c.credits, c.department_id, c.description, d.department_name " +
                "FROM courses c JOIN departments d ON c.department_id = d.department_id WHERE c.course_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCourse(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding course by ID", e);
        }
        return null;
    }

    @Override
    public Course getCourseByCode(String code) {
        String sql = "SELECT c.course_id, c.course_code, c.course_title, c.credits, c.department_id, c.description, d.department_name " +
                "FROM courses c JOIN departments d ON c.department_id = d.department_id WHERE c.course_code = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCourse(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding course by code", e);
        }
        return null;
    }

    @Override
    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.course_id, c.course_code, c.course_title, c.credits, c.department_id, c.description, d.department_name " +
                "FROM courses c JOIN departments d ON c.department_id = d.department_id ORDER BY c.course_code";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToCourse(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching all courses", e);
        }
        return list;
    }

    @Override
    public List<Course> getCoursesByDepartment(int departmentId) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.course_id, c.course_code, c.course_title, c.credits, c.department_id, c.description, d.department_name " +
                "FROM courses c JOIN departments d ON c.department_id = d.department_id WHERE c.department_id = ? ORDER BY c.course_code";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToCourse(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching courses by department", e);
        }
        return list;
    }

    @Override
    public boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET course_code = ?, course_title = ?, credits = ?, department_id = ?, description = ? WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getCourseTitle());
            ps.setInt(3, course.getCredits());
            ps.setInt(4, course.getDepartmentId());
            ps.setString(5, course.getDescription());
            ps.setInt(6, course.getCourseId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating course", e);
        }
    }

    @Override
    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting course", e);
        }
    }

    @Override
    public List<Integer> getPrerequisiteIds(int courseId) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT prerequisite_course_id FROM course_prerequisites WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("prerequisite_course_id"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching prerequisite IDs", e);
        }
        return list;
    }

    @Override
    public List<Course> getPrerequisiteCourses(int courseId) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.course_id, c.course_code, c.course_title, c.credits, c.department_id, c.description, d.department_name " +
                "FROM course_prerequisites cp " +
                "JOIN courses c ON cp.prerequisite_course_id = c.course_id " +
                "JOIN departments d ON c.department_id = d.department_id " +
                "WHERE cp.course_id = ? ORDER BY c.course_code";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToCourse(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching prerequisite courses", e);
        }
        return list;
    }

    @Override
    public boolean addPrerequisite(int courseId, int prereqCourseId) {
        if (courseId == prereqCourseId) {
            return false;
        }
        String sql = "INSERT INTO course_prerequisites (course_id, prerequisite_course_id) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE prerequisite_course_id = VALUES(prerequisite_course_id)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ps.setInt(2, prereqCourseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error adding prerequisite", e);
        }
    }

    @Override
    public boolean removePrerequisite(int courseId, int prereqCourseId) {
        String sql = "DELETE FROM course_prerequisites WHERE course_id = ? AND prerequisite_course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ps.setInt(2, prereqCourseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error removing prerequisite", e);
        }
    }

    @Override
    public int countCourses() {
        String sql = "SELECT COUNT(*) FROM courses";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting courses", e);
        }
        return 0;
    }

    private Course mapRowToCourse(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("course_id"));
        c.setCourseCode(rs.getString("course_code"));
        c.setCourseTitle(rs.getString("course_title"));
        c.setCredits(rs.getInt("credits"));
        c.setDepartmentId(rs.getInt("department_id"));
        c.setDescription(rs.getString("description"));
        c.setDepartmentName(rs.getString("department_name"));
        return c;
    }
}
