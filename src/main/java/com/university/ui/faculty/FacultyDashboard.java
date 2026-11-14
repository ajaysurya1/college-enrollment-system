package com.university.ui.faculty;

import com.university.model.CourseOffering;
import com.university.model.Enrollment;
import com.university.model.Faculty;
import com.university.service.CourseOfferingService;
import com.university.service.EnrollmentService;
import com.university.ui.auth.LoginFrame;
import com.university.ui.components.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FacultyDashboard extends JFrame {

    private final Faculty currentFaculty;
    private final CourseOfferingService offeringService;
    private final EnrollmentService enrollmentService;

    private JTable tblOfferings;
    private DefaultTableModel modelOfferings;

    private JTable tblRoster;
    private DefaultTableModel modelRoster;

    private JComboBox<String> cbGrade;
    private JLabel lblSelectedStudent;
    private int selectedEnrollmentId = -1;

    public FacultyDashboard(Faculty faculty) {
        this.currentFaculty = faculty;
        this.offeringService = new CourseOfferingService();
        this.enrollmentService = new EnrollmentService();

        initUI();
        loadAssignedOfferings();
    }

    private void initUI() {
        setTitle("Faculty Portal - " + currentFaculty.getFullName());
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.NAVY);
        topBar.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel(currentFaculty.getDashboardMessage());
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

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);

        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        topContainer.setBackground(UITheme.BG_LIGHT);
        topContainer.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel lblOfferingsHeader = new JLabel("My Assigned Course Offerings (Select a course to view roster):");
        lblOfferingsHeader.setFont(UITheme.FONT_HEADER);
        topContainer.add(lblOfferingsHeader, BorderLayout.NORTH);

        String[] offeringCols = {"Offering ID", "Course Code", "Course Title", "Credits", "Semester", "Section", "Capacity", "Enrolled Count"};
        modelOfferings = new DefaultTableModel(offeringCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblOfferings = new JTable(modelOfferings);
        tblOfferings.setRowHeight(26);
        tblOfferings.setFont(UITheme.FONT_BODY);
        tblOfferings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        topContainer.add(new JScrollPane(tblOfferings), BorderLayout.CENTER);

        tblOfferings.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblOfferings.getSelectedRow();
                if (row >= 0) {
                    int offeringId = (int) modelOfferings.getValueAt(row, 0);
                    loadRosterForOffering(offeringId);
                }
            }
        });

        splitPane.setTopComponent(topContainer);

        JPanel bottomContainer = new JPanel(new BorderLayout(10, 10));
        bottomContainer.setBackground(UITheme.BG_LIGHT);
        bottomContainer.setBorder(new EmptyBorder(10, 15, 15, 15));

        JLabel lblRosterHeader = new JLabel("Class Roster & Grade Submission:");
        lblRosterHeader.setFont(UITheme.FONT_HEADER);
        bottomContainer.add(lblRosterHeader, BorderLayout.NORTH);

        String[] rosterCols = {"Enrollment ID", "Roll Number", "Student Name", "Enrollment Date", "Current Grade", "Status"};
        modelRoster = new DefaultTableModel(rosterCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblRoster = new JTable(modelRoster);
        tblRoster.setRowHeight(26);
        tblRoster.setFont(UITheme.FONT_BODY);
        tblRoster.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bottomContainer.add(new JScrollPane(tblRoster), BorderLayout.CENTER);

        tblRoster.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblRoster.getSelectedRow();
                if (row >= 0) {
                    selectedEnrollmentId = (int) modelRoster.getValueAt(row, 0);
                    String roll = (String) modelRoster.getValueAt(row, 1);
                    String name = (String) modelRoster.getValueAt(row, 2);
                    lblSelectedStudent.setText("Selected: " + name + " (" + roll + ")");
                }
            }
        });

        JPanel gradingPanel = UITheme.createCardPanel();
        gradingPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 8));

        lblSelectedStudent = new JLabel("Selected: (None selected)");
        lblSelectedStudent.setFont(UITheme.FONT_BOLD);
        gradingPanel.add(lblSelectedStudent);

        gradingPanel.add(new JLabel("Assign Grade:"));
        String[] grades = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F"};
        cbGrade = new JComboBox<>(grades);
        cbGrade.setPreferredSize(new Dimension(80, 30));
        gradingPanel.add(cbGrade);

        JButton btnSubmitGrade = UITheme.createSuccessButton("Submit Grade");
        btnSubmitGrade.addActionListener(e -> handleSubmitGrade());
        gradingPanel.add(btnSubmitGrade);

        bottomContainer.add(gradingPanel, BorderLayout.SOUTH);
        splitPane.setBottomComponent(bottomContainer);

        mainPanel.add(splitPane, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private void loadAssignedOfferings() {
        modelOfferings.setRowCount(0);
        List<CourseOffering> list = offeringService.getOfferingsByFaculty(currentFaculty.getUserId());
        for (CourseOffering o : list) {
            modelOfferings.addRow(new Object[]{
                    o.getOfferingId(),
                    o.getCourseCode(),
                    o.getCourseTitle(),
                    o.getCredits(),
                    o.getSemesterName(),
                    o.getSection(),
                    o.getCapacity(),
                    o.getEnrolledCount()
            });
        }

        if (modelOfferings.getRowCount() > 0) {
            tblOfferings.setRowSelectionInterval(0, 0);
        }
    }

    private void loadRosterForOffering(int offeringId) {
        modelRoster.setRowCount(0);
        selectedEnrollmentId = -1;
        lblSelectedStudent.setText("Selected: (None selected)");

        List<Enrollment> enrollments = enrollmentService.getOfferingEnrollments(offeringId);
        for (Enrollment e : enrollments) {
            modelRoster.addRow(new Object[]{
                    e.getEnrollmentId(),
                    e.getStudentRoll(),
                    e.getStudentName(),
                    e.getEnrollmentDate(),
                    e.getGrade() != null ? e.getGrade() : "Not Graded",
                    e.getStatus()
            });
        }
    }

    private void handleSubmitGrade() {
        if (selectedEnrollmentId <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a student from the roster first.", "No Student Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String grade = (String) cbGrade.getSelectedItem();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Submit grade " + grade + " for the selected student?",
                "Confirm Grade Submission",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = enrollmentService.submitGrade(selectedEnrollmentId, currentFaculty.getUserId(), grade);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Grade submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    int offeringRow = tblOfferings.getSelectedRow();
                    if (offeringRow >= 0) {
                        int offeringId = (int) modelOfferings.getValueAt(offeringRow, 0);
                        loadRosterForOffering(offeringId);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update grade.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
