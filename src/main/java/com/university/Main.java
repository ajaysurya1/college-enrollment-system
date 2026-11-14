package com.university;

import com.university.ui.auth.LoginFrame;
import com.university.ui.components.UITheme;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        UITheme.setupFlatLaf();

        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
