package com.university;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SemesterDAO {

    public boolean addSemester(Semester sem) {
        String sql = "INSERT INTO semesters (semester_name, academic_year, start_date, end_date, registration_start, registration_end) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
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
            System.out.println("Error adding semester: " + e.getMessage());
        }
        return false;
    }

    public Semester getSemesterById(int id) {
        String sql = "SELECT semester_id, semester_name, academic_year, start_date, end_date, registration_start, registration_end " +
                "FROM semesters WHERE semester_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToSemester(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting semester by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Semester> getAllSemesters() {
        List<Semester> list = new ArrayList<>();
        String sql = "SELECT semester_id, semester_name, academic_year, start_date, end_date, registration_start, registration_end " +
                "FROM semesters ORDER BY start_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToSemester(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching semesters: " + e.getMessage());
        }
        return list;
    }

    public Semester getCurrentSemester() {
        String sql = "SELECT semester_id, semester_name, academic_year, start_date, end_date, registration_start, registration_end " +
                "FROM semesters WHERE CURRENT_DATE BETWEEN registration_start AND registration_end " +
                "ORDER BY registration_end LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return mapRowToSemester(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching current semester: " + e.getMessage());
        }

        List<Semester> all = getAllSemesters();
        return all.isEmpty() ? null : all.get(0);
    }

    public boolean updateSemester(Semester sem) {
        String sql = "UPDATE semesters SET semester_name = ?, academic_year = ?, start_date = ?, end_date = ?, " +
                "registration_start = ?, registration_end = ? WHERE semester_id = ?";
        try (Connection conn = DBConnection.getConnection();
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
            System.out.println("Error updating semester: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSemester(int id) {
        String sql = "DELETE FROM semesters WHERE semester_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting semester: " + e.getMessage());
            return false;
        }
    }

    public void viewSemesters() {
        List<Semester> list = getAllSemesters();
        System.out.println("\n--- ACADEMIC SEMESTERS ---");
        System.out.printf("%-6s %-16s %-12s %-12s %-12s %-10s\n", "ID", "Semester", "Year", "Start Date", "End Date", "Status");
        System.out.println("-----------------------------------------------------------------------------");
        for (Semester s : list) {
            System.out.printf("%-6d %-16s %-12s %-12s %-12s %-10s\n",
                    s.getSemesterId(), s.getSemesterName(), s.getAcademicYear(),
                    s.getStartDate(), s.getEndDate(), s.isRegistrationOpen() ? "OPEN" : "CLOSED");
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
