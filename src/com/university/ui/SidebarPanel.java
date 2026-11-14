package com.university.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SidebarPanel extends JPanel {

    private final Consumer<String> onNavigate;
    private final List<JButton> navButtons = new ArrayList<>();
    private String activePage = "DASHBOARD";

    public SidebarPanel(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        setLayout(new BorderLayout());
        setBackground(UITheme.NAVY_DARK);
        setPreferredSize(new Dimension(230, 800));

        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(UITheme.NAVY_DARK);
        brandPanel.setBorder(new EmptyBorder(22, 18, 22, 18));

        JLabel lblLogo = new JLabel("UniPortal");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Academic System v1.0");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(new Color(180, 200, 230));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        brandPanel.add(lblLogo);
        brandPanel.add(Box.createVerticalStrut(4));
        brandPanel.add(lblSub);

        add(brandPanel, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(UITheme.NAVY_DARK);
        menuPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        addNavButton(menuPanel, "Dashboard", "DASHBOARD");
        addNavButton(menuPanel, "Students", "STUDENTS");
        addNavButton(menuPanel, "Faculty", "FACULTY");
        addNavButton(menuPanel, "Courses", "COURSES");
        addNavButton(menuPanel, "Offerings", "OFFERINGS");
        addNavButton(menuPanel, "Enrollments", "ENROLLMENTS");
        addNavButton(menuPanel, "Student Portal", "STUDENT_PORTAL");
        addNavButton(menuPanel, "Faculty Portal", "FACULTY_PORTAL");
        addNavButton(menuPanel, "About Project", "ABOUT");

        add(new JScrollPane(menuPanel), BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(UITheme.NAVY_DARK);
        footerPanel.setBorder(new EmptyBorder(12, 10, 14, 10));

        JLabel lblFoot = new JLabel("End-Sem OOP Project");
        lblFoot.setFont(UITheme.FONT_SMALL);
        lblFoot.setForeground(new Color(150, 175, 210));
        footerPanel.add(lblFoot);

        add(footerPanel, BorderLayout.SOUTH);

        setActiveButton("DASHBOARD");
    }

    private void addNavButton(JPanel container, String text, String pageId) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BOLD);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(206, 42));
        btn.setPreferredSize(new Dimension(206, 42));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            setActiveButton(pageId);
            if (onNavigate != null) {
                onNavigate.accept(pageId);
            }
        });

        navButtons.add(btn);
        container.add(btn);
        container.add(Box.createVerticalStrut(6));
    }

    public void setActiveButton(String pageId) {
        this.activePage = pageId;
        String[] pageIds = {"DASHBOARD", "STUDENTS", "FACULTY", "COURSES", "OFFERINGS", "ENROLLMENTS", "STUDENT_PORTAL", "FACULTY_PORTAL", "ABOUT"};

        for (int i = 0; i < navButtons.size(); i++) {
            JButton btn = navButtons.get(i);
            if (i < pageIds.length && pageIds[i].equals(pageId)) {
                btn.setBackground(UITheme.NAVY_LIGHT);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(UITheme.NAVY_DARK);
                btn.setForeground(new Color(210, 225, 245));
            }
        }
    }
}
