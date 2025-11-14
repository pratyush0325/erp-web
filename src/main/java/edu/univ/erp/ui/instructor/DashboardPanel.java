package edu.univ.erp.ui.instructor;

import edu.univ.erp.ui.common.ColorPalette;
import edu.univ.erp.ui.common.StatCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private final JPanel maintenanceBanner = new JPanel(new BorderLayout());

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(ColorPalette.APP_BG);

        // Maintenance banner
        maintenanceBanner.setBackground(ColorPalette.MAINT_BG);
        JLabel bannerText = new JLabel("Maintenance Mode is ON — Edits are temporarily disabled.");
        bannerText.setBorder(new EmptyBorder(8, 16, 8, 16));
        bannerText.setForeground(ColorPalette.MAINT_TXT);
        bannerText.setFont(new Font("Raleway", Font.BOLD, 13));
        maintenanceBanner.add(bannerText, BorderLayout.CENTER);
        maintenanceBanner.setVisible(false);
        add(maintenanceBanner, BorderLayout.NORTH);

        // Pages
        cards.add(buildHome(), "home");
        cards.add(buildSections(), "sections");
        cards.add(buildGrades(), "grades");
        cards.add(buildStats(), "stats");
        add(cards, BorderLayout.CENTER);
    }

    public void showPage(String key) { cardLayout.show(cards, key); }
    public void setMaintenanceMode(boolean on) {
        maintenanceBanner.setVisible(on);
        revalidate();
        repaint();
    }

    private JComponent buildHome() {
        JPanel root = scaffold("Dashboard");
        JPanel grid = new JPanel(new GridLayout(1, 3, 16, 16));
        grid.setOpaque(false);
        Icon ic1 = UIManager.getIcon("OptionPane.informationIcon");
        Icon ic2 = UIManager.getIcon("OptionPane.warningIcon");
        Icon ic3 = UIManager.getIcon("OptionPane.questionIcon");
        grid.add(new StatCard("My Sections", "3", "This semester", ic1));
        grid.add(new StatCard("Students", "90", "Across all sections", ic2));
        grid.add(new StatCard("Pending Grades", "2", "Need submission", ic3));
        root.add(grid, BorderLayout.CENTER);
        return root;
    }

    private JComponent buildSections() {
        JPanel root = scaffold("My Sections");
        JTable table = new JTable(
                new Object[][] {
                        {"CSE212-1", "Data Structures", "Mon/Wed 10:00", "Room 201"},
                        {"CSE212-L1", "Data Structures Lab", "Fri 14:00", "Lab B1"}
                },
                new Object[] {"Section", "Title", "Schedule", "Room"}
        );
        decorateTable(table);
        root.add(new JScrollPane(table), BorderLayout.CENTER);
        return root;
    }

    private JComponent buildGrades() {
        JPanel root = scaffold("Gradebook");
        JTable table = new JTable(
                new Object[][] {
                        {"CSE212", "Quiz 1", "20%", "Completed"},
                        {"CSE212", "Midterm", "30%", "Pending"},
                        {"CSE212", "End Sem", "50%", "Pending"}
                },
                new Object[] {"Course", "Component", "Weight", "Status"}
        );
        decorateTable(table);
        root.add(new JScrollPane(table), BorderLayout.CENTER);
        return root;
    }

    private JComponent buildStats() {
        JPanel root = scaffold("Class Statistics");
        JTextArea area = new JTextArea("Average Scores:\nCSE212 - 78%\nCSE212-L1 - 82%\n\nTotal Enrolled: 90");
        area.setEditable(false);
        root.add(new JScrollPane(area), BorderLayout.CENTER);
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
