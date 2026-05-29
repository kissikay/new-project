package com.academic.gui;

import com.academic.model.User;
import com.academic.service.PDFService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MenuFrame extends JFrame {
    private User currentUser;

    public MenuFrame(User user) {
        this.currentUser = user;
        setTitle("IS - Student Menu");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BACKGROUND_COLOR);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.weightx = 1.0;

        // Header
        JLabel isLabel = new JLabel("IS", JLabel.CENTER);
        isLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        isLabel.setForeground(Theme.PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0;
        add(isLabel, gbc);

        JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername(), JLabel.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        welcomeLabel.setForeground(Theme.TEXT_COLOR);
        gbc.gridy = 1;
        add(welcomeLabel, gbc);

        JLabel programmeLabel = new JLabel(user.getProgramme(), JLabel.CENTER);
        programmeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        programmeLabel.setForeground(Theme.SECONDARY_TEXT);
        gbc.gridy = 2;
        gbc.insets = new Insets(2, 20, 10, 20);
        add(programmeLabel, gbc);

        // Menu Card
        JPanel card = Theme.createCard();
        card.setLayout(new GridLayout(2, 1, 0, 30));
        
        // Check Result Button
        JButton checkBtn = createMenuButton("Check Result", "✓");
        checkBtn.addActionListener(e -> {
            new DashboardFrame(currentUser).setVisible(true);
            dispose();
        });
        card.add(checkBtn);

        // Download Result Button
        JButton downloadBtn = createMenuButton("Download Result (PDF)", "⬇");
        downloadBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Result as PDF");
            fileChooser.setSelectedFile(new java.io.File("Semester_Result_" + currentUser.getUsername() + ".pdf"));
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                if (!path.toLowerCase().endsWith(".pdf")) path += ".pdf";
                
                if (PDFService.generateResultPDF(currentUser, path)) {
                    JOptionPane.showMessageDialog(this, "PDF generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to generate PDF. Please ensure you have marks entered.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        card.add(downloadBtn);

        gbc.gridy = 3;
        gbc.insets = new Insets(30, 40, 30, 40);
        add(card, gbc);

        // Logout
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setForeground(Theme.PRIMARY_COLOR);
        logoutBtn.setOpaque(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        logoutBtn.setBorder(null);
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        gbc.gridy = 4;
        add(logoutBtn, gbc);
    }

    private JButton createMenuButton(String text, String icon) {
        JButton btn = new JButton("<html><center><font size='6'>" + icon + "</font><br><br><font size='5'>" + text + "</font></center></html>");
        btn.setBackground(Color.WHITE);
        btn.setForeground(Theme.PRIMARY_COLOR);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(240, 248, 255));
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.PRIMARY_COLOR, 1, true),
                    new EmptyBorder(20, 20, 20, 20)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true),
                    new EmptyBorder(20, 20, 20, 20)
                ));
            }
        });

        return btn;
    }
}
