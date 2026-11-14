package com.university.ui.admin;

import com.university.model.Administrator;
import com.university.model.Course;
import com.university.model.CourseOffering;
import com.university.model.Department;
import com.university.model.Enrollment;
import com.university.model.Faculty;
import com.university.model.Semester;
import com.university.model.Student;
import com.university.service.CourseOfferingService;
import com.university.service.CourseService;
import com.university.service.DepartmentService;
import com.university.service.EnrollmentService;
import com.university.service.FacultyService;
import com.university.service.SemesterService;
import com.university.service.StudentService;
import com.university.ui.auth.LoginFrame;
import com.university.ui.components.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class AdminDashboard extends JFrame {

    private final Administrator currentAdmin;
    private final DepartmentService departmentService;
    private final StudentService studentService;
    private final FacultyService facultyService;
    private final CourseService courseService;
    private final SemesterService semesterService;
    private final CourseOfferingService offeringService;
    private final EnrollmentService enrollmentService;

    private JLabel lblStatStudents;
    private JLabel lblStatFaculty;
    private JLabel lblStatCourses;
    private JLabel lblStatEnrollments;

    private JTable tblDepartments;
    private DefaultTableModel modelDepartments;

    private JTable tblStudents;
    private DefaultTableModel modelStudents;

    private JTable tblFaculty;
    private DefaultTableModel modelFaculty;

    private JTable tblCourses;
    private DefaultTableModel modelCourses;

    private JTable tblSemesters;
    private DefaultTableModel modelSemesters;

    private JTable tblOfferings;
    private DefaultTableModel modelOfferings;

    private JTable tblEnrollments;
    private DefaultTableModel modelEnrollments;

    public AdminDashboard(Administrator admin) {
        this.currentAdmin = admin;
        this.departmentService = new DepartmentService();
        this.studentService = new StudentService();
        this.facultyService = new FacultyService();
        this.courseService = new CourseService();
        this.semesterService = new SemesterService();
        this.offeringService = new CourseOfferingService();
        this.enrollmentService = new EnrollmentService();

        initUI();
        refreshAllData();
    }

    private void initUI() {
        setTitle("Admin Portal - " + currentAdmin.getFullName());
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.NAVY);
        topBar.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel(currentAdmin.getDashboardMessage());
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

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BOLD);

        tabs.addTab("Dashboard Overview", createOverviewTab());
        tabs.addTab("Departments", createDepartmentTab());
        tabs.addTab("Students", createStudentTab());
        tabs.addTab("Faculty", createFacultyTab());
        tabs.addTab("Courses", createCourseTab());
        tabs.addTab("Semesters", createSemesterTab());
        tabs.addTab("Course Offerings", createOfferingsTab());
        tabs.addTab("Enrollments", createEnrollmentsTab());

        mainPanel.add(tabs, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JPanel createOverviewTab() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        statsGrid.setBackground(UITheme.BG_LIGHT);

        JPanel card1 = UITheme.createCardPanel();
        card1.setLayout(new BorderLayout());
        card1.add(new JLabel("Total Active Students", SwingConstants.CENTER), BorderLayout.NORTH);
        lblStatStudents = new JLabel("0", SwingConstants.CENTER);
        lblStatStudents.setFont(UITheme.FONT_STAT);
        lblStatStudents.setForeground(UITheme.NAVY);
        card1.add(lblStatStudents, BorderLayout.CENTER);

        JPanel card2 = UITheme.createCardPanel();
        card2.setLayout(new BorderLayout());
        card2.add(new JLabel("Total Faculty Members", SwingConstants.CENTER), BorderLayout.NORTH);
        lblStatFaculty = new JLabel("0", SwingConstants.CENTER);
        lblStatFaculty.setFont(UITheme.FONT_STAT);
        lblStatFaculty.setForeground(UITheme.NAVY);
        card2.add(lblStatFaculty, BorderLayout.CENTER);

        JPanel card3 = UITheme.createCardPanel();
        card3.setLayout(new BorderLayout());
        card3.add(new JLabel("Total Approved Courses", SwingConstants.CENTER), BorderLayout.NORTH);
        lblStatCourses = new JLabel("0", SwingConstants.CENTER);
        lblStatCourses.setFont(UITheme.FONT_STAT);
        lblStatCourses.setForeground(UITheme.NAVY);
        card3.add(lblStatCourses, BorderLayout.CENTER);

        JPanel card4 = UITheme.createCardPanel();
        card4.setLayout(new BorderLayout());
        card4.add(new JLabel("Active Enrollments", SwingConstants.CENTER), BorderLayout.NORTH);
        lblStatEnrollments = new JLabel("0", SwingConstants.CENTER);
        lblStatEnrollments.setFont(UITheme.FONT_STAT);
        lblStatEnrollments.setForeground(UITheme.ACCENT_GREEN);
        card4.add(lblStatEnrollments, BorderLayout.CENTER);

        statsGrid.add(card1);
        statsGrid.add(card2);
        statsGrid.add(card3);
        statsGrid.add(card4);

        panel.add(statsGrid, BorderLayout.CENTER);

        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.CENTER));
        quickActions.setBackground(UITheme.BG_LIGHT);
        JButton btnRefreshAll = UITheme.createPrimaryButton("Refresh Statistics");
        btnRefreshAll.addActionListener(e -> refreshAllData());
        quickActions.add(btnRefreshAll);

        panel.add(quickActions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createDepartmentTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] cols = {"Department ID", "Code", "Department Name"};
        modelDepartments = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblDepartments = new JTable(modelDepartments);
        tblDepartments.setRowHeight(26);
        panel.add(new JScrollPane(tblDepartments), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actions.setBackground(UITheme.BG_LIGHT);

        JButton btnAdd = UITheme.createPrimaryButton("Add Department");
        btnAdd.addActionListener(e -> showAddDepartmentDialog());
        actions.add(btnAdd);

        JButton btnDelete = UITheme.createDangerButton("Delete Department");
        btnDelete.addActionListener(e -> {
            int row = tblDepartments.getSelectedRow();
            if (row >= 0) {
                int id = (int) modelDepartments.getValueAt(row, 0);
                if (JOptionPane.showConfirmDialog(this, "Delete selected department?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    departmentService.deleteDepartment(id);
                    loadDepartments();
                }
            }
        });
        actions.add(btnDelete);

        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void showAddDepartmentDialog() {
        JTextField txtCode = new JTextField();
        JTextField txtName = new JTextField();

        Object[] fields = {
                "Department Code (e.g. CS):", txtCode,
                "Department Name:", txtName
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add Department", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                departmentService.createDepartment(txtCode.getText(), txtName.getText());
                loadDepartments();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createStudentTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] cols = {"User ID", "Roll Number", "Full Name", "Email", "Programme", "Year", "Active"};
        modelStudents = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblStudents = new JTable(modelStudents);
        tblStudents.setRowHeight(26);
        panel.add(new JScrollPane(tblStudents), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actions.setBackground(UITheme.BG_LIGHT);

        JButton btnAdd = UITheme.createPrimaryButton("Register New Student");
        btnAdd.addActionListener(e -> showAddStudentDialog());
        actions.add(btnAdd);

        JButton btnDeactivate = UITheme.createDangerButton("Deactivate Student");
        btnDeactivate.addActionListener(e -> {
            int row = tblStudents.getSelectedRow();
            if (row >= 0) {
                int id = (int) modelStudents.getValueAt(row, 0);
                if (JOptionPane.showConfirmDialog(this, "Deactivate selected student?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    studentService.deactivateStudent(id);
                    loadStudents();
                }
            }
        });
        actions.add(btnDeactivate);

        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void showAddStudentDialog() {
        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JTextField txtName = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtRoll = new JTextField();
        JTextField txtProg = new JTextField();
        JTextField txtYear = new JTextField("1");

        JComboBox<Department> cbDept = new JComboBox<>();
        for (Department d : departmentService.getAllDepartments()) {
            cbDept.addItem(d);
        }

        Object[] fields = {
                "Username:", txtUser,
                "Password:", txtPass,
                "Full Name:", txtName,
                "Email:", txtEmail,
                "Roll Number:", txtRoll,
                "Programme (e.g. B.Tech CS):", txtProg,
                "Year of Study:", txtYear,
                "Department:", cbDept
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Register Student", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Department selDept = (Department) cbDept.getSelectedItem();
                Integer deptId = selDept != null ? selDept.getDepartmentId() : null;
                int year = Integer.parseInt(txtYear.getText().trim());

                studentService.registerStudent(txtUser.getText(), new String(txtPass.getPassword()),
                        txtName.getText(), txtEmail.getText(), deptId, txtRoll.getText(), txtProg.getText(), year);
                loadStudents();
                refreshAllData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createFacultyTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] cols = {"User ID", "Full Name", "Username", "Email", "Designation", "Active"};
        modelFaculty = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblFaculty = new JTable(modelFaculty);
        tblFaculty.setRowHeight(26);
        panel.add(new JScrollPane(tblFaculty), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actions.setBackground(UITheme.BG_LIGHT);

        JButton btnAdd = UITheme.createPrimaryButton("Register New Faculty");
        btnAdd.addActionListener(e -> showAddFacultyDialog());
        actions.add(btnAdd);

        JButton btnDeactivate = UITheme.createDangerButton("Deactivate Faculty");
        btnDeactivate.addActionListener(e -> {
            int row = tblFaculty.getSelectedRow();
            if (row >= 0) {
                int id = (int) modelFaculty.getValueAt(row, 0);
                if (JOptionPane.showConfirmDialog(this, "Deactivate selected faculty?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    facultyService.deactivateFaculty(id);
                    loadFaculty();
                }
            }
        });
        actions.add(btnDeactivate);

        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void showAddFacultyDialog() {
        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JTextField txtName = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtDesig = new JTextField("Assistant Professor");

        JComboBox<Department> cbDept = new JComboBox<>();
        for (Department d : departmentService.getAllDepartments()) {
            cbDept.addItem(d);
        }

        Object[] fields = {
                "Username:", txtUser,
                "Password:", txtPass,
                "Full Name:", txtName,
                "Email:", txtEmail,
                "Designation:", txtDesig,
                "Department:", cbDept
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Register Faculty", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Department selDept = (Department) cbDept.getSelectedItem();
                Integer deptId = selDept != null ? selDept.getDepartmentId() : null;

                facultyService.registerFaculty(txtUser.getText(), new String(txtPass.getPassword()),
                        txtName.getText(), txtEmail.getText(), deptId, txtDesig.getText());
                loadFaculty();
                refreshAllData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createCourseTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] cols = {"Course ID", "Code", "Title", "Credits", "Department", "Description"};
        modelCourses = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblCourses = new JTable(modelCourses);
        tblCourses.setRowHeight(26);
        panel.add(new JScrollPane(tblCourses), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actions.setBackground(UITheme.BG_LIGHT);

        JButton btnAdd = UITheme.createPrimaryButton("Add Course");
        btnAdd.addActionListener(e -> showAddCourseDialog());
        actions.add(btnAdd);

        JButton btnPrereq = UITheme.createPrimaryButton("Manage Prerequisites");
        btnPrereq.addActionListener(e -> showPrerequisiteDialog());
        actions.add(btnPrereq);

        JButton btnDelete = UITheme.createDangerButton("Delete Course");
        btnDelete.addActionListener(e -> {
            int row = tblCourses.getSelectedRow();
            if (row >= 0) {
                int id = (int) modelCourses.getValueAt(row, 0);
                if (JOptionPane.showConfirmDialog(this, "Delete selected course?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    courseService.deleteCourse(id);
                    loadCourses();
                }
            }
        });
        actions.add(btnDelete);

        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void showAddCourseDialog() {
        JTextField txtCode = new JTextField();
        JTextField txtTitle = new JTextField();
        JTextField txtCredits = new JTextField("3");
        JTextField txtDesc = new JTextField();

        JComboBox<Department> cbDept = new JComboBox<>();
        for (Department d : departmentService.getAllDepartments()) {
            cbDept.addItem(d);
        }

        Object[] fields = {
                "Course Code (e.g. CS101):", txtCode,
                "Course Title:", txtTitle,
                "Credits (positive integer):", txtCredits,
                "Department:", cbDept,
                "Description:", txtDesc
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add Course", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Department selDept = (Department) cbDept.getSelectedItem();
                int deptId = selDept != null ? selDept.getDepartmentId() : 1;
                int credits = Integer.parseInt(txtCredits.getText().trim());

                courseService.createCourse(txtCode.getText(), txtTitle.getText(), credits, deptId, txtDesc.getText());
                loadCourses();
                refreshAllData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showPrerequisiteDialog() {
        int row = tblCourses.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a target course from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int targetCourseId = (int) modelCourses.getValueAt(row, 0);
        String targetCode = (String) modelCourses.getValueAt(row, 1);

        JComboBox<Course> cbPrereq = new JComboBox<>();
        for (Course c : courseService.getAllCourses()) {
            if (c.getCourseId() != targetCourseId) {
                cbPrereq.addItem(c);
            }
        }

        Object[] fields = {
                "Target Course: " + targetCode,
                "Select Prerequisite Course:", cbPrereq
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add Prerequisite", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Course sel = (Course) cbPrereq.getSelectedItem();
            if (sel != null) {
                courseService.addPrerequisite(targetCourseId, sel.getCourseId());
                JOptionPane.showMessageDialog(this, "Prerequisite added!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private JPanel createSemesterTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] cols = {"Semester ID", "Name", "Academic Year", "Start Date", "End Date", "Reg Start", "Reg End", "Open Now"};
        modelSemesters = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblSemesters = new JTable(modelSemesters);
        tblSemesters.setRowHeight(26);
        panel.add(new JScrollPane(tblSemesters), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actions.setBackground(UITheme.BG_LIGHT);

        JButton btnAdd = UITheme.createPrimaryButton("Add Semester");
        btnAdd.addActionListener(e -> showAddSemesterDialog());
        actions.add(btnAdd);

        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void showAddSemesterDialog() {
        JTextField txtName = new JTextField("Spring 2027");
        JTextField txtYear = new JTextField("2026-2027");
        JTextField txtStart = new JTextField("2027-01-10");
        JTextField txtEnd = new JTextField("2027-05-25");
        JTextField txtRegStart = new JTextField("2026-12-01");
        JTextField txtRegEnd = new JTextField("2027-02-15");

        Object[] fields = {
                "Semester Name:", txtName,
                "Academic Year:", txtYear,
                "Start Date (YYYY-MM-DD):", txtStart,
                "End Date (YYYY-MM-DD):", txtEnd,
                "Registration Start (YYYY-MM-DD):", txtRegStart,
                "Registration End (YYYY-MM-DD):", txtRegEnd
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add Semester", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                LocalDate start = LocalDate.parse(txtStart.getText().trim());
                LocalDate end = LocalDate.parse(txtEnd.getText().trim());
                LocalDate regStart = LocalDate.parse(txtRegStart.getText().trim());
                LocalDate regEnd = LocalDate.parse(txtRegEnd.getText().trim());

                semesterService.createSemester(txtName.getText(), txtYear.getText(), start, end, regStart, regEnd);
                loadSemesters();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createOfferingsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] cols = {"Offering ID", "Course Code", "Course Title", "Semester", "Section", "Capacity", "Enrolled", "Faculty Assigned"};
        modelOfferings = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblOfferings = new JTable(modelOfferings);
        tblOfferings.setRowHeight(26);
        panel.add(new JScrollPane(tblOfferings), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actions.setBackground(UITheme.BG_LIGHT);

        JButton btnAdd = UITheme.createPrimaryButton("Create Course Offering");
        btnAdd.addActionListener(e -> showAddOfferingDialog());
        actions.add(btnAdd);

        JButton btnAssignFaculty = UITheme.createPrimaryButton("Assign Faculty");
        btnAssignFaculty.addActionListener(e -> showAssignFacultyDialog());
        actions.add(btnAssignFaculty);

        JButton btnDelete = UITheme.createDangerButton("Delete Offering");
        btnDelete.addActionListener(e -> {
            int row = tblOfferings.getSelectedRow();
            if (row >= 0) {
                int id = (int) modelOfferings.getValueAt(row, 0);
                if (JOptionPane.showConfirmDialog(this, "Delete selected offering?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    offeringService.deleteOffering(id);
                    loadOfferings();
                }
            }
        });
        actions.add(btnDelete);

        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void showAddOfferingDialog() {
        JComboBox<Course> cbCourse = new JComboBox<>();
        for (Course c : courseService.getAllCourses()) {
            cbCourse.addItem(c);
        }

        JComboBox<Semester> cbSem = new JComboBox<>();
        for (Semester s : semesterService.getAllSemesters()) {
            cbSem.addItem(s);
        }

        JComboBox<Faculty> cbFac = new JComboBox<>();
        for (Faculty f : facultyService.getAllFaculty()) {
            cbFac.addItem(f);
        }

        JTextField txtSec = new JTextField("A");
        JTextField txtCap = new JTextField("30");

        Object[] fields = {
                "Select Course:", cbCourse,
                "Select Semester:", cbSem,
                "Assign Faculty:", cbFac,
                "Section:", txtSec,
                "Capacity:", txtCap
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Create Offering", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Course c = (Course) cbCourse.getSelectedItem();
                Semester s = (Semester) cbSem.getSelectedItem();
                Faculty f = (Faculty) cbFac.getSelectedItem();
                int cap = Integer.parseInt(txtCap.getText().trim());

                if (c != null && s != null) {
                    offeringService.createOffering(c.getCourseId(), s.getSemesterId(), f != null ? f.getUserId() : null, txtSec.getText(), cap);
                    loadOfferings();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAssignFacultyDialog() {
        int row = tblOfferings.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course offering first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int offeringId = (int) modelOfferings.getValueAt(row, 0);

        JComboBox<Faculty> cbFac = new JComboBox<>();
        for (Faculty f : facultyService.getAllFaculty()) {
            cbFac.addItem(f);
        }

        Object[] fields = {
                "Offering ID: " + offeringId,
                "Select Faculty Member:", cbFac
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Assign Faculty", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Faculty f = (Faculty) cbFac.getSelectedItem();
            if (f != null) {
                offeringService.assignFaculty(offeringId, f.getUserId());
                loadOfferings();
            }
        }
    }

    private JPanel createEnrollmentsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] cols = {"Enrollment ID", "Roll Number", "Student Name", "Course", "Title", "Semester", "Section", "Date", "Status", "Grade"};
        modelEnrollments = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblEnrollments = new JTable(modelEnrollments);
        tblEnrollments.setRowHeight(26);
        panel.add(new JScrollPane(tblEnrollments), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actions.setBackground(UITheme.BG_LIGHT);

        JButton btnRefresh = UITheme.createPrimaryButton("Refresh Enrollments");
        btnRefresh.addActionListener(e -> loadEnrollments());
        actions.add(btnRefresh);

        JButton btnOverrideGrade = UITheme.createSuccessButton("Admin Grade Override");
        btnOverrideGrade.addActionListener(e -> showAdminGradeDialog());
        actions.add(btnOverrideGrade);

        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void showAdminGradeDialog() {
        int row = tblEnrollments.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an enrollment record first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int enrollmentId = (int) modelEnrollments.getValueAt(row, 0);
        String student = (String) modelEnrollments.getValueAt(row, 2);
        String course = (String) modelEnrollments.getValueAt(row, 3);

        String[] grades = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F"};
        JComboBox<String> cbGrade = new JComboBox<>(grades);

        Object[] fields = {
                "Student: " + student + " (" + course + ")",
                "Assign Final Grade:", cbGrade
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Admin Grade Override", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                enrollmentService.submitGradeByAdmin(enrollmentId, (String) cbGrade.getSelectedItem());
                loadEnrollments();
                refreshAllData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshAllData() {
        loadDepartments();
        loadStudents();
        loadFaculty();
        loadCourses();
        loadSemesters();
        loadOfferings();
        loadEnrollments();

        lblStatStudents.setText(String.valueOf(studentService.getStudentCount()));
        lblStatFaculty.setText(String.valueOf(facultyService.getFacultyCount()));
        lblStatCourses.setText(String.valueOf(courseService.getCourseCount()));
        lblStatEnrollments.setText(String.valueOf(enrollmentService.getActiveEnrollmentCount()));
    }

    private void loadDepartments() {
        modelDepartments.setRowCount(0);
        for (Department d : departmentService.getAllDepartments()) {
            modelDepartments.addRow(new Object[]{d.getDepartmentId(), d.getDepartmentCode(), d.getDepartmentName()});
        }
    }

    private void loadStudents() {
        modelStudents.setRowCount(0);
        for (Student s : studentService.getAllStudents()) {
            modelStudents.addRow(new Object[]{
                    s.getUserId(), s.getRollNumber(), s.getFullName(), s.getEmail(), s.getProgramme(), s.getYearOfStudy(), s.isActive() ? "Yes" : "No"
            });
        }
    }

    private void loadFaculty() {
        modelFaculty.setRowCount(0);
        for (Faculty f : facultyService.getAllFaculty()) {
            modelFaculty.addRow(new Object[]{
                    f.getUserId(), f.getFullName(), f.getUsername(), f.getEmail(), f.getDesignation(), f.isActive() ? "Yes" : "No"
            });
        }
    }

    private void loadCourses() {
        modelCourses.setRowCount(0);
        for (Course c : courseService.getAllCourses()) {
            modelCourses.addRow(new Object[]{
                    c.getCourseId(), c.getCourseCode(), c.getCourseTitle(), c.getCredits(), c.getDepartmentName(), c.getDescription()
            });
        }
    }

    private void loadSemesters() {
        modelSemesters.setRowCount(0);
        for (Semester s : semesterService.getAllSemesters()) {
            modelSemesters.addRow(new Object[]{
                    s.getSemesterId(), s.getSemesterName(), s.getAcademicYear(),
                    s.getStartDate(), s.getEndDate(), s.getRegistrationStart(), s.getRegistrationEnd(),
                    s.isRegistrationOpen() ? "Yes" : "No"
            });
        }
    }

    private void loadOfferings() {
        modelOfferings.setRowCount(0);
        for (CourseOffering o : offeringService.getAllOfferings()) {
            modelOfferings.addRow(new Object[]{
                    o.getOfferingId(), o.getCourseCode(), o.getCourseTitle(), o.getSemesterName(),
                    o.getSection(), o.getCapacity(), o.getEnrolledCount(),
                    o.getFacultyName() != null ? o.getFacultyName() : "Unassigned"
            });
        }
    }

    private void loadEnrollments() {
        modelEnrollments.setRowCount(0);
        for (Enrollment e : enrollmentService.getAllEnrollments()) {
            modelEnrollments.addRow(new Object[]{
                    e.getEnrollmentId(), e.getStudentRoll(), e.getStudentName(), e.getCourseCode(),
                    e.getCourseTitle(), e.getSemesterName(), e.getSection(), e.getEnrollmentDate(),
                    e.getStatus(), e.getGrade() != null ? e.getGrade() : "Pending"
            });
        }
    }
}
