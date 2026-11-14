package com.university.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HeaderPanel extends JPanel {

    private final JLabel titleLabel;
    private final JLabel subtitleLabel;
    private final JLabel clockLabel;
    private final JButton refreshButton;

    public HeaderPanel(Runnable onRefresh) {
        setLayout(new BorderLayout());
        setBackground(UITheme.NAVY);
        setPreferredSize(new Dimension(1000, 72));
        setBorder(new EmptyBorder(12, 24, 12, 24));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        titleLabel = new JLabel("University Dashboard");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        textPanel.add(titleLabel);

        subtitleLabel = new JLabel("Academic & Course Enrollment Management Portal");
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(new Color(210, 225, 245));
        textPanel.add(subtitleLabel);

        add(textPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 6));
        rightPanel.setOpaque(false);

        clockLabel = new JLabel();
        clockLabel.setFont(UITheme.FONT_BOLD);
        clockLabel.setForeground(Color.WHITE);
        updateClock();
        rightPanel.add(clockLabel);

        Timer timer = new Timer(1000, e -> updateClock());
        timer.start();

        refreshButton = new JButton("Refresh Data");
        refreshButton.setFont(UITheme.FONT_BOLD);
        refreshButton.setBackground(UITheme.NAVY_LIGHT);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> {
            if (onRefresh != null) {
                onRefresh.run();
            }
        });
        rightPanel.add(refreshButton);

        add(rightPanel, BorderLayout.EAST);
    }

    private void updateClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy - hh:mm:ss a");
        clockLabel.setText(sdf.format(new Date()));
    }

    public void setPageTitle(String title, String subtitle) {
        titleLabel.setText(title);
        subtitleLabel.setText(subtitle);
    }
}
