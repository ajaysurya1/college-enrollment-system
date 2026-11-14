package com.university.dao;

import com.university.model.Student;
import java.util.List;

public interface StudentDAO {
    boolean addStudent(Student student, String passwordHash);
    Student getStudentById(int userId);
    Student getStudentByRoll(String rollNumber);
    List<Student> getAllStudents();
    boolean updateStudent(Student student);
    boolean deleteStudent(int userId);
    int countStudents();
}
