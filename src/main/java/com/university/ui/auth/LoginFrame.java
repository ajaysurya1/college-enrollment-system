package com.university.ui.auth;

import com.university.exception.AuthenticationException;
import com.university.model.Administrator;
import com.university.model.Faculty;
import com.university.model.Person;
import com.university.model.Student;
import com.university.service.AuthService;
import com.university.ui.admin.AdminDashboard;
import com.university.ui.components.UITheme;
import com.university.ui.faculty.FacultyDashboard;
import com.university.ui.student.StudentDashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {

    private final AuthService authService;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblError;
    private JButton btnLogin;

    public LoginFrame() {
        this.authService = new AuthService();
        initUI();
    }

    private void initUI() {
        setTitle("University Academic System - Login");
        setSize(480, 520);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UITheme.NAVY);
        headerPanel.setBorder(new EmptyBorder(25, 20, 25, 20));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("University Academic Portal");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Sign in to your academic account");
        lblSubtitle.setFont(UITheme.FONT_BODY);
        lblSubtitle.setForeground(new Color(220, 230, 242));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblTitle);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(lblSubtitle);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        lblError = new JLabel(" ");
        lblError.setForeground(UITheme.DANGER_RED);
        lblError.setFont(UITheme.FONT_BODY);
        gbc.gridy = 0;
        formCard.add(lblError, gbc);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(UITheme.FONT_BOLD);
        gbc.gridy = 1;
        formCard.add(lblUser, gbc);

        txtUsername = new JTextField(20);
        txtUsername.setFont(UITheme.FONT_BODY);
        txtUsername.setPreferredSize(new Dimension(280, 34));
        gbc.gridy = 2;
        formCard.add(txtUsername, gbc);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(UITheme.FONT_BOLD);
        gbc.gridy = 3;
        formCard.add(lblPass, gbc);

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(UITheme.FONT_BODY);
        txtPassword.setPreferredSize(new Dimension(280, 34));
        gbc.gridy = 4;
        formCard.add(txtPassword, gbc);

        btnLogin = UITheme.createPrimaryButton("Sign In");
        btnLogin.setPreferredSize(new Dimension(280, 38));
        gbc.gridy = 5;
        gbc.insets = new Insets(16, 12, 8, 12);
        formCard.add(btnLogin, gbc);

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.setBackground(UITheme.BG_LIGHT);
        centerWrapper.setBorder(new EmptyBorder(20, 20, 10, 20));
        centerWrapper.add(formCard);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        JPanel footer = new JPanel(new GridLayout(2, 1));
        footer.setBackground(UITheme.BG_LIGHT);
        footer.setBorder(new EmptyBorder(5, 15, 15, 15));

        JLabel lblDemo1 = new JLabel("Demo Logins: Admin: admin / admin123 | Faculty: prof.smith / faculty123", SwingConstants.CENTER);
        lblDemo1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDemo1.setForeground(UITheme.TEXT_MUTED);

        JLabel lblDemo2 = new JLabel("Student: student.alice / student123 | student.evan / student123", SwingConstants.CENTER);
        lblDemo2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDemo2.setForeground(UITheme.TEXT_MUTED);

        footer.add(lblDemo1);
        footer.add(lblDemo2);

        mainPanel.add(footer, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> performLogin());

        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtPassword.requestFocusInWindow();
                }
            }
        });

        setContentPane(mainPanel);
    }

    private void performLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        lblError.setText(" ");
        btnLogin.setEnabled(false);

        SwingWorker<Person, Void> worker = new SwingWorker<>() {
            @Override
            protected Person doInBackground() throws Exception {
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                btnLogin.setEnabled(true);
                try {
                    Person currentUser = get();
                    openDashboard(currentUser);
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    if (cause instanceof AuthenticationException) {
                        lblError.setText(cause.getMessage());
                    } else {
                        lblError.setText("Database connection error. Is MySQL running?");
                    }
                }
            }
        };
        worker.execute();
    }

    private void openDashboard(Person person) {
        dispose();
        if (person instanceof Administrator) {
            new AdminDashboard((Administrator) person).setVisible(true);
        } else if (person instanceof Student) {
            new StudentDashboard((Student) person).setVisible(true);
        } else if (person instanceof Faculty) {
            new FacultyDashboard((Faculty) person).setVisible(true);
        }
    }
}
