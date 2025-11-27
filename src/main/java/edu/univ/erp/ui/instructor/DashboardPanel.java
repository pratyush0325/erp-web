package edu.univ.erp.ui.instructor;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.domain.InstructorCourseItem;
import edu.univ.erp.domain.StudentGradeItem;
import edu.univ.erp.ui.common.ColorPalette; // Ensure this exists, or remove color refs

import edu.univ.erp.domain.AssignmentScore;
import java.util.Vector; // For dynamic table model

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import edu.univ.erp.api.maintenance.MaintenanceApi;
import edu.univ.erp.ui.common.ColorPalette;

public class DashboardPanel extends JPanel {

    private final InstructorApi api = new InstructorApi();
    private JPanel contentPanel;

    // --- NEW: Maintenance Banner ---
    private final JPanel maintenanceBanner = new JPanel(new BorderLayout());

    private enum ViewMode {
        ROSTER_ONLY,
        GRADING
    }

    public DashboardPanel() {
        setLayout(new BorderLayout());

        // --- 1. Setup Maintenance Banner (Hidden by default) ---
        maintenanceBanner.setBackground(ColorPalette.MAINT_BG);
        JLabel bannerText = new JLabel("Maintenance Mode is ON — Grading and changes are disabled.");
        bannerText.setBorder(new EmptyBorder(8, 16, 8, 16));
        bannerText.setForeground(ColorPalette.MAINT_TXT);
        bannerText.setFont(new Font("Raleway", Font.BOLD, 13));
        maintenanceBanner.add(bannerText, BorderLayout.CENTER);
        maintenanceBanner.setVisible(false);
        add(maintenanceBanner, BorderLayout.NORTH);
        // -------------------------------------------------------

        contentPanel = new JPanel(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);

        // Start at Home
        showOverview();
    }

    // --- MAIN NAVIGATION ROUTER ---
    public void showPage(String key) {
        // --- 2. Check Status on every page load ---
        boolean isMaint = MaintenanceApi.isMaintenanceOn();
        maintenanceBanner.setVisible(isMaint);
        // ------------------------------------------

        switch (key.toLowerCase()) {
            case "home":
            case "dashboard":
                showOverview();
                break;
            case "sections":
            case "my_sections":
                showCourseList(ViewMode.ROSTER_ONLY);
                break;
            case "grades":
                showCourseList(ViewMode.GRADING);
                break;
            case "stats":
                showStatistics();
                break;
            default:
                System.out.println("Unknown key: " + key);
                break;
        }
    }



    // ==========================================
    // 1. OVERVIEW (HOME)
    // ==========================================
    private void showOverview() {
        contentPanel.removeAll();
        JPanel root = scaffold("Instructor Overview");

        // Panel for Cards
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        cardsPanel.setOpaque(false);

        // Fetch Data
        List<InstructorCourseItem> courses = api.getMyCourses();
        int totalCourses = courses.size();
        int totalStudents = 0;
        for (InstructorCourseItem c : courses) {
            totalStudents += c.getEnrolledCount();
        }

        // Add Cards
        cardsPanel.add(createStatCard("Active Courses", String.valueOf(totalCourses), new Color(0xE1F5FE)));
        cardsPanel.add(createStatCard("Total Students", String.valueOf(totalStudents), new Color(0xE8F5E9)));
        cardsPanel.add(createStatCard("Pending Grades", "5", new Color(0xFFF3E0)));

        // Welcome Text
        JTextArea info = new JTextArea("Welcome, Instructor.\n\n" +
                "• Use 'My Sections' to view student details.\n" +
                "• Use 'Gradebook' to enter marks for Quizzes and Exams.\n" +
                "• Check 'Statistics' for enrollment analysis.");
        info.setEditable(false);
        info.setOpaque(false);
        info.setFont(new Font("SansSerif", Font.PLAIN, 14));
        info.setBorder(new EmptyBorder(20, 20, 0, 0));

        JPanel container = new JPanel(new BorderLayout());
        container.add(cardsPanel, BorderLayout.CENTER);
        container.add(info, BorderLayout.SOUTH);

        root.add(container, BorderLayout.NORTH);

        // --- NEW: Change Password Button ---
        JButton btnPass = new JButton("Change Password");
        btnPass.addActionListener(e -> showChangePasswordDialog());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnPass);
        root.add(btnPanel, BorderLayout.SOUTH);
        // -----------------------------------

        refreshView(root);
    }

    private void showChangePasswordDialog() {
        JPasswordField txtPass = new JPasswordField();
        JPasswordField txtConfirm = new JPasswordField();

        Object[] msg = {
                "New Password:", txtPass,
                "Confirm Password:", txtConfirm
        };

        int opt = JOptionPane.showConfirmDialog(this, msg, "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            String p1 = new String(txtPass.getPassword());
            String p2 = new String(txtConfirm.getPassword());

            if (p1.isEmpty() || !p1.equals(p2)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match or are empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Use a new instance of AuthApi
            edu.univ.erp.api.auth.AuthApi authApi = new edu.univ.erp.api.auth.AuthApi();
            if (authApi.changePassword(p1)) {
                JOptionPane.showMessageDialog(this, "Password changed successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Error changing password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createStatCard(String title, String value, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(220, 120));
        card.setBackground(bgColor);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 36));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblTitle.setForeground(Color.DARK_GRAY);

        card.add(lblValue, BorderLayout.CENTER);
        card.add(lblTitle, BorderLayout.SOUTH);
        return card;
    }

    // ==========================================
    // 2. COURSE LIST (Shared View)
    // ==========================================
    private void showCourseList(ViewMode mode) {
        contentPanel.removeAll();
        String title = (mode == ViewMode.GRADING) ? "Gradebook: Select Course" : "My Sections";
        JPanel root = scaffold(title);

        String[] columnNames = {"ID", "Code", "Title", "Schedule", "Enrolled"};
        List<InstructorCourseItem> courses = api.getMyCourses();

        Object[][] data = new Object[courses.size()][5];
        for (int i = 0; i < courses.size(); i++) {
            InstructorCourseItem c = courses.get(i);
            data[i][0] = c.getSectionId();
            data[i][1] = c.getCourseCode();
            data[i][2] = c.getCourseTitle();
            data[i][3] = c.getSchedule();
            data[i][4] = c.getEnrolledCount() + " / " + c.getCapacity();
        }

        JTable table = new JTable(data, columnNames);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton btnAction = new JButton(mode == ViewMode.GRADING ? "Open Gradebook" : "View Class List");
        btnAction.setEnabled(false);

        table.getSelectionModel().addListSelectionListener(e ->
                btnAction.setEnabled(table.getSelectedRow() != -1)
        );

        btnAction.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int sectionId = (int) table.getValueAt(row, 0);
                String courseName = (String) table.getValueAt(row, 2);

                // ROUTING LOGIC
                if (mode == ViewMode.GRADING) {
                    showComponentGradebook(sectionId, courseName);
                } else {
                    showRoster(sectionId, courseName);
                }
            }
        });

        root.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnAction);
        root.add(btnPanel, BorderLayout.SOUTH);

        refreshView(root);
    }

    // ==========================================
    // 3. ROSTER VIEW (Read Only)
    // ==========================================
    private void showRoster(int sectionId, String courseName) {
        contentPanel.removeAll();
        JPanel root = scaffold("Roster: " + courseName);

        List<StudentGradeItem> students = api.getClassList(sectionId);
        String[] columns = {"ID", "Student Name"};

        Object[][] data = new Object[students.size()][3];
        for (int i = 0; i < students.size(); i++) {
            data[i][0] = students.get(i).getStudentId();
            data[i][1] = students.get(i).getName();
        }

        JTable table = new JTable(data, columns);
        table.setRowHeight(30);
        table.setEnabled(false); // Read only

        JButton btnBack = new JButton("Back to Sections");
        btnBack.addActionListener(e -> showCourseList(ViewMode.ROSTER_ONLY));

        root.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnBack);
        root.add(btnPanel, BorderLayout.SOUTH);

        refreshView(root);
    }

    // ==========================================
    // 4. COMPONENT GRADEBOOK (Editable)
    // ==========================================
    private void showComponentGradebook(int sectionId, String courseName) {
        contentPanel.removeAll();
        JPanel root = scaffold("Grading: " + courseName);

        // 1. Fetch Assignments (Should be Quiz, Midterm, End-Sem if configured)
        List<AssignmentScore> assignments = api.getCourseAssignments(sectionId);

        // Setup Columns
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Student ID");
        columnNames.add("Name");

        // Helper vars to pre-fill the dialog later
        int wQ = 0, wM = 0, wE = 0;

        for (AssignmentScore assign : assignments) {
            columnNames.add(String.format("%s (%d%%)", assign.getAssignmentName(), assign.getWeight()));
            // Capture current weights for the dialog
            if ("Quiz".equals(assign.getAssignmentName())) wQ = assign.getWeight();
            if ("Midterm".equals(assign.getAssignmentName())) wM = assign.getWeight();
            if ("End-Sem".equals(assign.getAssignmentName())) wE = assign.getWeight();
        }
        columnNames.add("Total (%)");

        // 2. Build Data Rows
        List<StudentGradeItem> students = api.getClassList(sectionId);
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (StudentGradeItem student : students) {
            Vector<Object> row = new Vector<>();
            row.add(student.getStudentId());
            row.add(student.getName());

            double totalWeightedScore = 0.0;
            for (AssignmentScore assign : assignments) {
                Double score = api.getScore(student.getStudentId(), assign.getAssignmentId());
                row.add(score == null ? "-" : String.valueOf(score));
                if (score != null) {
                    double percentage = score / assign.getMaxScore();
                    totalWeightedScore += percentage * assign.getWeight();
                }
            }
            row.add(String.format("%.2f%%", totalWeightedScore));
            model.addRow(row);
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // --- BUTTONS ---
        JButton btnBack = new JButton("Back");

        // NEW: Configure Weights Button
        JButton btnWeights = new JButton("Configure Grading");
        if (assignments.isEmpty()) {
            btnWeights.setText("Setup Grading (Required)");
            btnWeights.setForeground(new Color(220, 53, 69)); // Red to alert user
        }

        JButton btnEnterMark = new JButton("Enter Mark");
        btnEnterMark.setEnabled(false);

        table.getSelectionModel().addListSelectionListener(e ->
                btnEnterMark.setEnabled(table.getSelectedRow() != -1)
        );

        // Logic: Configure Weights
        int finalWQ = wQ; int finalWM = wM; int finalWE = wE; // eff-final for lambda
        btnWeights.addActionListener(e -> {
            if (MaintenanceApi.isMaintenanceOn()) {
                JOptionPane.showMessageDialog(root, "Maintenance Mode ON. Changes disabled.", "Blocked", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JTextField txtQuiz = new JTextField(String.valueOf(finalWQ > 0 ? finalWQ : 20));
            JTextField txtMid = new JTextField(String.valueOf(finalWM > 0 ? finalWM : 30));
            JTextField txtEnd = new JTextField(String.valueOf(finalWE > 0 ? finalWE : 50));

            JPanel panel = new JPanel(new GridLayout(0, 2));
            panel.add(new JLabel("Quiz Weight (%):")); panel.add(txtQuiz);
            panel.add(new JLabel("Midterm Weight (%):")); panel.add(txtMid);
            panel.add(new JLabel("End-Sem Weight (%):")); panel.add(txtEnd);
            panel.add(new JLabel("Note: Max Score defaults to 100")); panel.add(new JLabel(""));

            int result = JOptionPane.showConfirmDialog(root, panel, "Configure Grading Scheme", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    int q = Integer.parseInt(txtQuiz.getText());
                    int m = Integer.parseInt(txtMid.getText());
                    int end = Integer.parseInt(txtEnd.getText());

                    if (q + m + end != 100) {
                        JOptionPane.showMessageDialog(root, "Weights must sum to exactly 100%. (Current: " + (q+m+end) + "%)", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (api.saveWeights(sectionId, q, m, end)) {
                        JOptionPane.showMessageDialog(root, "Grading scheme updated!");
                        showComponentGradebook(sectionId, courseName); // Refresh
                    } else {
                        JOptionPane.showMessageDialog(root, "Failed to save.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(root, "Please enter valid integers.");
                }
            }
        });

        // Logic: Enter Mark (Same as before)
        btnEnterMark.addActionListener(e -> {
            if (MaintenanceApi.isMaintenanceOn()) {
                JOptionPane.showMessageDialog(root, "Maintenance Mode ON.", "Blocked", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int row = table.getSelectedRow();
            int col = table.getSelectedColumn();

            if (row != -1 && col >= 2 && col < columnNames.size() - 1) {
                int studentId = (int) table.getValueAt(row, 0);
                AssignmentScore currentAssignment = assignments.get(col - 2);

                String input = JOptionPane.showInputDialog(root,
                        "Enter score for " + currentAssignment.getAssignmentName() +
                                " (Max " + currentAssignment.getMaxScore() + "):");

                if (input != null && !input.trim().isEmpty()) {
                    try {
                        double scoreVal = Double.parseDouble(input);
                        if(scoreVal > currentAssignment.getMaxScore()) {
                            JOptionPane.showMessageDialog(root, "Score cannot be higher than " + currentAssignment.getMaxScore());
                            return;
                        }
                        if (api.updateComponentScore(currentAssignment.getAssignmentId(), studentId, scoreVal)) {
                            showComponentGradebook(sectionId, courseName);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(root, "Invalid number.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(root, "Click on a specific Quiz/Exam cell to grade.");
            }
        });

        btnBack.addActionListener(e -> showCourseList(ViewMode.GRADING));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnBack);
        btnPanel.add(btnWeights);
        btnPanel.add(btnEnterMark);

        root.add(new JScrollPane(table), BorderLayout.CENTER);
        root.add(btnPanel, BorderLayout.SOUTH);

        refreshView(root);
    }

    // ==========================================
    // 5. STATISTICS
    // ==========================================
    private void showStatistics() {
        contentPanel.removeAll();
        JPanel root = scaffold("Enrollment Statistics");

        List<InstructorCourseItem> courses = api.getMyCourses();

        JPanel statsContainer = new JPanel();
        statsContainer.setLayout(new BoxLayout(statsContainer, BoxLayout.Y_AXIS));
        statsContainer.setBorder(new EmptyBorder(20, 20, 20, 20));
        statsContainer.setBackground(Color.WHITE); // Make sure background is visible

        if (courses.isEmpty()) {
            statsContainer.add(new JLabel("No courses assigned to generate statistics."));
        } else {
            for (InstructorCourseItem c : courses) {
                JPanel row = new JPanel(new BorderLayout(10, 10));
                row.setBorder(new EmptyBorder(0, 0, 15, 0));
                row.setBackground(Color.WHITE);

                JLabel lblName = new JLabel(c.getCourseCode());
                lblName.setPreferredSize(new Dimension(80, 20));

                // Avoid divide by zero
                int max = c.getCapacity() > 0 ? c.getCapacity() : 50;
                JProgressBar bar = new JProgressBar(0, max);
                bar.setValue(c.getEnrolledCount());
                bar.setStringPainted(true);
                bar.setString(c.getEnrolledCount() + " / " + max + " Enrolled");

                // Color the bar based on fullness
                if (c.getEnrolledCount() >= max) bar.setForeground(new Color(220, 53, 69)); // Red if full
                else bar.setForeground(new Color(40, 167, 69)); // Green otherwise

                row.add(lblName, BorderLayout.WEST);
                row.add(bar, BorderLayout.CENTER);
                statsContainer.add(row);
            }
        }

        root.add(new JScrollPane(statsContainer), BorderLayout.CENTER);

        contentPanel.add(root);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // --- UTILITIES ---

    private JPanel scaffold(String heading) {
        JPanel root = new JPanel(new BorderLayout());
        JLabel title = new JLabel(heading);
        title.setBorder(new EmptyBorder(16, 20, 8, 20));
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        root.add(title, BorderLayout.NORTH);
        root.setBorder(new EmptyBorder(0, 20, 20, 20));
        return root;
    }

    private void refreshView(JPanel root) {
        contentPanel.add(root);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}