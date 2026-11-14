package com.university.dao.impl;

import com.university.config.DatabaseConnection;
import com.university.dao.SemesterDAO;
import com.university.exception.DatabaseException;
import com.university.model.Semester;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SemesterDAOImpl implements SemesterDAO {

    @Override
    public boolean addSemester(Semester sem) {
        String sql = "INSERT INTO semesters (semester_name, academic_year, start_date, end_date, registration_start, registration_end) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, sem.getSemesterName());
            ps.setString(2, sem.getAcademicYear());
            ps.setDate(3, Date.valueOf(sem.getStartDate()));
            ps.setDate(4, Date.valueOf(sem.getEndDate()));
            ps.setDate(5, Date.valueOf(sem.getRegistrationStart()));
            ps.setDate(6, Date.valueOf(sem.getRegistrationEnd()));

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        sem.setSemesterId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error adding semester", e);
        }
        return false;
    }

    @Override
    public Semester getSemesterById(int id) {
        String sql = "SELECT semester_id, semester_name, academic_year, start_date, end_date, registration_start, registration_end " +
                "FROM semesters WHERE semester_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToSemester(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting semester by ID", e);
        }
        return null;
    }

    @Override
    public List<Semester> getAllSemesters() {
        List<Semester> list = new ArrayList<>();
        String sql = "SELECT semester_id, semester_name, academic_year, start_date, end_date, registration_start, registration_end " +
                "FROM semesters ORDER BY start_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToSemester(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching semesters", e);
        }
        return list;
    }

    @Override
    public Semester getCurrentSemester() {
        String sql = "SELECT semester_id, semester_name, academic_year, start_date, end_date, registration_start, registration_end " +
                "FROM semesters WHERE CURRENT_DATE BETWEEN registration_start AND registration_end " +
                "ORDER BY registration_end LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return mapRowToSemester(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching current semester", e);
        }

        List<Semester> all = getAllSemesters();
        return all.isEmpty() ? null : all.get(0);
    }

    @Override
    public boolean updateSemester(Semester sem) {
        String sql = "UPDATE semesters SET semester_name = ?, academic_year = ?, start_date = ?, end_date = ?, " +
                "registration_start = ?, registration_end = ? WHERE semester_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sem.getSemesterName());
            ps.setString(2, sem.getAcademicYear());
            ps.setDate(3, Date.valueOf(sem.getStartDate()));
            ps.setDate(4, Date.valueOf(sem.getEndDate()));
            ps.setDate(5, Date.valueOf(sem.getRegistrationStart()));
            ps.setDate(6, Date.valueOf(sem.getRegistrationEnd()));
            ps.setInt(7, sem.getSemesterId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating semester", e);
        }
    }

    @Override
    public boolean deleteSemester(int id) {
        String sql = "DELETE FROM semesters WHERE semester_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting semester", e);
        }
    }

    private Semester mapRowToSemester(ResultSet rs) throws SQLException {
        Semester s = new Semester();
        s.setSemesterId(rs.getInt("semester_id"));
        s.setSemesterName(rs.getString("semester_name"));
        s.setAcademicYear(rs.getString("academic_year"));
        s.setStartDate(rs.getDate("start_date").toLocalDate());
        s.setEndDate(rs.getDate("end_date").toLocalDate());
        s.setRegistrationStart(rs.getDate("registration_start").toLocalDate());
        s.setRegistrationEnd(rs.getDate("registration_end").toLocalDate());
        return s;
    }
}
