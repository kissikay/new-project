package com.academic.gui;

import com.academic.database.DatabaseManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private JTextField userField, indexField, programmeField, courseCountField;
    private JPasswordField passField;
    private JComboBox<Integer> levelBox;
    private JComboBox<Integer> semesterBox;

    public RegisterFrame() {
        setTitle("IS - Student Registration");
        setSize(500, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BACKGROUND_COLOR);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel simLabel = new JLabel("IS", JLabel.CENTER);
        simLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        simLabel.setForeground(Theme.PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(simLabel, gbc);

        JLabel subLabel = new JLabel("Student Registration", JLabel.CENTER);
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subLabel.setForeground(Theme.SECONDARY_TEXT);
        gbc.gridy = 1;
        add(subLabel, gbc);

        JPanel card = Theme.createCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints cgbc = new GridBagConstraints();
        cgbc.fill = GridBagConstraints.HORIZONTAL;
        cgbc.insets = new Insets(5, 0, 5, 0);
        cgbc.weightx = 1.0;

        // Full Name
        addLabel(card, cgbc, "Full Name", 0);
        userField = new JTextField(20);
        Theme.applyStyle(userField);
        cgbc.gridy = 1; cgbc.insets = new Insets(5, 0, 5, 0);
        card.add(userField, cgbc);

        // Index Number
        addLabel(card, cgbc, "Index Number", 2);
        indexField = new JTextField(20);
        Theme.applyStyle(indexField);
        cgbc.gridy = 3;
        card.add(indexField, cgbc);

        // Programme
        addLabel(card, cgbc, "Programme", 4);
        programmeField = new JTextField(20);
        Theme.applyStyle(programmeField);
        cgbc.gridy = 5;
        card.add(programmeField, cgbc);

        // Level and Semester
        JPanel comboPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        comboPanel.setBackground(Theme.CARD_BACKGROUND);

        JPanel levelPanel = new JPanel(new BorderLayout(0, 5));
        levelPanel.setBackground(Theme.CARD_BACKGROUND);
        JLabel levelLabel = new JLabel("Level");
        levelLabel.setForeground(Theme.PRIMARY_COLOR);
        levelLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        levelPanel.add(levelLabel, BorderLayout.NORTH);
        levelBox = new JComboBox<>(new Integer[]{100, 200, 300, 400});
        levelPanel.add(levelBox, BorderLayout.CENTER);
        comboPanel.add(levelPanel);

        JPanel semPanel = new JPanel(new BorderLayout(0, 5));
        semPanel.setBackground(Theme.CARD_BACKGROUND);
        JLabel semLabel = new JLabel("Semester");
        semLabel.setForeground(Theme.PRIMARY_COLOR);
        semLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        semPanel.add(semLabel, BorderLayout.NORTH);
        semesterBox = new JComboBox<>(new Integer[]{1, 2});
        semPanel.add(semesterBox, BorderLayout.CENTER);
        comboPanel.add(semPanel);

        cgbc.gridy = 6; cgbc.insets = new Insets(15, 0, 5, 0);
        card.add(comboPanel, cgbc);

        // Number of Courses
        addLabel(card, cgbc, "Number of Courses Registered", 7);
        courseCountField = new JTextField(20);
        Theme.applyStyle(courseCountField);
        cgbc.gridy = 8; cgbc.insets = new Insets(5, 0, 5, 0);
        card.add(courseCountField, cgbc);

        // Password
        addLabel(card, cgbc, "Password", 9);
        passField = new JPasswordField(20);
        Theme.applyStyle(passField);
        cgbc.gridy = 10;
        card.add(passField, cgbc);

        JButton registerBtn = new JButton("Register");
        Theme.applyStyle(registerBtn);
        cgbc.gridy = 11; cgbc.insets = new Insets(30, 0, 10, 0);
        card.add(registerBtn, cgbc);

        JButton backBtn = new JButton("Back to Login");
        backBtn.setForeground(Theme.PRIMARY_COLOR);
        backBtn.setOpaque(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        backBtn.setBorder(new EmptyBorder(5, 0, 5, 0));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cgbc.gridy = 12; cgbc.insets = new Insets(10, 0, 0, 0);
        card.add(backBtn, cgbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 20, 20, 20);
        add(card, gbc);

        registerBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            String index = indexField.getText().trim();
            String programme = programmeField.getText().trim();
            String countStr = courseCountField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || index.isEmpty() || programme.isEmpty() || countStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int count = Integer.parseInt(countStr);
                int level = (Integer) levelBox.getSelectedItem();
                int semester = (Integer) semesterBox.getSelectedItem();
                if (DatabaseManager.registerUser(username, password, index, programme, level, semester, count)) {
                    JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    new LoginFrame().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid course count!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });
    }

    private void addLabel(JPanel card, GridBagConstraints cgbc, String text, int row) {
        JLabel label = new JLabel(text);
        label.setForeground(Theme.PRIMARY_COLOR);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        cgbc.gridy = row;
        cgbc.insets = new Insets(15, 0, 5, 0);
        card.add(label, cgbc);
    }
}
