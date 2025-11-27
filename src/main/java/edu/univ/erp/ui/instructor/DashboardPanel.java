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
import edu.univ.erp.util.GradeUtils;

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

        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        cardsPanel.setOpaque(false);

        // 1. Fetch Data
        List<InstructorCourseItem> courses = api.getMyCourses();
        int totalCourses = courses.size();
        int totalStudents = 0;
        for (InstructorCourseItem c : courses) {
            totalStudents += c.getEnrolledCount();
        }
        int pendingGrades = api.getPendingGrades();

        // NEW: Fetch Deadline
        String deadline = api.getDeadline();

        // 2. Add Cards
        cardsPanel.add(createStatCard("Active Courses", String.valueOf(totalCourses), new Color(0xE1F5FE)));
        cardsPanel.add(createStatCard("Total Students", String.valueOf(totalStudents), new Color(0xE8F5E9)));
        cardsPanel.add(createStatCard("Pending Grades", String.valueOf(pendingGrades), new Color(0xFFF3E0)));

        // NEW CARD
        cardsPanel.add(createStatCard("Add/Drop Deadline", deadline, new Color(0xF3E5F5)));

//        JTextArea info = new JTextArea("Welcome, Instructor.\n\n" +
//                "• Use 'My Sections' to view student details.\n" +
//                "• Use 'Gradebook' to enter marks for Quizzes and Exams.\n" +
//                "• Check 'Statistics' for enrollment analysis.");
//        info.setEditable(false);
//        info.setOpaque(false);
//        info.setFont(new Font("SansSerif", Font.PLAIN, 14));
//        info.setBorder(new EmptyBorder(20, 20, 0, 0));

        JPanel container = new JPanel(new BorderLayout());
        container.add(cardsPanel, BorderLayout.CENTER);
//        container.add(info, BorderLayout.SOUTH);

        root.add(container, BorderLayout.NORTH);

        // Change Password Button
        JButton btnPass = new JButton("Change Password");
        btnPass.addActionListener(e -> showChangePasswordDialog());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnPass);
        root.add(btnPanel, BorderLayout.SOUTH);

        refreshView(root);
    }

    private void showChangePasswordDialog() {
        JPasswordField txtCurrent = new JPasswordField();
        JPasswordField txtPass = new JPasswordField();
        JPasswordField txtConfirm = new JPasswordField();

        Object[] msg = {
                "Current Password:", txtCurrent,
                "New Password:", txtPass,
                "Confirm Password:", txtConfirm
        };

        int opt = JOptionPane.showConfirmDialog(this, msg, "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            String curr = new String(txtCurrent.getPassword());
            String p1 = new String(txtPass.getPassword());
            String p2 = new String(txtConfirm.getPassword());

            if (curr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your current password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (p1.isEmpty() || !p1.equals(p2)) {
                JOptionPane.showMessageDialog(this, "New passwords do not match or are empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Call API with Current + New password
            edu.univ.erp.api.auth.AuthApi authApi = new edu.univ.erp.api.auth.AuthApi();
            if (authApi.changePassword(curr, p1)) {
                JOptionPane.showMessageDialog(this, "Password changed successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Error changing password. (Is your current password correct?)", "Error", JOptionPane.ERROR_MESSAGE);
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

        // 1. Fetch Assignments
        List<AssignmentScore> assignments = api.getCourseAssignments(sectionId);

        Vector<String> columnNames = new Vector<>();
        columnNames.add("Student ID");
        columnNames.add("Name");

        int wQ = 0, wM = 0, wE = 0;

        for (AssignmentScore assign : assignments) {
            columnNames.add(String.format("%s (%d%%)", assign.getAssignmentName(), assign.getWeight()));
            if ("Quiz".equals(assign.getAssignmentName())) wQ = assign.getWeight();
            if ("Midterm".equals(assign.getAssignmentName())) wM = assign.getWeight();
            if ("End-Sem".equals(assign.getAssignmentName())) wE = assign.getWeight();
        }
        columnNames.add("Total (%)");
        columnNames.add("Grade");

        // 2. Build Rows
        List<StudentGradeItem> students = api.getClassList(sectionId);
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (StudentGradeItem student : students) {
            Vector<Object> row = new Vector<>();
            row.add(student.getStudentId());
            row.add(student.getName());

            double totalWeightedScore = 0.0;
            int totalGradedWeight = 0; // <--- Track how much weight has been graded

            for (AssignmentScore assign : assignments) {
                Double score = api.getScore(student.getStudentId(), assign.getAssignmentId());
                row.add(score == null ? "-" : String.valueOf(score));

                if (score != null) {
                    double percentage = score / assign.getMaxScore();
                    totalWeightedScore += percentage * assign.getWeight();
                    totalGradedWeight += assign.getWeight(); // <--- Add weight if graded
                }
            }

            row.add(String.format("%.2f%%", totalWeightedScore));

            // --- NEW LOGIC: Check if course is complete ---
            if (totalGradedWeight < 100) {
                row.add("In Progress");
            } else {
                row.add(edu.univ.erp.util.GradeUtils.getLetterGrade(totalWeightedScore));
            }
            // ----------------------------------------------

            model.addRow(row);
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // --- BUTTONS ---
        JButton btnBack = new JButton("Back");

        JButton btnWeights = new JButton("Configure Grading");
        if (assignments.isEmpty()) {
            btnWeights.setText("Setup Grading (Required)");
            btnWeights.setForeground(new Color(220, 53, 69));
        }

        JButton btnEnterMark = new JButton("Enter Mark");
        btnEnterMark.setEnabled(false);

        table.getSelectionModel().addListSelectionListener(e ->
                btnEnterMark.setEnabled(table.getSelectedRow() != -1)
        );

        // Configure Weights Logic
        int finalWQ = wQ; int finalWM = wM; int finalWE = wE;
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
                        showComponentGradebook(sectionId, courseName);
                    } else {
                        JOptionPane.showMessageDialog(root, "Failed to save.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(root, "Please enter valid integers.");
                }
            }
        });

        // Enter Mark Logic
        // Logic: Enter Mark
        btnEnterMark.addActionListener(e -> {
            if (MaintenanceApi.isMaintenanceOn()) {
                JOptionPane.showMessageDialog(root, "Maintenance Mode ON.", "Blocked", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int row = table.getSelectedRow();
            int col = table.getSelectedColumn();

            // Ensure the user clicked on an Assignment column (not ID, Name, Total, or Grade)
            if (row != -1 && col >= 2 && col < columnNames.size() - 2) {
                int studentId = (int) table.getValueAt(row, 0);
                AssignmentScore currentAssignment = assignments.get(col - 2);

                String input = JOptionPane.showInputDialog(root,
                        "Enter score for " + currentAssignment.getAssignmentName() +
                                " (Max " + currentAssignment.getMaxScore() + "):");

                if (input != null && !input.trim().isEmpty()) {
                    try {
                        double scoreVal = Double.parseDouble(input);

                        // --- VALIDATION: No Negative Scores ---
                        if (scoreVal < 0) {
                            JOptionPane.showMessageDialog(root, "Score cannot be negative.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        // --------------------------------------

                        if(scoreVal > currentAssignment.getMaxScore()) {
                            JOptionPane.showMessageDialog(root, "Score cannot be higher than " + currentAssignment.getMaxScore(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        if (api.updateComponentScore(currentAssignment.getAssignmentId(), studentId, scoreVal)) {
                            showComponentGradebook(sectionId, courseName);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(root, "Invalid number. Please enter a valid score.");
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
    // 5. STATISTICS (UPDATED)
    // ==========================================
    private void showStatistics() {
        contentPanel.removeAll();
        JPanel root = scaffold("Course Statistics");

        // 1. Fetch Calculated Data
        List<edu.univ.erp.domain.CourseStats> statsList = api.getAllCourseStatistics();

        // 2. Setup Table Columns
        String[] columnNames = {
                "Course",
                "Title",
                "Enrolled",
                "Graded",
                "Min (%)",
                "Max (%)",
                "Avg (%)"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // 3. Populate Rows
        for (edu.univ.erp.domain.CourseStats s : statsList) {
            model.addRow(new Object[]{
                    s.getCourseCode(),
                    s.getCourseTitle(),
                    s.getTotalEnrolled(),
                    s.getGradesSubmitted(), // Count of students with grades
                    String.format("%.2f", s.getMinScore()),
                    String.format("%.2f", s.getMaxScore()),
                    String.format("%.2f", s.getAvgScore())
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setEnabled(false); // Read-only view
        table.setAutoCreateRowSorter(true); // Allow sorting by column

        root.add(new JScrollPane(table), BorderLayout.CENTER);

        // (Refresh button removed - navigation auto-refreshes)

        refreshView(root);
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