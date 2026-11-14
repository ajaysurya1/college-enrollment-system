package com.university.ui;

import com.university.DashboardDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class DashboardPanel extends JPanel {

    private final DashboardDAO dashboardDAO;

    private JLabel lblStudents;
    private JLabel lblFaculty;
    private JLabel lblCourses;
    private JLabel lblEnrollments;
    private JLabel lblOfferings;
    private JLabel lblDepartments;
    private DefaultListModel<String> activityModel;

    public DashboardPanel() {
        this.dashboardDAO = new DashboardDAO();
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_LIGHT);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        initUI();
        refreshData();
    }

    private void initUI() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(UITheme.BG_LIGHT);

        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 16, 16));
        gridPanel.setBackground(UITheme.BG_LIGHT);
        gridPanel.setMaximumSize(new Dimension(2000, 220));

        lblStudents = new JLabel("0");
        lblFaculty = new JLabel("0");
        lblCourses = new JLabel("0");
        lblEnrollments = new JLabel("0");
        lblOfferings = new JLabel("0");
        lblDepartments = new JLabel("0");

        gridPanel.add(createStatCard("Total Students", lblStudents, new Color(41, 128, 185)));
        gridPanel.add(createStatCard("Total Faculty", lblFaculty, new Color(39, 174, 96)));
        gridPanel.add(createStatCard("Total Courses", lblCourses, new Color(142, 68, 173)));
        gridPanel.add(createStatCard("Active Enrollments", lblEnrollments, new Color(230, 126, 34)));
        gridPanel.add(createStatCard("Course Offerings", lblOfferings, new Color(22, 160, 133)));
        gridPanel.add(createStatCard("Academic Departments", lblDepartments, new Color(52, 73, 94)));

        centerPanel.add(gridPanel);
        centerPanel.add(Box.createVerticalStrut(20));

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 16, 16));
        bottomPanel.setBackground(UITheme.BG_LIGHT);

        JPanel activityCard = UITheme.createCardPanel();
        activityCard.setLayout(new BorderLayout(10, 10));

        JLabel lblActTitle = new JLabel("Recent Activity & Course Registrations");
        lblActTitle.setFont(UITheme.FONT_HEADER);
        activityCard.add(lblActTitle, BorderLayout.NORTH);

        activityModel = new DefaultListModel<>();
        JList<String> listActivity = new JList<>(activityModel);
        listActivity.setFont(UITheme.FONT_BODY);
        listActivity.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        activityCard.add(new JScrollPane(listActivity), BorderLayout.CENTER);

        bottomPanel.add(activityCard);

        JPanel quickInfoCard = UITheme.createCardPanel();
        quickInfoCard.setLayout(new BoxLayout(quickInfoCard, BoxLayout.Y_AXIS));

        JLabel lblQuickTitle = new JLabel("System Quick Status");
        lblQuickTitle.setFont(UITheme.FONT_HEADER);
        quickInfoCard.add(lblQuickTitle);
        quickInfoCard.add(Box.createVerticalStrut(14));

        quickInfoCard.add(createInfoLine("Database Engine:", "MySQL 8.0+ (InnoDB)"));
        quickInfoCard.add(createInfoLine("Relational Integrity:", "Active Foreign Keys & Checks"));
        quickInfoCard.add(createInfoLine("Concurrency Safety:", "SELECT ... FOR UPDATE Row-Lock"));
        quickInfoCard.add(createInfoLine("Password Security:", "BCrypt Strong Hashing"));
        quickInfoCard.add(createInfoLine("Current Semester:", "Spring 2027 (Registration OPEN)"));
        quickInfoCard.add(createInfoLine("Architecture:", "Object-Oriented + DAO Pattern"));

        bottomPanel.add(quickInfoCard);

        centerPanel.add(bottomPanel);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(8, 8));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_BOLD);
        lblTitle.setForeground(UITheme.TEXT_MUTED);
        header.add(lblTitle, BorderLayout.WEST);

        card.add(header, BorderLayout.NORTH);

        valueLabel.setFont(UITheme.FONT_STAT);
        valueLabel.setForeground(accentColor);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createInfoLine(String label, String value) {
        JPanel line = new JPanel(new BorderLayout());
        line.setOpaque(false);
        line.setMaximumSize(new Dimension(2000, 26));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_DARK);

        JLabel val = new JLabel(value);
        val.setFont(UITheme.FONT_BODY);
        val.setForeground(UITheme.TEXT_MUTED);

        line.add(lbl, BorderLayout.WEST);
        line.add(val, BorderLayout.EAST);
        return line;
    }

    public void refreshData() {
        Map<String, Integer> stats = dashboardDAO.getSystemStats();
        lblStudents.setText(String.valueOf(stats.get("students")));
        lblFaculty.setText(String.valueOf(stats.get("faculty")));
        lblCourses.setText(String.valueOf(stats.get("courses")));
        lblEnrollments.setText(String.valueOf(stats.get("enrollments")));
        lblOfferings.setText(String.valueOf(stats.get("offerings")));
        lblDepartments.setText(String.valueOf(stats.get("departments")));

        activityModel.clear();
        List<String> activities = dashboardDAO.getRecentActivity();
        if (activities.isEmpty()) {
            activityModel.addElement("No recent registrations recorded.");
        } else {
            for (String a : activities) {
                activityModel.addElement(a);
            }
        }
    }
}
