package com.academic.gui;

import com.academic.database.DatabaseManager;
import com.academic.model.User;
import com.academic.model.Mark;
import com.academic.service.AIService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ChatDialog extends JDialog {
    private JTextArea chatArea;

    public ChatDialog(JFrame parent, User user) {
        super(parent, "Academic Advisor", true);
        setSize(620, 560);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Theme.BACKGROUND_COLOR);
        setLayout(new BorderLayout(0, 0));

        // ── Header ────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, Theme.PRIMARY_COLOR, getWidth(), 0, new Color(0, 130, 180)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setBorder(new EmptyBorder(16, 22, 16, 22));

        JLabel icon = new JLabel("📋");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 26));
        header.add(icon, BorderLayout.WEST);

        JPanel titleStack = new JPanel();
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.setOpaque(false);
        titleStack.setBorder(new EmptyBorder(0, 12, 0, 0));
        JLabel title = new JLabel("Academic Advisor");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Personalised report based on your semester results");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sub.setForeground(new Color(200, 230, 255));
        titleStack.add(title);
        titleStack.add(Box.createVerticalStrut(3));
        titleStack.add(sub);
        header.add(titleStack, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ── Report area ───────────────────────────────────────────────
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        chatArea.setForeground(Theme.TEXT_COLOR);
        chatArea.setBackground(Color.WHITE);
        chatArea.setBorder(new EmptyBorder(18, 20, 18, 20));
        chatArea.setText("Generating your advisory report, please wait...");

        JScrollPane scroll = new JScrollPane(chatArea);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Theme.BORDER_COLOR));
        add(scroll, BorderLayout.CENTER);

        // ── Footer ────────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
        footer.setBackground(new Color(246, 249, 252));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER_COLOR));

        JButton closeBtn = new JButton("Close");
        Theme.applyStyle(closeBtn);
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);

        // ── Generate report ───────────────────────────────────────────
        List<Mark> marks = DatabaseManager.getUserMarks(user.getId());

        if (marks.isEmpty()) {
            chatArea.setText("No marks found.\nPlease submit your semester results first.");
        } else {
            AIService.getGuidance(user.getUsername(), marks).thenAccept(report ->
                SwingUtilities.invokeLater(() -> {
                    chatArea.setText(report);
                    chatArea.setCaretPosition(0);
                })
            );
        }
    }
}
