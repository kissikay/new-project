package com.academic.gui;

import com.academic.database.DatabaseManager;
import com.academic.model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;

    public LoginFrame() {
        setTitle("IS - Information System");
        setSize(450, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BACKGROUND_COLOR);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Header
        JLabel simLabel = new JLabel("IS", JLabel.CENTER);
        simLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        simLabel.setForeground(Theme.PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(simLabel, gbc);

        JLabel subLabel = new JLabel("Information System", JLabel.CENTER);
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subLabel.setForeground(Theme.SECONDARY_TEXT);
        gbc.gridy = 1;
        add(subLabel, gbc);

        JLabel welcomeLabel = new JLabel("Welcome back! Please sign in to continue", JLabel.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        welcomeLabel.setForeground(Theme.SECONDARY_TEXT);
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 10, 30, 10);
        add(welcomeLabel, gbc);

        // Login Card
        JPanel card = Theme.createCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints cgbc = new GridBagConstraints();
        cgbc.fill = GridBagConstraints.HORIZONTAL;
        cgbc.insets = new Insets(5, 0, 5, 0);
        cgbc.weightx = 1.0;

        JLabel userLabel = new JLabel("Student Name");
        userLabel.setForeground(Theme.PRIMARY_COLOR);
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        cgbc.gridx = 0; cgbc.gridy = 0;
        card.add(userLabel, cgbc);

        userField = new JTextField(20);
        Theme.applyStyle(userField);
        cgbc.gridy = 1;
        card.add(userField, cgbc);

        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(Theme.PRIMARY_COLOR);
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        cgbc.gridy = 2;
        cgbc.insets = new Insets(15, 0, 5, 0);
        card.add(passLabel, cgbc);

        passField = new JPasswordField(20);
        Theme.applyStyle(passField);
        cgbc.gridy = 3;
        cgbc.insets = new Insets(5, 0, 5, 0);
        card.add(passField, cgbc);

        JButton loginBtn = new JButton("Sign In");
        Theme.applyStyle(loginBtn);
        cgbc.gridy = 4;
        cgbc.insets = new Insets(30, 0, 10, 0);
        card.add(loginBtn, cgbc);

        JButton registerBtn = new JButton("Don't have an account? Register");
        registerBtn.setForeground(Theme.PRIMARY_COLOR);
        registerBtn.setOpaque(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setFocusPainted(false);
        registerBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        registerBtn.setBorder(new EmptyBorder(5, 0, 5, 0));
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cgbc.gridy = 5;
        cgbc.insets = new Insets(10, 0, 0, 0);
        card.add(registerBtn, cgbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 20, 20, 20);
        add(card, gbc);

        // Footer
        JLabel footerLabel = new JLabel("© 2026 University ICT Services", JLabel.CENTER);
        footerLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        footerLabel.setForeground(Theme.SECONDARY_TEXT);
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 10, 10, 10);
        add(footerLabel, gbc);

        // Actions
        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            User user = DatabaseManager.loginUser(username, password);
            if (user != null) {
                new MenuFrame(user).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerBtn.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
    }
}
