package com.academic.gui;

import com.academic.database.DatabaseManager;
import com.academic.model.User;
import com.academic.model.Mark;
import com.academic.service.GradeCalculator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class DashboardFrame extends JFrame {
    private final User currentUser;
    private DefaultTableModel tableModel;
    private final List<JTextField> codeFields   = new ArrayList<>();
    private final List<JTextField> nameFields   = new ArrayList<>();
    private final List<JTextField> creditFields = new ArrayList<>();
    private final List<JTextField> scoreFields  = new ArrayList<>();

    // Stat labels — updated whenever refreshTable() is called
    private JLabel cwaValue, avgValue, passValue, bestValue, weakValue, creditsValue;
    private JLabel classificationLabel;
    private JPanel classificationCard;

    public DashboardFrame(User user) {
        this.currentUser = user;
        setTitle("IS - Academic Results");
        setSize(1250, 900);
        setMinimumSize(new Dimension(1000, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BACKGROUND_COLOR);
        setLayout(new BorderLayout(0, 0));

        add(buildTopBar(user), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setBorder(null);
        split.setDividerSize(1);
        split.setDividerLocation(620);
        split.setBackground(Theme.BACKGROUND_COLOR);
        split.setLeftComponent(buildResultsPanel(user));
        split.setRightComponent(buildEntryPanel(user));
        add(split, BorderLayout.CENTER);

        refreshTable();
    }

    // ── Top bar ──────────────────────────────────────────────────────
    private JPanel buildTopBar(User user) {
        JPanel topBar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, Theme.PRIMARY_COLOR,
                        getWidth(), 0, new Color(0, 130, 180));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topBar.setBorder(new EmptyBorder(14, 28, 14, 28));

        JLabel appLabel = new JLabel("IS  ·  Academic Portal");
        appLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        appLabel.setForeground(Color.WHITE);
        topBar.add(appLabel, BorderLayout.WEST);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        topRight.setOpaque(false);

        JLabel userLbl = new JLabel("👤  " + user.getUsername());
        userLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        userLbl.setForeground(new Color(220, 240, 255));
        topRight.add(userLbl);

        topRight.add(makeTopBtn("⬅  Menu",  al -> { new MenuFrame(user).setVisible(true);  dispose(); }));
        topRight.add(makeTopBtn("Logout",   al -> { new LoginFrame().setVisible(true);      dispose(); }));
        topBar.add(topRight, BorderLayout.EAST);
        return topBar;
    }

    // ── Left: Results panel ───────────────────────────────────────────
    private JPanel buildResultsPanel(User user) {
        JPanel outer = new JPanel(new BorderLayout(0, 14));
        outer.setBackground(Theme.BACKGROUND_COLOR);
        outer.setBorder(new EmptyBorder(22, 22, 22, 12));

        outer.add(buildResultsHeader(), BorderLayout.NORTH);

        JPanel centerStack = new JPanel(new BorderLayout(0, 12));
        centerStack.setOpaque(false);
        centerStack.add(buildInfoCard(user), BorderLayout.NORTH);

        JPanel statsSection = new JPanel(new BorderLayout(0, 10));
        statsSection.setOpaque(false);
        statsSection.add(buildStatsRow(), BorderLayout.NORTH);
        classificationCard = buildClassificationCard();
        statsSection.add(classificationCard, BorderLayout.CENTER);
        centerStack.add(statsSection, BorderLayout.CENTER);

        centerStack.add(buildTableCard(), BorderLayout.SOUTH);
        outer.add(centerStack, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildResultsHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleStack = new JPanel();
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.setOpaque(false);
        JLabel title = new JLabel("Academic Results");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Theme.TEXT_COLOR);
        JLabel sub = new JLabel("Your semester performance at a glance");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(Theme.SECONDARY_TEXT);
        titleStack.add(title);
        titleStack.add(Box.createVerticalStrut(3));
        titleStack.add(sub);
        header.add(titleStack, BorderLayout.WEST);

        JButton refreshBtn = makeIconBtn("⟳");
        refreshBtn.setToolTipText("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());
        header.add(refreshBtn, BorderLayout.EAST);
        return header;
    }

    private JPanel buildInfoCard(User user) {
        JPanel card = new RoundedPanel(14, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 6));
        grid.setOpaque(false);
        grid.add(infoChip("Name",             user.getUsername().toUpperCase()));
        grid.add(infoChip("Index Number",     user.getIndexNumber()));
        grid.add(infoChip("Programme",        user.getProgramme()));
        grid.add(infoChip("Level / Semester", "Level " + user.getLevel() + "  ·  Sem " + user.getSemester()));
        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private JPanel infoChip(String label, String value) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(new Font("SansSerif", Font.BOLD, 9));
        lbl.setForeground(Theme.SECONDARY_TEXT);
        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.BOLD, 13));
        val.setForeground(Theme.TEXT_COLOR);
        p.add(lbl, BorderLayout.NORTH);
        p.add(val, BorderLayout.CENTER);
        return p;
    }

    // ── Stats row ─────────────────────────────────────────────────────
    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 6, 8, 0));
        row.setOpaque(false);

        creditsValue = new JLabel("—");
        cwaValue     = new JLabel("—");
        avgValue     = new JLabel("—");
        passValue    = new JLabel("—");
        bestValue    = new JLabel("—");
        weakValue    = new JLabel("—");

        row.add(miniStatCard("Total Credits", creditsValue, new Color(0, 105, 148),  new Color(225, 242, 254)));
        row.add(miniStatCard("CWA",           cwaValue,     new Color(30, 130, 76),   new Color(230, 250, 238)));
        row.add(miniStatCard("Avg Score",     avgValue,     new Color(123, 31, 162),  new Color(243, 229, 245)));
        row.add(miniStatCard("Pass Rate",     passValue,    new Color(21, 101, 192),  new Color(227, 242, 253)));
        row.add(miniStatCard("Best Course",   bestValue,    new Color(230, 81, 0),    new Color(255, 243, 224)));
        row.add(miniStatCard("Weakest",       weakValue,    new Color(183, 28, 28),   new Color(255, 235, 238)));

        return row;
    }

    private JPanel miniStatCard(String title, JLabel valueLabel, Color accent, Color bg) {
        JPanel card = new RoundedPanel(10, bg);
        card.setLayout(new BorderLayout(0, 4));
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel stripe = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(accent);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        stripe.setPreferredSize(new Dimension(0, 3));
        stripe.setOpaque(false);

        JLabel titleLbl = new JLabel(title.toUpperCase());
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 9));
        titleLbl.setForeground(accent);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        valueLabel.setForeground(accent);

        JPanel inner = new JPanel(new BorderLayout(0, 3));
        inner.setOpaque(false);
        inner.add(stripe,     BorderLayout.NORTH);
        inner.add(titleLbl,   BorderLayout.CENTER);
        inner.add(valueLabel, BorderLayout.SOUTH);

        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    // ── Classification banner ─────────────────────────────────────────
    private JPanel buildClassificationCard() {
        JPanel card = new RoundedPanel(12, new Color(245, 250, 255));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(14, 20, 14, 20));

        classificationLabel = new JLabel("Submit your marks to see your classification.", JLabel.CENTER);
        classificationLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
        classificationLabel.setForeground(Theme.SECONDARY_TEXT);
        card.add(classificationLabel, BorderLayout.CENTER);
        return card;
    }

    private void updateClassification(double cwa, String name) {
        String text;
        Color  bg, fg, border;

        if (cwa >= 80) {
            text = "🏆  " + name + ", you are a First Class student!";
            bg = new Color(230, 255, 240); fg = new Color(10, 120, 60);   border = new Color(10, 180, 80);
        } else if (cwa >= 70) {
            text = "🎖  " + name + ", you are a Second Class Upper student!";
            bg = new Color(230, 245, 255); fg = new Color(20, 90, 180);   border = new Color(30, 120, 220);
        } else if (cwa >= 60) {
            text = "📘  " + name + ", you are a Second Class Lower student.";
            bg = new Color(255, 248, 225); fg = new Color(160, 100, 0);   border = new Color(220, 160, 0);
        } else if (cwa >= 50) {
            text = "📄  " + name + ", you have passed.";
            bg = new Color(245, 245, 245); fg = new Color(80, 80, 80);    border = new Color(180, 180, 180);
        } else {
            text = "⚠️  " + name + ", your standard is too low to earn a certificate.";
            bg = new Color(255, 235, 235); fg = new Color(180, 30, 30);   border = new Color(220, 60, 60);
        }

        classificationCard.setBackground(bg);
        classificationCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(border, 1, true),
            new EmptyBorder(14, 20, 14, 20)));
        classificationLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        classificationLabel.setForeground(fg);
        classificationLabel.setText(text);
        classificationCard.repaint();
    }

    // ── Results table ─────────────────────────────────────────────────
    private JPanel buildTableCard() {
        JPanel tableCard = new RoundedPanel(12, Color.WHITE);
        tableCard.setLayout(new BorderLayout());
        tableCard.setPreferredSize(new Dimension(0, 280));

        String[] cols = {"Course Code", "Course Name", "Credits", "Grade", "Score", "Remark"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = buildStyledTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        tableCard.add(table.getTableHeader(), BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);
        return tableCard;
    }

    private JTable buildStyledTable() {
        JTable table = new JTable(tableModel) {
            @Override
            public TableCellRenderer getCellRenderer(int row, int col) {
                if (col == 3) return new GradeBadgeRenderer();
                return super.getCellRenderer(row, col);
            }
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row) && col != 3)
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 251, 255));
                return c;
            }
        };

        table.setRowHeight(42);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(235, 238, 243));
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(213, 232, 255));
        table.setSelectionForeground(Theme.TEXT_COLOR);

        table.getTableHeader().setBackground(new Color(245, 248, 252));
        table.getTableHeader().setForeground(Theme.TEXT_COLOR);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.BORDER_COLOR));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));

        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(170);
        table.getColumnModel().getColumn(2).setPreferredWidth(55);
        table.getColumnModel().getColumn(3).setPreferredWidth(65);
        table.getColumnModel().getColumn(4).setPreferredWidth(60);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);

        return table;
    }

    // ── Right: Entry form panel ───────────────────────────────────────
    private JPanel buildEntryPanel(User user) {
        JPanel outer = new JPanel(new BorderLayout(0, 16));
        outer.setBackground(Theme.BACKGROUND_COLOR);
        outer.setBorder(new EmptyBorder(22, 12, 22, 22));
        outer.add(buildEntryHeader(user), BorderLayout.NORTH);
        outer.add(buildFormCard(user),    BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildEntryHeader(User user) {
        JPanel titleStack = new JPanel();
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.setOpaque(false);
        JLabel title = new JLabel("Enter Marks");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Theme.TEXT_COLOR);
        JLabel sub = new JLabel("Fill in all " + user.getCourseCount() + " courses and click Submit");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(Theme.SECONDARY_TEXT);
        titleStack.add(title);
        titleStack.add(Box.createVerticalStrut(3));
        titleStack.add(sub);
        return titleStack;
    }

    private JPanel buildFormCard(User user) {
        JPanel formCard = new RoundedPanel(14, Color.WHITE);
        formCard.setLayout(new BorderLayout());

        // Column headers
        JPanel headerRow = new JPanel(new GridLayout(1, 5, 8, 0));
        headerRow.setBackground(new Color(246, 249, 252));
        headerRow.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_COLOR),
            new EmptyBorder(10, 14, 10, 14)));
        for (String h : new String[]{"#", "Code", "Course Name", "Credits", "Score /100"}) {
            JLabel lbl = new JLabel(h);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
            lbl.setForeground(Theme.SECONDARY_TEXT);
            if (h.equals("#")) lbl.setHorizontalAlignment(SwingConstants.CENTER);
            headerRow.add(lbl);
        }
        formCard.add(headerRow, BorderLayout.NORTH);

        JPanel rowsPanel = new JPanel();
        rowsPanel.setLayout(new BoxLayout(rowsPanel, BoxLayout.Y_AXIS));
        rowsPanel.setBackground(Color.WHITE);

        for (int i = 0; i < user.getCourseCount(); i++) {
            JPanel row = new JPanel(new GridLayout(1, 5, 8, 0));
            row.setBackground(i % 2 == 0 ? Color.WHITE : new Color(250, 252, 255));
            row.setBorder(new EmptyBorder(7, 14, 7, 14));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

            JLabel num = new JLabel(String.valueOf(i + 1));
            num.setFont(new Font("SansSerif", Font.BOLD, 10));
            num.setForeground(Theme.SECONDARY_TEXT);
            num.setHorizontalAlignment(SwingConstants.CENTER);

            JTextField code   = new JTextField();
            JTextField name   = new JTextField();
            JTextField credit = new JTextField();
            JTextField score  = new JTextField();

            styleEntryField(code); styleEntryField(name);
            styleEntryField(credit); styleEntryField(score);
            attachScoreFeedback(score);

            row.add(num); row.add(code); row.add(name); row.add(credit); row.add(score);
            codeFields.add(code); nameFields.add(name);
            creditFields.add(credit); scoreFields.add(score);
            rowsPanel.add(row);
        }

        JScrollPane scroll = new JScrollPane(rowsPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        formCard.add(scroll, BorderLayout.CENTER);
        formCard.add(buildButtonRow(), BorderLayout.SOUTH);
        return formCard;
    }

    private JPanel buildButtonRow() {
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow.setBackground(new Color(246, 249, 252));
        btnRow.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER_COLOR),
            new EmptyBorder(14, 16, 14, 16)));

        JButton submitBtn = makeActionBtn("✔  Submit All Marks", Theme.PRIMARY_COLOR);
        JButton aiBtn     = makeActionBtn("🤖  Get AI Guidance",  new Color(33, 150, 243));
        submitBtn.addActionListener(e -> submitMarks());
        aiBtn.addActionListener(e -> new ChatDialog(this, currentUser).setVisible(true));

        btnRow.add(submitBtn);
        btnRow.add(aiBtn);
        return btnRow;
    }

    /** Attaches live color border feedback to a score field on focus lost. */
    private void attachScoreFeedback(JTextField score) {
        score.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                try {
                    double val = Double.parseDouble(score.getText().trim());
                    if (val >= 0 && val <= 100) {
                        String grade = GradeCalculator.calculateGrade(val);
                        score.setToolTipText("Grade: " + grade + "  (" + GradeCalculator.calculateRemark(val) + ")");
                        score.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(gradeColor(grade), 2, true),
                            new EmptyBorder(4, 8, 4, 8)));
                    }
                } catch (NumberFormatException ignored) { }
            }
            @Override public void focusGained(FocusEvent e) {
                styleEntryField(score);
            }
        });
    }

    // ── Submit marks (duplicate guard) ───────────────────────────────
    private void submitMarks() {
        if (DatabaseManager.hasMarks(currentUser.getId())) {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Marks have already been submitted.\nDo you want to overwrite all existing marks?",
                "Overwrite Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) return;
            DatabaseManager.deleteUserMarks(currentUser.getId());
        }

        try {
            for (int i = 0; i < currentUser.getCourseCount(); i++) {
                String code      = codeFields.get(i).getText().trim();
                String name      = nameFields.get(i).getText().trim();
                String creditStr = creditFields.get(i).getText().trim();
                String scoreStr  = scoreFields.get(i).getText().trim();

                if (code.isEmpty() || name.isEmpty() || creditStr.isEmpty() || scoreStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Please fill all fields for row " + (i + 1), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int    credit = Integer.parseInt(creditStr);
                double score  = Double.parseDouble(scoreStr);
                if (score < 0 || score > 100) throw new NumberFormatException();

                DatabaseManager.addMark(currentUser.getId(), code, name, credit, score,
                    GradeCalculator.calculateGrade(score),
                    GradeCalculator.calculateRemark(score));
            }
            refreshTable();
            JOptionPane.showMessageDialog(this,
                "All marks submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            new ChatDialog(this, currentUser).setVisible(true);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Please enter valid numbers for credits and scores (0–100).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Refresh table & recalculate all stats ─────────────────────────
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Mark> marks = DatabaseManager.getUserMarks(currentUser.getId());

        if (marks.isEmpty()) {
            creditsValue.setText("—"); cwaValue.setText("—");  avgValue.setText("—");
            passValue.setText("—");    bestValue.setText("—"); weakValue.setText("—");
            classificationLabel.setText("Submit your marks to see your classification.");
            classificationLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
            classificationLabel.setForeground(Theme.SECONDARY_TEXT);
            classificationCard.setBackground(new Color(245, 250, 255));
            classificationCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true),
                new EmptyBorder(14, 20, 14, 20)));
            return;
        }

        for (Mark m : marks) {
            tableModel.addRow(new Object[]{
                m.getCourseCode(), m.getCourseName(), m.getCreditHours(),
                m.getGrade(), m.getScore(), m.getRemark()
            });
        }

        // ── Compute statistics ─────────────────────────────────────
        int    totalCredits = 0;
        double weightedSum  = 0;
        double totalScore   = 0;
        int    passed       = 0;
        Mark   best         = marks.getFirst();
        Mark   weak         = marks.getFirst();

        for (Mark m : marks) {
            int    cr = m.getCreditHours();
            double sc = m.getScore();
            totalCredits += cr;
            weightedSum  += sc * cr;      // CWA = Σ(score × credits) / totalCredits
            totalScore   += sc;
            if (sc >= 50) passed++;
            if (sc > best.getScore()) best = m;
            if (sc < weak.getScore()) weak = m;
        }

        double cwa      = totalCredits > 0 ? weightedSum / totalCredits : 0;
        double avgScore = totalScore / marks.size();
        double passRate = (passed * 100.0) / marks.size();

        creditsValue.setText(String.valueOf(totalCredits));
        cwaValue.setText(String.format("%.2f", cwa));
        avgValue.setText(String.format("%.1f", avgScore));
        passValue.setText(String.format("%.0f%%", passRate));
        bestValue.setText(best.getCourseCode());
        weakValue.setText(weak.getCourseCode());

        updateClassification(cwa, currentUser.getUsername());
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private Color gradeColor(String grade) {
        return switch (grade) {
            case "A" -> new Color(10, 150, 70);
            case "B" -> new Color(30, 100, 200);
            case "C" -> new Color(180, 130, 0);
            case "D" -> new Color(200, 80, 0);
            default  -> new Color(200, 30, 30);
        };
    }

    private JButton makeTopBtn(String text, ActionListener al) {
        JButton b = new JButton(text);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(true);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1, true),
            new EmptyBorder(6, 14, 6, 14)));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addActionListener(al);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setContentAreaFilled(true);  b.setBackground(new Color(0, 0, 0, 30)); }
            public void mouseExited(MouseEvent e)  { b.setContentAreaFilled(false); }
        });
        return b;
    }

    private JButton makeIconBtn(String icon) {
        JButton b = new JButton(icon);
        b.setBackground(Theme.PRIMARY_COLOR);
        b.setForeground(Color.WHITE);
        b.setOpaque(true); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        b.setPreferredSize(new Dimension(38, 38));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton makeActionBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setOpaque(true); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBorder(new EmptyBorder(10, 18, 10, 18));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    private void styleEntryField(JTextField f) {
        f.setFont(new Font("SansSerif", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true),
            new EmptyBorder(4, 8, 4, 8)));
    }

    // ── Rounded panel helper ──────────────────────────────────────────
    static class RoundedPanel extends JPanel {
        private final int   radius;
        private final Color bg;
        RoundedPanel(int radius, Color bg) { this.radius = radius; this.bg = bg; setOpaque(false); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── Colored grade badge renderer ──────────────────────────────────
    static class GradeBadgeRenderer extends JLabel implements TableCellRenderer {
        GradeBadgeRenderer() { setOpaque(true); setHorizontalAlignment(CENTER); }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            String grade = value == null ? "" : value.toString();
            setText(grade);
            setFont(new Font("SansSerif", Font.BOLD, 12));

            Color badgeBg = switch (grade) {
                case "A" -> new Color(209, 250, 229);
                case "B" -> new Color(219, 234, 254);
                case "C" -> new Color(254, 249, 195);
                case "D" -> new Color(255, 237, 213);
                default  -> new Color(254, 226, 226);
            };
            Color badgeFg = switch (grade) {
                case "A" -> new Color(6,   95,  70);
                case "B" -> new Color(30,  64, 175);
                case "C" -> new Color(133, 77,  14);
                case "D" -> new Color(154, 52,  18);
                default  -> new Color(153, 27,  27);
            };

            setBackground(isSelected ? table.getSelectionBackground()
                                     : (row % 2 == 0 ? Color.WHITE : new Color(248, 251, 255)));
            setForeground(badgeFg);
            putClientProperty("badge_bg", badgeBg);
            putClientProperty("badge_fg", badgeFg);
            return this;
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Color badgeBg = (Color) getClientProperty("badge_bg");
            Color badgeFg = (Color) getClientProperty("badge_fg");
            if (badgeBg == null) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = 32, h = 22;
            int x = (getWidth() - w) / 2, y = (getHeight() - h) / 2;
            g2.setColor(badgeBg);
            g2.fillRoundRect(x, y, w, h, 12, 12);
            g2.setColor(badgeFg);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            String t = getText();
            g2.drawString(t, x + (w - fm.stringWidth(t)) / 2,
                    y + (h + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }
}
