package com.university.service;

import com.university.dao.StudentDAO;
import com.university.dao.impl.StudentDAOImpl;
import com.university.model.Student;
import com.university.util.PasswordUtil;

import java.util.List;

public class StudentService {

    private final StudentDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAOImpl();
    }

    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public boolean registerStudent(String username, String rawPassword, String fullName, String email,
                                   Integer departmentId, String rollNumber, String programme, int yearOfStudy) {

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (rawPassword == null || rawPassword.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long");
        }
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Roll number is required");
        }

        Student student = new Student(0, username.trim(), fullName, email, departmentId, true,
                rollNumber.trim(), programme != null ? programme.trim() : "", yearOfStudy);

        String hash = PasswordUtil.hashPassword(rawPassword);
        return studentDAO.addStudent(student, hash);
    }

    public Student getStudentById(int userId) {
        return studentDAO.getStudentById(userId);
    }

    public Student getStudentByRoll(String rollNumber) {
        return studentDAO.getStudentByRoll(rollNumber);
    }

    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    public boolean updateStudent(Student student) {
        if (student == null || student.getUserId() <= 0) {
            throw new IllegalArgumentException("Invalid student record for update");
        }
        return studentDAO.updateStudent(student);
    }

    public boolean deactivateStudent(int userId) {
        return studentDAO.deleteStudent(userId);
    }

    public int getStudentCount() {
        return studentDAO.countStudents();
    }
}
