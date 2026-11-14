package com.university.ui;

import javax.swing.*;
import java.awt.*;

public class UniversityApp extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCards = new JPanel(cardLayout);

    private SidebarPanel sidebarPanel;
    private HeaderPanel headerPanel;

    private DashboardPanel dashboardPanel;
    private StudentsPanel studentsPanel;
    private FacultyPanel facultyPanel;
    private CoursesPanel coursesPanel;
    private CourseOfferingsPanel offeringsPanel;
    private EnrollmentsPanel enrollmentsPanel;
    private StudentPortalPanel studentPortalPanel;
    private FacultyPortalPanel facultyPortalPanel;
    private AboutPanel aboutPanel;

    public UniversityApp() {
        super("University Academic & Course Enrollment Management System");
        initApp();
    }

    private void initApp() {
        UITheme.setupFlatLaf();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1020, 640));
        setLocationRelativeTo(null);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(UITheme.BG_LIGHT);

        headerPanel = new HeaderPanel(this::refreshCurrentPage);
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        sidebarPanel = new SidebarPanel(this::navigateToPage);
        rootPanel.add(sidebarPanel, BorderLayout.WEST);

        dashboardPanel = new DashboardPanel();
        studentsPanel = new StudentsPanel();
        facultyPanel = new FacultyPanel();
        coursesPanel = new CoursesPanel();
        offeringsPanel = new CourseOfferingsPanel();
        enrollmentsPanel = new EnrollmentsPanel();
        studentPortalPanel = new StudentPortalPanel();
        facultyPortalPanel = new FacultyPortalPanel();
        aboutPanel = new AboutPanel();

        contentCards.setBackground(UITheme.BG_LIGHT);
        contentCards.add(dashboardPanel, "DASHBOARD");
        contentCards.add(studentsPanel, "STUDENTS");
        contentCards.add(facultyPanel, "FACULTY");
        contentCards.add(coursesPanel, "COURSES");
        contentCards.add(offeringsPanel, "OFFERINGS");
        contentCards.add(enrollmentsPanel, "ENROLLMENTS");
        contentCards.add(studentPortalPanel, "STUDENT_PORTAL");
        contentCards.add(facultyPortalPanel, "FACULTY_PORTAL");
        contentCards.add(aboutPanel, "ABOUT");

        rootPanel.add(contentCards, BorderLayout.CENTER);
        setContentPane(rootPanel);
    }

    public void navigateToPage(String pageId) {
        cardLayout.show(contentCards, pageId);
        switch (pageId) {
            case "DASHBOARD":
                headerPanel.setPageTitle("Dashboard Overview", "Real-time University Academic Statistics & Activity");
                dashboardPanel.refreshData();
                break;
            case "STUDENTS":
                headerPanel.setPageTitle("Student Directory", "Manage registered students, roll numbers and degree programmes");
                studentsPanel.loadStudents();
                break;
            case "FACULTY":
                headerPanel.setPageTitle("Faculty Directory", "Manage academic professors, instructors and designations");
                facultyPanel.loadFaculty();
                break;
            case "COURSES":
                headerPanel.setPageTitle("Course Catalog", "Manage academic courses, credits, departments and prerequisites");
                coursesPanel.loadCourses();
                break;
            case "OFFERINGS":
                headerPanel.setPageTitle("Course Offerings", "Manage semester schedules, class sections and seat capacities");
                offeringsPanel.loadOfferings();
                break;
            case "ENROLLMENTS":
                headerPanel.setPageTitle("Enrollment Records", "Track student course registrations and assign academic grades");
                enrollmentsPanel.loadEnrollments();
                break;
            case "STUDENT_PORTAL":
                headerPanel.setPageTitle("Student Self-Service Portal", "Course registration with ACID transactional capacity checks and drop courses");
                studentPortalPanel.loadStudentsAndSemesters();
                break;
            case "FACULTY_PORTAL":
                headerPanel.setPageTitle("Faculty Grading Portal", "View assigned course offerings, student rosters and submit final grades");
                facultyPortalPanel.loadFacultyList();
                break;
            case "ABOUT":
                headerPanel.setPageTitle("About System Architecture", "Object-Oriented Programming (OOP) specifications and technology credits");
                break;
        }
    }

    public void refreshCurrentPage() {
        dashboardPanel.refreshData();
        studentsPanel.loadStudents();
        facultyPanel.loadFaculty();
        coursesPanel.loadCourses();
        offeringsPanel.loadOfferings();
        enrollmentsPanel.loadEnrollments();
        studentPortalPanel.loadStudentsAndSemesters();
        facultyPortalPanel.loadFacultyList();
    }

    public static void main(String[] args) {
        UITheme.setupFlatLaf();
        SwingUtilities.invokeLater(() -> {
            UniversityApp app = new UniversityApp();
            app.setVisible(true);
        });
    }
}
