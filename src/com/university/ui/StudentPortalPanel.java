package com.university.ui;

import com.university.Course;
import com.university.CourseDAO;
import com.university.CourseOffering;
import com.university.CourseOfferingDAO;
import com.university.Enrollment;
import com.university.EnrollmentDAO;
import com.university.Semester;
import com.university.SemesterDAO;
import com.university.Student;
import com.university.StudentDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentPortalPanel extends JPanel {

    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    private final CourseOfferingDAO offeringDAO;
    private final EnrollmentDAO enrollmentDAO;
    private final SemesterDAO semesterDAO;

    private JComboBox<Student> cbStudents;
    private JComboBox<Semester> cbSemesters;
    private JTable tblAvailableOfferings;
    private DefaultTableModel modelAvailableOfferings;

    private JTable tblMyEnrollments;
    private DefaultTableModel modelMyEnrollments;

    public StudentPortalPanel() {
        this.studentDAO = new StudentDAO();
        this.courseDAO = new CourseDAO();
        this.offeringDAO = new CourseOfferingDAO();
        this.enrollmentDAO = new EnrollmentDAO();
        this.semesterDAO = new SemesterDAO();

        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
        loadStudentsAndSemesters();
    }

    private void initUI() {
        JPanel selectorBar = UITheme.createCardPanel();
        selectorBar.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 6));

        selectorBar.add(new JLabel("Acting as Student:"));
        cbStudents = new JComboBox<>();
        cbStudents.setPreferredSize(new Dimension(280, 32));
        cbStudents.addActionListener(e -> {
            loadAvailableCourses();
            loadMyEnrollments();
        });
        selectorBar.add(cbStudents);

        selectorBar.add(new JLabel("Select Semester:"));
        cbSemesters = new JComboBox<>();
        cbSemesters.setPreferredSize(new Dimension(200, 32));
        cbSemesters.addActionListener(e -> loadAvailableCourses());
        selectorBar.add(cbSemesters);

        JButton btnEnroll = UITheme.createSuccessButton("Enroll in Selected Course");
        btnEnroll.addActionListener(e -> handleEnrollCourse());
        selectorBar.add(btnEnroll);

        JButton btnDrop = UITheme.createDangerButton("Drop Course");
        btnDrop.addActionListener(e -> handleDropCourse());
        selectorBar.add(btnDrop);

        add(selectorBar, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(320);
        splitPane.setResizeWeight(0.5);

        JPanel topContainer = new JPanel(new BorderLayout(8, 8));
        topContainer.setBackground(UITheme.BG_LIGHT);
        JLabel lblAvail = new JLabel("Available Course Offerings (Select a course below to enroll):");
        lblAvail.setFont(UITheme.FONT_HEADER);
        topContainer.add(lblAvail, BorderLayout.NORTH);

        String[] availCols = {"Offering ID", "Course Code", "Course Title", "Credits", "Section", "Faculty", "Available Seats", "Prerequisites"};
        modelAvailableOfferings = new DefaultTableModel(availCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblAvailableOfferings = new JTable(modelAvailableOfferings);
        tblAvailableOfferings.setRowHeight(28);
        tblAvailableOfferings.setFont(UITheme.FONT_BODY);
        tblAvailableOfferings.getTableHeader().setFont(UITheme.FONT_BOLD);
        tblAvailableOfferings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        topContainer.add(new JScrollPane(tblAvailableOfferings), BorderLayout.CENTER);

        splitPane.setTopComponent(topContainer);

        JPanel bottomContainer = new JPanel(new BorderLayout(8, 8));
        bottomContainer.setBackground(UITheme.BG_LIGHT);
        JLabel lblMine = new JLabel("My Enrolled Courses & Academic History:");
        lblMine.setFont(UITheme.FONT_HEADER);
        bottomContainer.add(lblMine, BorderLayout.NORTH);

        String[] myCols = {"Enrollment ID", "Course Code", "Course Title", "Credits", "Semester", "Section", "Status", "Grade"};
        modelMyEnrollments = new DefaultTableModel(myCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblMyEnrollments = new JTable(modelMyEnrollments);
        tblMyEnrollments.setRowHeight(28);
        tblMyEnrollments.setFont(UITheme.FONT_BODY);
        tblMyEnrollments.getTableHeader().setFont(UITheme.FONT_BOLD);
        tblMyEnrollments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bottomContainer.add(new JScrollPane(tblMyEnrollments), BorderLayout.CENTER);

        splitPane.setBottomComponent(bottomContainer);

        add(splitPane, BorderLayout.CENTER);
    }

    public void loadStudentsAndSemesters() {
        cbStudents.removeAllItems();
        List<Student> students = studentDAO.getAllStudents();
        for (Student s : students) {
            cbStudents.addItem(s);
        }

        cbSemesters.removeAllItems();
        List<Semester> semesters = semesterDAO.getAllSemesters();
        for (Semester s : semesters) {
            cbSemesters.addItem(s);
        }

        loadAvailableCourses();
        loadMyEnrollments();
    }

    private void loadAvailableCourses() {
        modelAvailableOfferings.setRowCount(0);
        Semester sem = (Semester) cbSemesters.getSelectedItem();
        if (sem == null) return;

        List<CourseOffering> list = offeringDAO.getOfferingsBySemester(sem.getSemesterId());
        for (CourseOffering o : list) {
            List<Course> prereqs = courseDAO.getPrerequisiteCourses(o.getCourseId());
            StringBuilder prereqStr = new StringBuilder();
            for (Course p : prereqs) {
                if (prereqStr.length() > 0) prereqStr.append(", ");
                prereqStr.append(p.getCourseCode());
            }

            int seatsLeft = Math.max(0, o.getCapacity() - o.getEnrolledCount());
            String seatInfo = seatsLeft + " left (Total: " + o.getCapacity() + ")";

            modelAvailableOfferings.addRow(new Object[]{
                    o.getOfferingId(),
                    o.getCourseCode(),
                    o.getCourseTitle(),
                    o.getCredits(),
                    o.getSection(),
                    o.getFacultyName() != null ? o.getFacultyName() : "TBD",
                    seatInfo,
                    prereqStr.length() > 0 ? prereqStr.toString() : "None"
            });
        }
    }

    private void loadMyEnrollments() {
        modelMyEnrollments.setRowCount(0);
        Student s = (Student) cbStudents.getSelectedItem();
        if (s == null) return;

        List<Enrollment> list = enrollmentDAO.getEnrollmentsByStudent(s.getUserId());
        for (Enrollment e : list) {
            modelMyEnrollments.addRow(new Object[]{
                    e.getEnrollmentId(),
                    e.getCourseCode(),
                    e.getCourseTitle(),
                    e.getCredits(),
                    e.getSemesterName(),
                    e.getSection(),
                    e.getStatus(),
                    e.getGrade() != null ? e.getGrade() : "Pending"
            });
        }
    }

    private void handleEnrollCourse() {
        Student s = (Student) cbStudents.getSelectedItem();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Please select a student.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = tblAvailableOfferings.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course offering to enroll.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int offeringId = (int) modelAvailableOfferings.getValueAt(row, 0);
        String courseCode = (String) modelAvailableOfferings.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Enroll " + s.getFullName() + " in " + courseCode + "?",
                "Confirm Enrollment",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String result = enrollmentDAO.enrollStudent(s.getUserId(), offeringId);
            if ("SUCCESS".equalsIgnoreCase(result)) {
                JOptionPane.showMessageDialog(this, "Successfully enrolled in " + courseCode + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAvailableCourses();
                loadMyEnrollments();
            } else {
                JOptionPane.showMessageDialog(this, result, "Enrollment Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDropCourse() {
        Student s = (Student) cbStudents.getSelectedItem();
        if (s == null) return;

        int row = tblMyEnrollments.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an enrolled course from the bottom table to drop.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int enrollId = (int) modelMyEnrollments.getValueAt(row, 0);
        String courseCode = (String) modelMyEnrollments.getValueAt(row, 1);
        String status = (String) modelMyEnrollments.getValueAt(row, 6);

        if (!"ENROLLED".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Only active 'ENROLLED' courses can be dropped.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to drop " + courseCode + "?",
                "Confirm Drop",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = enrollmentDAO.dropCourse(enrollId, s.getUserId());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Course dropped successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAvailableCourses();
                loadMyEnrollments();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to drop course.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
