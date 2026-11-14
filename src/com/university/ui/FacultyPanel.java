package com.university.ui;

import com.university.Department;
import com.university.DepartmentDAO;
import com.university.Faculty;
import com.university.FacultyDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FacultyPanel extends JPanel {

    private final FacultyDAO facultyDAO;
    private final DepartmentDAO departmentDAO;

    private JTable tblFaculty;
    private DefaultTableModel modelFaculty;

    public FacultyPanel() {
        this.facultyDAO = new FacultyDAO();
        this.departmentDAO = new DepartmentDAO();
        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
        loadFaculty();
    }

    private void initUI() {
        JPanel topBar = UITheme.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));

        JButton btnAdd = UITheme.createSuccessButton("+ Add New Faculty");
        btnAdd.addActionListener(e -> showAddFacultyDialog());
        topBar.add(btnAdd);

        JButton btnDeactivate = UITheme.createDangerButton("Deactivate Faculty");
        btnDeactivate.addActionListener(e -> handleDeactivateFaculty());
        topBar.add(btnDeactivate);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadFaculty());
        topBar.add(btnRefresh);

        add(topBar, BorderLayout.NORTH);

        String[] cols = {"User ID", "Full Name", "Username", "Email", "Designation", "Active"};
        modelFaculty = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblFaculty = new JTable(modelFaculty);
        tblFaculty.setRowHeight(28);
        tblFaculty.setFont(UITheme.FONT_BODY);
        tblFaculty.getTableHeader().setFont(UITheme.FONT_BOLD);
        tblFaculty.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(tblFaculty), BorderLayout.CENTER);
    }

    public void loadFaculty() {
        modelFaculty.setRowCount(0);
        List<Faculty> list = facultyDAO.getAllFaculty();
        for (Faculty f : list) {
            modelFaculty.addRow(new Object[]{
                    f.getUserId(),
                    f.getFullName(),
                    f.getUsername(),
                    f.getEmail(),
                    f.getDesignation(),
                    f.isActive() ? "Yes" : "No"
            });
        }
    }

    private void showAddFacultyDialog() {
        JTextField txtUser = new JTextField(15);
        JTextField txtName = new JTextField(15);
        JTextField txtEmail = new JTextField(15);
        String[] desigs = {"Assistant Professor", "Associate Professor", "Professor", "Lecturer"};
        JComboBox<String> cbDesig = new JComboBox<>(desigs);

        JComboBox<Department> cbDept = new JComboBox<>();
        for (Department d : departmentDAO.getAllDepartments()) {
            cbDept.addItem(d);
        }

        JPanel panel = new JPanel(new GridLayout(5, 2, 8, 8));
        panel.add(new JLabel("Username:"));
        panel.add(txtUser);
        panel.add(new JLabel("Full Name:"));
        panel.add(txtName);
        panel.add(new JLabel("Email Address:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Department:"));
        panel.add(cbDept);
        panel.add(new JLabel("Designation:"));
        panel.add(cbDesig);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Faculty", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String user = txtUser.getText().trim();
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            String desig = (String) cbDesig.getSelectedItem();
            Department dept = (Department) cbDept.getSelectedItem();

            if (user.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter all required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Faculty f = new Faculty(0, user, name, email, dept != null ? dept.getDepartmentId() : 1, true, desig);
            boolean ok = facultyDAO.addFaculty(f, "faculty123");
            if (ok) {
                JOptionPane.showMessageDialog(this, "Faculty member added successfully! (Default Password: faculty123)", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadFaculty();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add faculty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeactivateFaculty() {
        int row = tblFaculty.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a faculty member from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) modelFaculty.getValueAt(row, 0);
        String name = (String) modelFaculty.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to deactivate " + name + "?",
                "Confirm Deactivation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = facultyDAO.deleteFaculty(userId);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Faculty deactivated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadFaculty();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to deactivate faculty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
