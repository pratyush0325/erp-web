package edu.univ.erp.ui.admin;



import edu.univ.erp.ui.common.ColorPalette;
import edu.univ.erp.ui.common.StatCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(ColorPalette.APP_BG);

        cards.add(buildHome(), "home");
        cards.add(buildUsers(), "users");
        cards.add(buildCourses(), "courses");
        cards.add(buildSections(), "sections");
        cards.add(buildMaintenance(), "maintenance");

        add(cards, BorderLayout.CENTER);
    }

    public void showPage(String key) {
        cardLayout.show(cards, key);
    }

    private JComponent buildHome() {
        JPanel root = scaffold("Admin Dashboard");
        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);
        Icon i1 = UIManager.getIcon("OptionPane.informationIcon");
        Icon i2 = UIManager.getIcon("OptionPane.warningIcon");
        Icon i3 = UIManager.getIcon("OptionPane.questionIcon");
        Icon i4 = UIManager.getIcon("OptionPane.errorIcon");
        grid.add(new StatCard("Total Users", "12", "Admins + Instructors + Students", i1));
        grid.add(new StatCard("Courses", "30", "Across all departments", i2));
        grid.add(new StatCard("Sections", "45", "Current semester", i3));
        grid.add(new StatCard("Maintenance", "OFF", "Toggle available", i4));
        root.add(grid, BorderLayout.CENTER);
        return root;
    }

    private JComponent buildUsers() {
        JPanel root = scaffold("Manage Users");
        JTable table = new JTable(
                new Object[][] {
                        {"admin1", "Admin", "Active"},
                        {"inst1", "Instructor", "Active"},
                        {"stu1", "Student", "Active"}
                },
                new Object[] {"Username", "Role", "Status"}
        );
        decorateTable(table);
        root.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton addBtn = new JButton("Add New User");
        root.add(addBtn, BorderLayout.SOUTH);
        return root;
    }

    private JComponent buildCourses() {
        JPanel root = scaffold("Manage Courses");
        JTable table = new JTable(
                new Object[][] {
                        {"CSE101", "Intro to Programming", 4},
                        {"CSE212", "Data Structures", 4}
                },
                new Object[] {"Code", "Title", "Credits"}
        );
        decorateTable(table);
        root.add(new JScrollPane(table), BorderLayout.CENTER);
        return root;
    }

    private JComponent buildSections() {
        JPanel root = scaffold("Manage Sections");
        JTable table = new JTable(
                new Object[][] {
                        {"CSE212-1", "Data Structures", "Prof. A", "Mon 10:00"},
                },
                new Object[] {"Section", "Course", "Instructor", "Schedule"}
        );
        decorateTable(table);
        root.add(new JScrollPane(table), BorderLayout.CENTER);
        return root;
    }

    private JComponent buildMaintenance() {
        JPanel root = scaffold("Maintenance Mode");
        JCheckBox toggle = new JCheckBox("Enable Maintenance Mode");
        toggle.setFont(new Font("Raleway", Font.BOLD, 14));
        root.add(toggle, BorderLayout.NORTH);
        return root;
    }

    private JPanel scaffold(String heading) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ColorPalette.APP_BG);
        JLabel title = new JLabel(heading);
        title.setBorder(new EmptyBorder(16, 20, 8, 20));
        title.setFont(new Font("Raleway", Font.BOLD, 20));
        title.setForeground(ColorPalette.TEXT_DARK);
        root.add(title, BorderLayout.NORTH);
        root.setBorder(new EmptyBorder(0, 20, 20, 20));
        return root;
    }

    private void decorateTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(26);
        table.setShowGrid(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}
