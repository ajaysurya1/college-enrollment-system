package com.university;

import com.university.ui.UniversityApp;

import java.awt.GraphicsEnvironment;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        if ((args.length > 0 && (args[0].equalsIgnoreCase("--cli") || args[0].equalsIgnoreCase("-c")))
                || GraphicsEnvironment.isHeadless()) {
            runConsoleApp(args);
        } else {
            UniversityApp.main(args);
        }
    }

    public static void runConsoleApp(String[] args) {
        Scanner sc = new Scanner(System.in);
        DashboardDAO dashboardDAO = new DashboardDAO();
        StudentDAO studentDAO = new StudentDAO();
        FacultyDAO facultyDAO = new FacultyDAO();
        CourseDAO courseDAO = new CourseDAO();
        CourseOfferingDAO offeringDAO = new CourseOfferingDAO();
        EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

        int choice = 0;

        do {
            System.out.println("\n=======================================================");
            System.out.println("  UNIVERSITY ACADEMIC & ENROLLMENT SYSTEM (CLI MODE)   ");
            System.out.println("=======================================================");
            System.out.println("1. View System Dashboard Overview");
            System.out.println("2. View All Students");
            System.out.println("3. Add New Student");
            System.out.println("4. View All Faculty");
            System.out.println("5. View Course Catalog");
            System.out.println("6. View Course Offerings");
            System.out.println("7. View All Enrollments");
            System.out.println("8. Enroll Student in Course Offering");
            System.out.println("9. Exit");
            System.out.print("Select an option (1-9): ");

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                choice = 0;
            }

            switch (choice) {
                case 1:
                    Map<String, Integer> stats = dashboardDAO.getSystemStats();
                    System.out.println("\n--- SYSTEM OVERVIEW METRICS ---");
                    System.out.println("Total Students:       " + stats.get("students"));
                    System.out.println("Total Faculty:        " + stats.get("faculty"));
                    System.out.println("Total Courses:        " + stats.get("courses"));
                    System.out.println("Active Enrollments:   " + stats.get("enrollments"));
                    System.out.println("Course Offerings:     " + stats.get("offerings"));
                    System.out.println("Departments:          " + stats.get("departments"));
                    break;

                case 2:
                    studentDAO.viewStudents();
                    break;

                case 3:
                    System.out.println("\n--- ADD NEW STUDENT ---");
                    System.out.print("Enter Username: ");
                    String username = sc.nextLine().trim();
                    System.out.print("Enter Full Name: ");
                    String name = sc.nextLine().trim();
                    System.out.print("Enter Email: ");
                    String email = sc.nextLine().trim();
                    System.out.print("Enter Roll Number: ");
                    String roll = sc.nextLine().trim();
                    System.out.print("Enter Programme (e.g. B.Tech Computer Science): ");
                    String prog = sc.nextLine().trim();
                    System.out.print("Enter Year of Study (1-4): ");
                    int year = 1;
                    try {
                        year = Integer.parseInt(sc.nextLine().trim());
                    } catch (Exception ex) {
                    }
                    Student newStudent = new Student(0, username, name, email, 1, true, roll, prog, year);
                    boolean added = studentDAO.addStudent(newStudent, "student123");
                    if (added) {
                        System.out.println("Student registered successfully! (Default Password: student123)");
                    } else {
                        System.out.println("Failed to register student.");
                    }
                    break;

                case 4:
                    facultyDAO.viewFaculty();
                    break;

                case 5:
                    courseDAO.viewCourses();
                    break;

                case 6:
                    offeringDAO.viewOfferings();
                    break;

                case 7:
                    enrollmentDAO.viewEnrollments();
                    break;

                case 8:
                    System.out.println("\n--- ENROLL STUDENT IN COURSE ---");
                    System.out.print("Enter Student Roll Number: ");
                    String sRoll = sc.nextLine().trim();
                    Student foundStudent = studentDAO.getStudentByRoll(sRoll);
                    if (foundStudent == null) {
                        System.out.println("Student not found with roll: " + sRoll);
                        break;
                    }
                    System.out.print("Enter Offering ID (e.g. 2, 3, 4, 5, 6): ");
                    int offId = 0;
                    try {
                        offId = Integer.parseInt(sc.nextLine().trim());
                    } catch (Exception ex) {
                        System.out.println("Invalid offering ID.");
                        break;
                    }
                    String enrollResult = enrollmentDAO.enrollStudent(foundStudent.getUserId(), offId);
                    if ("SUCCESS".equalsIgnoreCase(enrollResult)) {
                        System.out.println("Enrollment completed successfully for " + foundStudent.getFullName() + "!");
                    } else {
                        System.out.println("Enrollment rejected: " + enrollResult);
                    }
                    break;

                case 9:
                    System.out.println("\nExiting University Academic System. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 9.");
            }
        } while (choice != 9);
    }
}
