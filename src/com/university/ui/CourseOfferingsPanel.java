package com.university.ui;

import com.university.Course;
import com.university.CourseDAO;
import com.university.CourseOffering;
import com.university.CourseOfferingDAO;
import com.university.Faculty;
import com.university.FacultyDAO;
import com.university.Semester;
import com.university.SemesterDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CourseOfferingsPanel extends JPanel {

    private final CourseOfferingDAO offeringDAO;
    private final CourseDAO courseDAO;
    private final SemesterDAO semesterDAO;
    private final FacultyDAO facultyDAO;

    private JTable tblOfferings;
    private DefaultTableModel modelOfferings;

    public CourseOfferingsPanel() {
        this.offeringDAO = new CourseOfferingDAO();
        this.courseDAO = new CourseDAO();
        this.semesterDAO = new SemesterDAO();
        this.facultyDAO = new FacultyDAO();

        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BG_LIGHT);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
        loadOfferings();
    }

    private void initUI() {
        JPanel topBar = UITheme.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));

        JButton btnAdd = UITheme.createSuccessButton("+ Add Course Offering");
        btnAdd.addActionListener(e -> showAddOfferingDialog());
        topBar.add(btnAdd);

        JButton btnDelete = UITheme.createDangerButton("Delete Offering");
        btnDelete.addActionListener(e -> handleDeleteOffering());
        topBar.add(btnDelete);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadOfferings());
        topBar.add(btnRefresh);

        add(topBar, BorderLayout.NORTH);

        String[] cols = {"Offering ID", "Course Code", "Course Title", "Credits", "Semester", "Section", "Capacity", "Enrolled", "Seats Left", "Faculty"};
        modelOfferings = new DefaultTableModel(cols, 0) {
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

        add(new JScrollPane(tblOfferings), BorderLayout.CENTER);
    }

    public void loadOfferings() {
        modelOfferings.setRowCount(0);
        List<CourseOffering> list = offeringDAO.getAllOfferings();
        for (CourseOffering o : list) {
            int seatsLeft = Math.max(0, o.getCapacity() - o.getEnrolledCount());
            modelOfferings.addRow(new Object[]{
                    o.getOfferingId(),
                    o.getCourseCode(),
                    o.getCourseTitle(),
                    o.getCredits(),
                    o.getSemesterName(),
                    o.getSection(),
                    o.getCapacity(),
                    o.getEnrolledCount(),
                    seatsLeft,
                    o.getFacultyName() != null ? o.getFacultyName() : "Unassigned"
            });
        }
    }

    private void showAddOfferingDialog() {
        JComboBox<Course> cbCourse = new JComboBox<>();
        for (Course c : courseDAO.getAllCourses()) cbCourse.addItem(c);

        JComboBox<Semester> cbSem = new JComboBox<>();
        for (Semester s : semesterDAO.getAllSemesters()) cbSem.addItem(s);

        JComboBox<Faculty> cbFac = new JComboBox<>();
        for (Faculty f : facultyDAO.getAllFaculty()) cbFac.addItem(f);

        JTextField txtSection = new JTextField("A", 5);
        JTextField txtCapacity = new JTextField("30", 5);

        JPanel panel = new JPanel(new GridLayout(5, 2, 8, 8));
        panel.add(new JLabel("Course:"));
        panel.add(cbCourse);
        panel.add(new JLabel("Semester:"));
        panel.add(cbSem);
        panel.add(new JLabel("Faculty In-Charge:"));
        panel.add(cbFac);
        panel.add(new JLabel("Section (e.g. A, B):"));
        panel.add(txtSection);
        panel.add(new JLabel("Max Student Capacity:"));
        panel.add(txtCapacity);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Course Offering", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            Course c = (Course) cbCourse.getSelectedItem();
            Semester s = (Semester) cbSem.getSelectedItem();
            Faculty f = (Faculty) cbFac.getSelectedItem();
            String sec = txtSection.getText().trim();
            int cap = 30;
            try {
                cap = Integer.parseInt(txtCapacity.getText().trim());
            } catch (Exception ex) {
            }

            if (c == null || s == null || sec.isEmpty() || cap <= 0) {
                JOptionPane.showMessageDialog(this, "Please check all fields and enter valid capacity.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            CourseOffering co = new CourseOffering(0, c.getCourseId(), s.getSemesterId(), f != null ? f.getUserId() : null, sec, cap);
            boolean ok = offeringDAO.addOffering(co);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Course offering added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadOfferings();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add offering. Same course, semester and section may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeleteOffering() {
        int row = tblOfferings.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an offering to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) modelOfferings.getValueAt(row, 0);
        String course = (String) modelOfferings.getValueAt(row, 1);
        String sec = (String) modelOfferings.getValueAt(row, 5);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete offering " + course + " Section " + sec + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = offeringDAO.deleteOffering(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Offering deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadOfferings();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete offering. Students are enrolled.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
