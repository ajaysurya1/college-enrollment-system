package com.university.ui;

import com.university.Department;
import com.university.DepartmentDAO;
import com.university.Student;
import com.university.StudentDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentsPanel extends JPanel {

    private final StudentDAO studentDAO;
    private final DepartmentDAO departmentDAO;

    private JTable tblStudents;
    private DefaultTableModel modelStudents;
    private JTextField txtSearch;

    public StudentsPanel() {
        this.studentDAO = new StudentDAO();
        this.departmentDAO = new DepartmentDAO();
        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
        loadStudents();
    }

    private void initUI() {
        JPanel topBar = UITheme.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));

        JLabel lblSearch = new JLabel("Search Student:");
        lblSearch.setFont(UITheme.FONT_BOLD);
        topBar.add(lblSearch);

        txtSearch = new JTextField(18);
        txtSearch.setPreferredSize(new Dimension(180, 32));
        topBar.add(txtSearch);

        JButton btnSearch = UITheme.createPrimaryButton("Search");
        btnSearch.addActionListener(e -> searchStudents());
        topBar.add(btnSearch);

        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            loadStudents();
        });
        topBar.add(btnReset);

        topBar.add(Box.createHorizontalStrut(20));

        JButton btnAdd = UITheme.createSuccessButton("+ Add New Student");
        btnAdd.addActionListener(e -> showAddStudentDialog());
        topBar.add(btnAdd);

        JButton btnDeactivate = UITheme.createDangerButton("Deactivate Student");
        btnDeactivate.addActionListener(e -> handleDeactivateStudent());
        topBar.add(btnDeactivate);

        add(topBar, BorderLayout.NORTH);

        String[] cols = {"User ID", "Roll Number", "Full Name", "Username", "Email", "Programme", "Year", "Active"};
        modelStudents = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblStudents = new JTable(modelStudents);
        tblStudents.setRowHeight(28);
        tblStudents.setFont(UITheme.FONT_BODY);
        tblStudents.getTableHeader().setFont(UITheme.FONT_BOLD);
        tblStudents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(tblStudents), BorderLayout.CENTER);
    }

    public void loadStudents() {
        modelStudents.setRowCount(0);
        List<Student> students = studentDAO.getAllStudents();
        for (Student s : students) {
            modelStudents.addRow(new Object[]{
                    s.getUserId(),
                    s.getRollNumber(),
                    s.getFullName(),
                    s.getUsername(),
                    s.getEmail(),
                    s.getProgramme(),
                    s.getYearOfStudy(),
                    s.isActive() ? "Yes" : "No"
            });
        }
    }

    private void searchStudents() {
        String query = txtSearch.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            loadStudents();
            return;
        }

        modelStudents.setRowCount(0);
        List<Student> students = studentDAO.getAllStudents();
        for (Student s : students) {
            if (s.getFullName().toLowerCase().contains(query) || s.getRollNumber().toLowerCase().contains(query)) {
                modelStudents.addRow(new Object[]{
                        s.getUserId(),
                        s.getRollNumber(),
                        s.getFullName(),
                        s.getUsername(),
                        s.getEmail(),
                        s.getProgramme(),
                        s.getYearOfStudy(),
                        s.isActive() ? "Yes" : "No"
                });
            }
        }
    }

    private void showAddStudentDialog() {
        JTextField txtUser = new JTextField(15);
        JTextField txtName = new JTextField(15);
        JTextField txtEmail = new JTextField(15);
        JTextField txtRoll = new JTextField(15);
        JTextField txtProg = new JTextField(15);
        txtProg.setText("B.Tech Computer Science");
        JComboBox<Integer> cbYear = new JComboBox<>(new Integer[]{1, 2, 3, 4});

        JComboBox<Department> cbDept = new JComboBox<>();
        for (Department d : departmentDAO.getAllDepartments()) {
            cbDept.addItem(d);
        }

        JPanel panel = new JPanel(new GridLayout(7, 2, 8, 8));
        panel.add(new JLabel("Username:"));
        panel.add(txtUser);
        panel.add(new JLabel("Full Name:"));
        panel.add(txtName);
        panel.add(new JLabel("Email Address:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Roll Number:"));
        panel.add(txtRoll);
        panel.add(new JLabel("Department:"));
        panel.add(cbDept);
        panel.add(new JLabel("Programme:"));
        panel.add(txtProg);
        panel.add(new JLabel("Year of Study:"));
        panel.add(cbYear);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Student", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String username = txtUser.getText().trim();
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            String roll = txtRoll.getText().trim();
            String prog = txtProg.getText().trim();
            int year = (Integer) cbYear.getSelectedItem();
            Department dept = (Department) cbDept.getSelectedItem();

            if (username.isEmpty() || name.isEmpty() || roll.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student s = new Student(0, username, name, email, dept != null ? dept.getDepartmentId() : 1, true, roll, prog, year);
            boolean ok = studentDAO.addStudent(s, "student123");
            if (ok) {
                JOptionPane.showMessageDialog(this, "Student added successfully! (Default Password: student123)", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStudents();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add student. Username or Roll Number may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeactivateStudent() {
        int row = tblStudents.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) modelStudents.getValueAt(row, 0);
        String name = (String) modelStudents.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to deactivate student " + name + "?",
                "Confirm Deactivation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = studentDAO.deleteStudent(userId);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Student deactivated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStudents();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to deactivate student.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
