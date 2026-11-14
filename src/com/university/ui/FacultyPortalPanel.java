package com.university.ui;

import com.university.CourseOffering;
import com.university.CourseOfferingDAO;
import com.university.Enrollment;
import com.university.EnrollmentDAO;
import com.university.Faculty;
import com.university.FacultyDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FacultyPortalPanel extends JPanel {

    private final FacultyDAO facultyDAO;
    private final CourseOfferingDAO offeringDAO;
    private final EnrollmentDAO enrollmentDAO;

    private JComboBox<Faculty> cbFaculty;
    private JTable tblOfferings;
    private DefaultTableModel modelOfferings;

    private JTable tblRoster;
    private DefaultTableModel modelRoster;

    private JComboBox<String> cbGrade;
    private JLabel lblSelectedStudent;
    private int selectedEnrollmentId = -1;

    public FacultyPortalPanel() {
        this.facultyDAO = new FacultyDAO();
        this.offeringDAO = new CourseOfferingDAO();
        this.enrollmentDAO = new EnrollmentDAO();

        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
        loadFacultyList();
    }

    private void initUI() {
        JPanel selectorBar = UITheme.createCardPanel();
        selectorBar.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 6));

        selectorBar.add(new JLabel("Acting as Faculty:"));
        cbFaculty = new JComboBox<>();
        cbFaculty.setPreferredSize(new Dimension(320, 32));
        cbFaculty.addActionListener(e -> loadAssignedOfferings());
        selectorBar.add(cbFaculty);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadAssignedOfferings());
        selectorBar.add(btnRefresh);

        add(selectorBar, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(280);
        splitPane.setResizeWeight(0.5);

        JPanel topContainer = new JPanel(new BorderLayout(8, 8));
        topContainer.setBackground(UITheme.BG_LIGHT);
        JLabel lblAssigned = new JLabel("My Assigned Course Offerings (Select a course below to view roster):");
        lblAssigned.setFont(UITheme.FONT_HEADER);
        topContainer.add(lblAssigned, BorderLayout.NORTH);

        String[] offCols = {"Offering ID", "Course Code", "Course Title", "Credits", "Semester", "Section", "Capacity", "Enrolled Count"};
        modelOfferings = new DefaultTableModel(offCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblOfferings = new JTable(modelOfferings);
        tblOfferings.setRowHeight(28);
        tblOfferings.setFont(UITheme.FONT_BODY);
        tblOfferings.getTableHeader().setFont(UITheme.FONT_BOLD);
        tblOfferings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblOfferings.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblOfferings.getSelectedRow();
                if (row >= 0) {
                    int offeringId = (int) modelOfferings.getValueAt(row, 0);
                    loadRoster(offeringId);
                }
            }
        });

        topContainer.add(new JScrollPane(tblOfferings), BorderLayout.CENTER);
        splitPane.setTopComponent(topContainer);

        JPanel bottomContainer = new JPanel(new BorderLayout(8, 8));
        bottomContainer.setBackground(UITheme.BG_LIGHT);
        JLabel lblRoster = new JLabel("Class Roster & Grade Submission:");
        lblRoster.setFont(UITheme.FONT_HEADER);
        bottomContainer.add(lblRoster, BorderLayout.NORTH);

        String[] rosterCols = {"Enrollment ID", "Roll Number", "Student Name", "Enrollment Date", "Current Grade", "Status"};
        modelRoster = new DefaultTableModel(rosterCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblRoster = new JTable(modelRoster);
        tblRoster.setRowHeight(28);
        tblRoster.setFont(UITheme.FONT_BODY);
        tblRoster.getTableHeader().setFont(UITheme.FONT_BOLD);
        tblRoster.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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

        bottomContainer.add(new JScrollPane(tblRoster), BorderLayout.CENTER);

        JPanel gradingBar = UITheme.createCardPanel();
        gradingBar.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 6));

        lblSelectedStudent = new JLabel("Selected: (None selected)");
        lblSelectedStudent.setFont(UITheme.FONT_BOLD);
        gradingBar.add(lblSelectedStudent);

        gradingBar.add(new JLabel("Assign Grade:"));
        String[] grades = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F"};
        cbGrade = new JComboBox<>(grades);
        gradingBar.add(cbGrade);

        JButton btnSubmit = UITheme.createSuccessButton("Submit Grade");
        btnSubmit.addActionListener(e -> handleSubmitGrade());
        gradingBar.add(btnSubmit);

        bottomContainer.add(gradingBar, BorderLayout.SOUTH);
        splitPane.setBottomComponent(bottomContainer);

        add(splitPane, BorderLayout.CENTER);
    }

    public void loadFacultyList() {
        cbFaculty.removeAllItems();
        List<Faculty> list = facultyDAO.getAllFaculty();
        for (Faculty f : list) {
            cbFaculty.addItem(f);
        }
        loadAssignedOfferings();
    }

    private void loadAssignedOfferings() {
        modelOfferings.setRowCount(0);
        modelRoster.setRowCount(0);
        selectedEnrollmentId = -1;
        lblSelectedStudent.setText("Selected: (None selected)");

        Faculty f = (Faculty) cbFaculty.getSelectedItem();
        if (f == null) return;

        List<CourseOffering> list = offeringDAO.getOfferingsByFaculty(f.getUserId());
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

    private void loadRoster(int offeringId) {
        modelRoster.setRowCount(0);
        selectedEnrollmentId = -1;
        lblSelectedStudent.setText("Selected: (None selected)");

        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByOffering(offeringId);
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
            JOptionPane.showMessageDialog(this, "Please select a student from the roster first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String grade = (String) cbGrade.getSelectedItem();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Assign grade " + grade + " to the selected student?",
                "Confirm Grade Submission",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = enrollmentDAO.submitGrade(selectedEnrollmentId, grade);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Grade submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                int offRow = tblOfferings.getSelectedRow();
                if (offRow >= 0) {
                    int offId = (int) modelOfferings.getValueAt(offRow, 0);
                    loadRoster(offId);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit grade.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
