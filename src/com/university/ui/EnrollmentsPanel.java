package com.university.ui;

import com.university.Enrollment;
import com.university.EnrollmentDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EnrollmentsPanel extends JPanel {

    private final EnrollmentDAO enrollmentDAO;
    private JTable tblEnrollments;
    private DefaultTableModel modelEnrollments;

    public EnrollmentsPanel() {
        this.enrollmentDAO = new EnrollmentDAO();
        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
        loadEnrollments();
    }

    private void initUI() {
        JPanel topBar = UITheme.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));

        JButton btnGrade = UITheme.createPrimaryButton("Assign / Override Grade");
        btnGrade.addActionListener(e -> showGradeDialog());
        topBar.add(btnGrade);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadEnrollments());
        topBar.add(btnRefresh);

        add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Roll Number", "Student Name", "Course Code", "Course Title", "Semester", "Section", "Enroll Date", "Status", "Grade"};
        modelEnrollments = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblEnrollments = new JTable(modelEnrollments);
        tblEnrollments.setRowHeight(28);
        tblEnrollments.setFont(UITheme.FONT_BODY);
        tblEnrollments.getTableHeader().setFont(UITheme.FONT_BOLD);
        tblEnrollments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(tblEnrollments), BorderLayout.CENTER);
    }

    public void loadEnrollments() {
        modelEnrollments.setRowCount(0);
        List<Enrollment> list = enrollmentDAO.getAllEnrollments();
        for (Enrollment e : list) {
            modelEnrollments.addRow(new Object[]{
                    e.getEnrollmentId(),
                    e.getStudentRoll(),
                    e.getStudentName(),
                    e.getCourseCode(),
                    e.getCourseTitle(),
                    e.getSemesterName(),
                    e.getSection(),
                    e.getEnrollmentDate(),
                    e.getStatus(),
                    e.getGrade() != null ? e.getGrade() : "Pending"
            });
        }
    }

    private void showGradeDialog() {
        int row = tblEnrollments.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an enrollment record from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) modelEnrollments.getValueAt(row, 0);
        String student = (String) modelEnrollments.getValueAt(row, 2);
        String course = (String) modelEnrollments.getValueAt(row, 3);

        String[] grades = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F"};
        JComboBox<String> cbGrade = new JComboBox<>(grades);

        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Student & Course:"));
        panel.add(new JLabel(student + " (" + course + ")"));
        panel.add(new JLabel("Assign Grade:"));
        panel.add(cbGrade);

        int result = JOptionPane.showConfirmDialog(this, panel, "Assign Grade", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String grade = (String) cbGrade.getSelectedItem();
            boolean ok = enrollmentDAO.submitGrade(id, grade);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Grade assigned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEnrollments();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit grade.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
