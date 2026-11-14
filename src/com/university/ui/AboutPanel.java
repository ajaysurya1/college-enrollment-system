package com.university.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AboutPanel extends JPanel {

    public AboutPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_LIGHT);

        initUI();
    }

    private void initUI() {
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(UITheme.BG_LIGHT);
        scrollContent.setBorder(new EmptyBorder(24, 28, 28, 28));

        JPanel heroCard = UITheme.createCardPanel();
        heroCard.setLayout(new BoxLayout(heroCard, BoxLayout.Y_AXIS));
        heroCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        heroCard.setMaximumSize(new Dimension(2000, 150));

        JLabel lblTitle = new JLabel("University Academic & Course Enrollment Management System");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.NAVY);
        heroCard.add(lblTitle);
        heroCard.add(Box.createVerticalStrut(8));

        JLabel lblDesc = new JLabel("<html>A complete, robust Java Swing desktop application and interactive terminal CLI connected to MySQL.<br>Built to manage students, faculty, course catalogs, semester offerings, prerequisites, and transactional student enrollment.</html>");
        lblDesc.setFont(UITheme.FONT_BODY);
        lblDesc.setForeground(UITheme.TEXT_DARK);
        heroCard.add(lblDesc);

        scrollContent.add(heroCard);
        scrollContent.add(Box.createVerticalStrut(20));

        JPanel oopCard = UITheme.createCardPanel();
        oopCard.setLayout(new BoxLayout(oopCard, BoxLayout.Y_AXIS));
        oopCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        oopCard.setMaximumSize(new Dimension(2000, 300));

        JLabel lblOopHeader = new JLabel("Object-Oriented Programming (OOP) Implementation");
        lblOopHeader.setFont(UITheme.FONT_HEADER);
        lblOopHeader.setForeground(UITheme.NAVY);
        oopCard.add(lblOopHeader);
        oopCard.add(Box.createVerticalStrut(12));

        oopCard.add(createFeatureItem("[1] Encapsulation:", "All entity fields (Student, Faculty, Course, etc.) are private with validated getters and setters."));
        oopCard.add(createFeatureItem("[2] Inheritance:", "Abstract base class Person is extended by Student, Faculty, and Administrator classes."));
        oopCard.add(createFeatureItem("[3] Polymorphism:", "Abstract methods getRoleName() and getDashboardMessage() are overridden dynamically across personas."));
        oopCard.add(createFeatureItem("[4] Abstraction & DAO Pattern:", "Clean Data Access Objects isolate all database queries and business transactions from the Swing UI."));
        oopCard.add(createFeatureItem("[5] ACID Transactions & Concurrency:", "Row-level locking via SELECT ... FOR UPDATE ensures zero race conditions during peak seat registration."));

        scrollContent.add(oopCard);
        scrollContent.add(Box.createVerticalStrut(20));

        JPanel techCard = UITheme.createCardPanel();
        techCard.setLayout(new BoxLayout(techCard, BoxLayout.Y_AXIS));
        techCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        techCard.setMaximumSize(new Dimension(2000, 200));

        JLabel lblTechHeader = new JLabel("Technology Stack & Architecture");
        lblTechHeader.setFont(UITheme.FONT_HEADER);
        lblTechHeader.setForeground(UITheme.NAVY);
        techCard.add(lblTechHeader);
        techCard.add(Box.createVerticalStrut(12));

        techCard.add(createFeatureItem("- Java SE 17+ / 21+:", "Core OOP language implementation, Swing & AWT for GUI, JDBC for data persistence."));
        techCard.add(createFeatureItem("- MySQL 8.0+ Relational DB:", "9 tables with foreign keys, checks, unique constraints, and indexes."));
        techCard.add(createFeatureItem("- FlatLaf & Modern Swing:", "Clean modern typography, responsive cards, and dynamic SplitPanes."));
        techCard.add(createFeatureItem("- BCrypt Security:", "Secure password hashing for all user authentication accounts."));

        scrollContent.add(techCard);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createFeatureItem(String title, String desc) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(2000, 32));

        JLabel lblT = new JLabel(title);
        lblT.setFont(UITheme.FONT_BOLD);
        lblT.setForeground(UITheme.TEXT_DARK);

        JLabel lblD = new JLabel(desc);
        lblD.setFont(UITheme.FONT_BODY);
        lblD.setForeground(UITheme.TEXT_MUTED);

        p.add(lblT);
        p.add(lblD);
        return p;
    }
}
