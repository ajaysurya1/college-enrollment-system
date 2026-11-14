package com.university.ui;

import com.university.Course;
import com.university.CourseDAO;
import com.university.Department;
import com.university.DepartmentDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CoursesPanel extends JPanel {

    private final CourseDAO courseDAO;
    private final DepartmentDAO departmentDAO;

    private JTable tblCourses;
    private DefaultTableModel modelCourses;

    public CoursesPanel() {
        this.courseDAO = new CourseDAO();
        this.departmentDAO = new DepartmentDAO();
        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
        loadCourses();
    }

    private void initUI() {
        JPanel topBar = UITheme.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));

        JButton btnAdd = UITheme.createSuccessButton("+ Add New Course");
        btnAdd.addActionListener(e -> showAddCourseDialog());
        topBar.add(btnAdd);

        JButton btnPrereq = UITheme.createPrimaryButton("Manage Prerequisites");
        btnPrereq.addActionListener(e -> showManagePrereqDialog());
        topBar.add(btnPrereq);

        JButton btnDelete = UITheme.createDangerButton("Delete Course");
        btnDelete.addActionListener(e -> handleDeleteCourse());
        topBar.add(btnDelete);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadCourses());
        topBar.add(btnRefresh);

        add(topBar, BorderLayout.NORTH);

        String[] cols = {"Course ID", "Course Code", "Course Title", "Credits", "Department", "Prerequisites", "Description"};
        modelCourses = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblCourses = new JTable(modelCourses);
        tblCourses.setRowHeight(28);
        tblCourses.setFont(UITheme.FONT_BODY);
        tblCourses.getTableHeader().setFont(UITheme.FONT_BOLD);
        tblCourses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(tblCourses), BorderLayout.CENTER);
    }

    public void loadCourses() {
        modelCourses.setRowCount(0);
        List<Course> courses = courseDAO.getAllCourses();
        for (Course c : courses) {
            List<Course> prereqs = courseDAO.getPrerequisiteCourses(c.getCourseId());
            StringBuilder prereqStr = new StringBuilder();
            for (Course p : prereqs) {
                if (prereqStr.length() > 0) prereqStr.append(", ");
                prereqStr.append(p.getCourseCode());
            }

            modelCourses.addRow(new Object[]{
                    c.getCourseId(),
                    c.getCourseCode(),
                    c.getCourseTitle(),
                    c.getCredits(),
                    c.getDepartmentName(),
                    prereqStr.length() > 0 ? prereqStr.toString() : "None",
                    c.getDescription()
            });
        }
    }

    private void showAddCourseDialog() {
        JTextField txtCode = new JTextField(12);
        JTextField txtTitle = new JTextField(16);
        JComboBox<Integer> cbCredits = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        cbCredits.setSelectedItem(4);

        JComboBox<Department> cbDept = new JComboBox<>();
        for (Department d : departmentDAO.getAllDepartments()) {
            cbDept.addItem(d);
        }

        JTextArea txtDesc = new JTextArea(3, 16);
        txtDesc.setLineWrap(true);

        JPanel panel = new JPanel(new GridLayout(5, 2, 8, 8));
        panel.add(new JLabel("Course Code (e.g. CS202):"));
        panel.add(txtCode);
        panel.add(new JLabel("Course Title:"));
        panel.add(txtTitle);
        panel.add(new JLabel("Credits:"));
        panel.add(cbCredits);
        panel.add(new JLabel("Department:"));
        panel.add(cbDept);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(txtDesc));

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Course", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String code = txtCode.getText().trim();
            String title = txtTitle.getText().trim();
            int credits = (Integer) cbCredits.getSelectedItem();
            Department dept = (Department) cbDept.getSelectedItem();
            String desc = txtDesc.getText().trim();

            if (code.isEmpty() || title.isEmpty() || dept == null) {
                JOptionPane.showMessageDialog(this, "Please fill in course code and title.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Course c = new Course(0, code, title, credits, dept.getDepartmentId(), desc);
            boolean ok = courseDAO.addCourse(c);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Course created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCourses();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create course. Code may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showManagePrereqDialog() {
        int row = tblCourses.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a target course from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int targetCourseId = (int) modelCourses.getValueAt(row, 0);
        String targetCode = (String) modelCourses.getValueAt(row, 1);

        JComboBox<Course> cbPrereq = new JComboBox<>();
        for (Course c : courseDAO.getAllCourses()) {
            if (c.getCourseId() != targetCourseId) {
                cbPrereq.addItem(c);
            }
        }

        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Target Course:"));
        panel.add(new JLabel(targetCode));
        panel.add(new JLabel("Select Prerequisite Course:"));
        panel.add(cbPrereq);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Course Prerequisite", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            Course selectedPrereq = (Course) cbPrereq.getSelectedItem();
            if (selectedPrereq != null) {
                boolean ok = courseDAO.addPrerequisite(targetCourseId, selectedPrereq.getCourseId());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Prerequisite added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add prerequisite.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void handleDeleteCourse() {
        int row = tblCourses.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId = (int) modelCourses.getValueAt(row, 0);
        String code = (String) modelCourses.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete course " + code + "? (Cannot delete if enrolled offerings exist)",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = courseDAO.deleteCourse(courseId);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Course deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCourses();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete course. It may be referenced in existing course offerings.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
