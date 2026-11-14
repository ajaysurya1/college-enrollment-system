package com.university.ui.student;

import com.university.exception.EnrollmentException;
import com.university.model.CourseOffering;
import com.university.model.Enrollment;
import com.university.model.Semester;
import com.university.model.Student;
import com.university.service.CourseOfferingService;
import com.university.service.CourseService;
import com.university.service.EnrollmentService;
import com.university.service.SemesterService;
import com.university.ui.auth.LoginFrame;
import com.university.ui.components.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame {

    private final Student currentStudent;
    private final CourseOfferingService offeringService;
    private final EnrollmentService enrollmentService;
    private final SemesterService semesterService;
    private final CourseService courseService;

    private JComboBox<Semester> cbSemesters;
    private JTextField txtSearch;
    private JTable tblOfferings;
    private DefaultTableModel modelOfferings;

    private JTable tblEnrolled;
    private DefaultTableModel modelEnrolled;

    private JTable tblGrades;
    private DefaultTableModel modelGrades;

    public StudentDashboard(Student student) {
        this.currentStudent = student;
        this.offeringService = new CourseOfferingService();
        this.enrollmentService = new EnrollmentService();
        this.semesterService = new SemesterService();
        this.courseService = new CourseService();

        initUI();
        loadSemesters();
        loadEnrolledCourses();
        loadGrades();
    }

    private void initUI() {
        setTitle("Student Portal - " + currentStudent.getFullName());
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.NAVY);
        topBar.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel(currentStudent.getDashboardMessage());
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        topBar.add(lblTitle, BorderLayout.WEST);

        JButton btnLogout = UITheme.createDangerButton("Logout");
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        topBar.add(btnLogout, BorderLayout.EAST);

        mainPanel.add(topBar, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BOLD);

        tabbedPane.addTab("Available Courses & Enrollment", createEnrollmentTab());
        tabbedPane.addTab("My Enrolled Courses", createMyCoursesTab());
        tabbedPane.addTab("Grades & History", createGradesTab());
        tabbedPane.addTab("My Profile", createProfileTab());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JPanel createEnrollmentTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel filterBar = UITheme.createCardPanel();
        filterBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        filterBar.add(new JLabel("Select Semester:"));
        cbSemesters = new JComboBox<>();
        cbSemesters.setPreferredSize(new Dimension(200, 30));
        cbSemesters.addActionListener(e -> loadAvailableCourses());
        filterBar.add(cbSemesters);

        filterBar.add(Box.createHorizontalStrut(15));
        filterBar.add(new JLabel("Search Course:"));
        txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(150, 30));
        filterBar.add(txtSearch);

        JButton btnSearch = UITheme.createPrimaryButton("Search");
        btnSearch.addActionListener(e -> loadAvailableCourses());
        filterBar.add(btnSearch);

        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            loadAvailableCourses();
        });
        filterBar.add(btnReset);

        panel.add(filterBar, BorderLayout.NORTH);

        String[] cols = {"Offering ID", "Course Code", "Course Title", "Credits", "Section", "Faculty", "Available Seats", "Prerequisites"};
        modelOfferings = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblOfferings = new JTable(modelOfferings);
        tblOfferings.setRowHeight(26);
        tblOfferings.setFont(UITheme.FONT_BODY);
        tblOfferings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(tblOfferings), BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setBackground(UITheme.BG_LIGHT);

        JButton btnEnroll = UITheme.createSuccessButton("Enroll in Selected Course");
        btnEnroll.addActionListener(e -> handleEnrollAction());
        bottomBar.add(btnEnroll);

        panel.add(bottomBar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createMyCoursesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] cols = {"Enrollment ID", "Course Code", "Course Title", "Credits", "Semester", "Section", "Faculty", "Date", "Status"};
        modelEnrolled = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblEnrolled = new JTable(modelEnrolled);
        tblEnrolled.setRowHeight(26);
        tblEnrolled.setFont(UITheme.FONT_BODY);
        tblEnrolled.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(tblEnrolled), BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomBar.setBackground(UITheme.BG_LIGHT);

        JButton btnRefresh = UITheme.createPrimaryButton("Refresh");
        btnRefresh.addActionListener(e -> loadEnrolledCourses());
        bottomBar.add(btnRefresh);

        JButton btnDrop = UITheme.createDangerButton("Drop Selected Course");
        btnDrop.addActionListener(e -> handleDropAction());
        bottomBar.add(btnDrop);

        panel.add(bottomBar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createGradesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] cols = {"Course Code", "Course Title", "Credits", "Semester", "Status", "Grade"};
        modelGrades = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblGrades = new JTable(modelGrades);
        tblGrades.setRowHeight(26);
        tblGrades.setFont(UITheme.FONT_BODY);
        panel.add(new JScrollPane(tblGrades), BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setBackground(UITheme.BG_LIGHT);

        JButton btnRefresh = UITheme.createPrimaryButton("Refresh Grades");
        btnRefresh.addActionListener(e -> loadGrades());
        bottomBar.add(btnRefresh);

        panel.add(bottomBar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createProfileTab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(new EmptyBorder(30, 20, 20, 20));

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridLayout(6, 2, 15, 15));
        card.setPreferredSize(new Dimension(480, 260));

        card.add(new JLabel("Roll Number:"));
        card.add(new JLabel(currentStudent.getRollNumber()));

        card.add(new JLabel("Full Name:"));
        card.add(new JLabel(currentStudent.getFullName()));

        card.add(new JLabel("Email Address:"));
        card.add(new JLabel(currentStudent.getEmail()));

        card.add(new JLabel("Programme:"));
        card.add(new JLabel(currentStudent.getProgramme()));

        card.add(new JLabel("Year of Study:"));
        card.add(new JLabel(String.valueOf(currentStudent.getYearOfStudy())));

        card.add(new JLabel("Account Status:"));
        card.add(new JLabel(currentStudent.isActive() ? "Active" : "Inactive"));

        panel.add(card);
        return panel;
    }

    private void loadSemesters() {
        cbSemesters.removeAllItems();
        List<Semester> semesters = semesterService.getAllSemesters();
        for (Semester s : semesters) {
            cbSemesters.addItem(s);
        }
        Semester current = semesterService.getCurrentSemester();
        if (current != null) {
            for (int i = 0; i < cbSemesters.getItemCount(); i++) {
                if (cbSemesters.getItemAt(i).getSemesterId() == current.getSemesterId()) {
                    cbSemesters.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void loadAvailableCourses() {
        modelOfferings.setRowCount(0);
        Semester selected = (Semester) cbSemesters.getSelectedItem();
        if (selected == null) {
            return;
        }

        String search = txtSearch.getText().trim().toLowerCase();
        List<CourseOffering> offerings = offeringService.getOfferingsBySemester(selected.getSemesterId());

        for (CourseOffering o : offerings) {
            boolean matches = search.isEmpty() ||
                    (o.getCourseCode() != null && o.getCourseCode().toLowerCase().contains(search)) ||
                    (o.getCourseTitle() != null && o.getCourseTitle().toLowerCase().contains(search));

            if (matches) {
                List<com.university.model.Course> prereqs = courseService.getPrerequisites(o.getCourseId());
                StringBuilder prereqStr = new StringBuilder();
                for (com.university.model.Course p : prereqs) {
                    if (prereqStr.length() > 0) prereqStr.append(", ");
                    prereqStr.append(p.getCourseCode());
                }
                if (prereqStr.length() == 0) {
                    prereqStr.append("None");
                }

                String seatInfo = o.getAvailableSeats() + " left (Total: " + o.getCapacity() + ")";

                modelOfferings.addRow(new Object[]{
                        o.getOfferingId(),
                        o.getCourseCode(),
                        o.getCourseTitle(),
                        o.getCredits(),
                        o.getSection(),
                        o.getFacultyName() != null ? o.getFacultyName() : "TBD",
                        seatInfo,
                        prereqStr.toString()
                });
            }
        }
    }

    private void handleEnrollAction() {
        int selectedRow = tblOfferings.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course offering to enroll.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int offeringId = (int) modelOfferings.getValueAt(selectedRow, 0);
        String courseCode = (String) modelOfferings.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Do you want to enroll in " + courseCode + "?",
                "Confirm Enrollment",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = enrollmentService.enroll(currentStudent.getUserId(), offeringId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Successfully enrolled in " + courseCode + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAvailableCourses();
                    loadEnrolledCourses();
                } else {
                    JOptionPane.showMessageDialog(this, "Enrollment could not be completed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (EnrollmentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Enrollment Failed", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadEnrolledCourses() {
        modelEnrolled.setRowCount(0);
        List<Enrollment> enrollments = enrollmentService.getStudentEnrollments(currentStudent.getUserId());
        for (Enrollment e : enrollments) {
            if ("ENROLLED".equalsIgnoreCase(e.getStatus())) {
                modelEnrolled.addRow(new Object[]{
                        e.getEnrollmentId(),
                        e.getCourseCode(),
                        e.getCourseTitle(),
                        e.getCredits(),
                        e.getSemesterName(),
                        e.getSection(),
                        e.getFacultyName() != null ? e.getFacultyName() : "TBD",
                        e.getEnrollmentDate(),
                        e.getStatus()
                });
            }
        }
    }

    private void handleDropAction() {
        int selectedRow = tblEnrolled.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an enrolled course to drop.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int enrollmentId = (int) modelEnrolled.getValueAt(selectedRow, 0);
        String courseCode = (String) modelEnrolled.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to drop " + courseCode + "?",
                "Confirm Drop",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean dropped = enrollmentService.dropCourse(enrollmentId, currentStudent.getUserId());
                if (dropped) {
                    JOptionPane.showMessageDialog(this, "Course dropped successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAvailableCourses();
                    loadEnrolledCourses();
                    loadGrades();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to drop course.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Drop Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadGrades() {
        modelGrades.setRowCount(0);
        List<Enrollment> enrollments = enrollmentService.getStudentEnrollments(currentStudent.getUserId());
        for (Enrollment e : enrollments) {
            modelGrades.addRow(new Object[]{
                    e.getCourseCode(),
                    e.getCourseTitle(),
                    e.getCredits(),
                    e.getSemesterName(),
                    e.getStatus(),
                    e.getGrade() != null ? e.getGrade() : "Pending"
            });
        }
    }
}
