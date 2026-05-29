package com.academic.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Theme {
    // Sea Blue Theme Colors
    public static final Color PRIMARY_COLOR = new Color(0, 105, 148); // Sea Blue
    public static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // Alice Blue
    public static final Color CARD_BACKGROUND = Color.WHITE;
    public static final Color TEXT_COLOR = new Color(33, 37, 41);
    public static final Color SECONDARY_TEXT = new Color(108, 117, 125);
    public static final Color BORDER_COLOR = new Color(222, 226, 230);
    public static final Color ACCENT_COLOR = new Color(0, 150, 136); // Teal accent

    public static void applyStyle(JButton button) {
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(new EmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void applyStyle(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 12, 10, 12)
        ));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(30, 40, 30, 40)
        ));
        return card;
    }
}
