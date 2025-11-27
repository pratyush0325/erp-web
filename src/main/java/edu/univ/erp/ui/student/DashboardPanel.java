package edu.univ.erp.ui.student;

import edu.univ.erp.api.student.DropStatus;
import edu.univ.erp.ui.common.ColorPalette;
import edu.univ.erp.ui.common.StatCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import edu.univ.erp.api.catalog.CatalogApi;
import edu.univ.erp.domain.CatalogItem;
import java.util.List;
import javax.swing.table.DefaultTableModel;

import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.domain.RegistrationItem;

import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.api.student.RegistrationStatus;
import edu.univ.erp.domain.CatalogItem;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class DashboardPanel extends JPanel {

    public static final String PAGE_DASHBOARD      = "dashboard";
    public static final String PAGE_CATALOG        = "catalog";
    public static final String PAGE_REGISTRATIONS  = "registrations";
    public static final String PAGE_TIMETABLE      = "timetable";
    public static final String PAGE_GRADES         = "grades";
    public static final String PAGE_TRANSCRIPT     = "transcript";
    public static final String PAGE_PROFILE        = "profile";

    private List<RegistrationItem> registrationItems; // Stores current registration data
    private int selectedRegistrationSectionId = -1;   // ID of the selected row
    private JButton btnDrop;                          // The drop button
    private JPanel registrationsPagePanel;            // The main panel for the page
    private JScrollPane registrationsTableScrollPane; // The scroll pane for the table

    private final StudentApi studentApi = new StudentApi();
    private List<CatalogItem> catalogItems; // Stores the data from the API
    private int selectedSectionId = -1; // Stores the ID of the selected row
    private JButton btnRegister; // The register button

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private final JPanel maintenanceBanner = new JPanel(new BorderLayout());
    private DefaultTableModel timetableModel; // <--- NEW FIELD

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(ColorPalette.APP_BG);

        maintenanceBanner.setBackground(ColorPalette.MAINT_BG);
        JLabel bannerText = new JLabel("Maintenance Mode is ON — Student changes are temporarily disabled.");
        bannerText.setBorder(new EmptyBorder(8, 16, 8, 16));
        bannerText.setForeground(ColorPalette.MAINT_TXT);
        bannerText.setFont(new Font("Raleway", Font.BOLD, 13));
        maintenanceBanner.add(bannerText, BorderLayout.CENTER);
        maintenanceBanner.setVisible(false);
        add(maintenanceBanner, BorderLayout.NORTH);

        cards.add(buildDashboardHome(), PAGE_DASHBOARD);
        cards.add(buildCatalogPage(), PAGE_CATALOG);
        cards.add(buildRegistrationsPage(), PAGE_REGISTRATIONS);
        cards.add(buildTimetablePage(), PAGE_TIMETABLE);
        cards.add(buildGradesPage(), PAGE_GRADES);
        cards.add(buildTranscriptPage(), PAGE_TRANSCRIPT);
        cards.add(buildProfilePage(), PAGE_PROFILE);

        add(cards, BorderLayout.CENTER);
    }

    public void showPage(String key) {
        // 1. Check Maintenance Status
        boolean isMaint = edu.univ.erp.api.maintenance.MaintenanceApi.isMaintenanceOn();
        setMaintenanceMode(isMaint);

        // 2. Refresh specific pages when opened
        if (PAGE_TIMETABLE.equals(key)) {
            refreshTimetable(); // <--- AUTO REFRESH
        }
        if (PAGE_REGISTRATIONS.equals(key)) {
            refreshRegistrationsPage();
        }
        if (PAGE_GRADES.equals(key)) {
            // We can also auto-refresh grades here if we want
            // (You'd need to refactor buildGradesPage similarly to use a class-level model)
        }

        // 3. Show the card
        cardLayout.show(cards, key);
    }

    public void setMaintenanceMode(boolean on) {
        maintenanceBanner.setVisible(on);
        revalidate();
        repaint();
    }


    private JComponent buildDashboardHome() {
        JPanel root = pageScaffold("Dashboard");
        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setOpaque(false);

        // Placeholder icons
        Icon courseIcon = UIManager.getIcon("OptionPane.informationIcon");
        Icon regIcon    = UIManager.getIcon("OptionPane.questionIcon");
        Icon timeIcon   = UIManager.getIcon("OptionPane.warningIcon");
        Icon gradeIcon  = UIManager.getIcon("OptionPane.informationIcon");
        Icon gpaIcon    = UIManager.getIcon("OptionPane.informationIcon");
        Icon holdIcon   = UIManager.getIcon("OptionPane.errorIcon");

        grid.add(new StatCard("Courses this term", "4", "Including labs", courseIcon));
        grid.add(new StatCard("My Registrations", "4/6", "Capacity remaining", regIcon));
        grid.add(new StatCard("Classes today", "2", "Next: 11:00 AM", timeIcon));
        grid.add(new StatCard("Graded items", "7", "New: 1 midterm posted", gradeIcon));
        grid.add(new StatCard("Current GPA", "8.21", "UG program", gpaIcon));
        grid.add(new StatCard("Holds/Warnings", "0", "All clear", holdIcon));

        root.add(grid, BorderLayout.CENTER);

        // --- NEW: Change Password Button ---
        JButton btnPass = new JButton("Change Password");
        btnPass.addActionListener(e -> showChangePasswordDialog());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(btnPass);
        root.add(btnPanel, BorderLayout.SOUTH);
        // -----------------------------------

        return root;
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

            // Use a new instance of AuthApi to handle the request
            edu.univ.erp.api.auth.AuthApi authApi = new edu.univ.erp.api.auth.AuthApi();
            if (authApi.changePassword(p1)) {
                JOptionPane.showMessageDialog(this, "Password changed successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Error changing password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JComponent buildCatalogPage() {
        JPanel root = pageScaffold("Course Catalog");

        // 1. Define columns (we will NOT add section_id here)
        String[] columnNames = {"Code", "Title", "Credits", "Instructor", "Capacity"};

        // 2. Fetch data from the API
        CatalogApi catalogApi = new CatalogApi();
        catalogItems = catalogApi.getCatalog(); // Store data in the class field

        // 3. Convert List to Object[][] for the JTable
        Object[][] data = new Object[catalogItems.size()][5];
        for (int i = 0; i < catalogItems.size(); i++) {
            CatalogItem item = catalogItems.get(i);
            data[i][0] = item.getCode();
            data[i][1] = item.getTitle();
            data[i][2] = item.getCredits();
            data[i][3] = item.getInstructorName();
            data[i][4] = item.getCapacity();
        }

        // 4. Create the table
        JTable table = new JTable(data, columnNames);
        table.setDefaultEditor(Object.class, null);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        decorateTable(table);

        // 5. Create the "Register" button
        btnRegister = new JButton("Register for Selected Course");
        btnRegister.setEnabled(false); // Disabled by default

        // 6. Add a listener to the table to enable/disable the button
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                // A row is selected
                int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
                // Get the hidden sectionId from our stored List
                selectedSectionId = catalogItems.get(modelRow).getSectionId();
                btnRegister.setEnabled(true);
            } else {
                selectedSectionId = -1;
                btnRegister.setEnabled(false);
            }
        });

        // 7. Add the button's action listener (calls the API)
        btnRegister.addActionListener(e -> {
            if (selectedSectionId == -1) return;

            // Call the API we built!
            RegistrationStatus status = studentApi.registerForSection(selectedSectionId);

            // Show a friendly message
            String message;
            int messageType;

            switch (status) {
                case MAINTENANCE_MODE:
                    message = "System is under maintenance. Registration is disabled.";
                    messageType = JOptionPane.WARNING_MESSAGE;
                    break;
                case DEADLINE_PASSED: // <--- NEW
                    message = "Registration is closed. The deadline has passed.";
                    messageType = JOptionPane.ERROR_MESSAGE;
                    break;
                case SUCCESS:
                    message = "Successfully registered for the course!";
                    messageType = JOptionPane.INFORMATION_MESSAGE;
                    refreshRegistrationsPage();
                    break;
                case ALREADY_REGISTERED:
                    message = "You are already registered for this section.";
                    messageType = JOptionPane.WARNING_MESSAGE;
                    break;
                case SECTION_FULL:
                    message = "Sorry, this section is full.";
                    messageType = JOptionPane.ERROR_MESSAGE;
                    break;
                default:
                    message = "An unknown error occurred. Please try again.";
                    messageType = JOptionPane.ERROR_MESSAGE;
                    break;
            }
            JOptionPane.showMessageDialog(root, message, "Registration Status", messageType);
        });

        // 8. Add components to the panel
        root.add(new JScrollPane(table), BorderLayout.CENTER);
        root.add(btnRegister, BorderLayout.SOUTH);
        return root;
    }

    private JComponent buildRegistrationsPage() {
        // Use the class field for the page panel
        registrationsPagePanel = pageScaffold("My Registrations");

        // Create the drop button
        btnDrop = new JButton("Drop Selected Section");
        btnDrop.setEnabled(false);

        // Add the button's action listener (calls the API)
        btnDrop.addActionListener(e -> {
            if (selectedRegistrationSectionId == -1) return;

            // Confirmation dialog
            int choice = JOptionPane.showConfirmDialog(registrationsPagePanel,
                    "Are you sure you want to drop this section?",
                    "Confirm Drop", JOptionPane.YES_NO_OPTION);

            if (choice != JOptionPane.YES_OPTION) {
                return;
            }

            // Call the API we built!
            DropStatus status = studentApi.dropSection(selectedRegistrationSectionId);

            String message;
            int messageType;

            switch (status) {
                case MAINTENANCE_MODE:
                    message = "System is under maintenance. You cannot drop courses now.";
                    messageType = JOptionPane.WARNING_MESSAGE;
                    break;
                case DEADLINE_PASSED:
                    message = "Cannot drop course. The deadline has passed.";
                    messageType = JOptionPane.ERROR_MESSAGE;
                    break;
                case SUCCESS:
                    message = "Successfully dropped the section.";
                    messageType = JOptionPane.INFORMATION_MESSAGE;
                    refreshRegistrationsPage(); // <-- REFRESH THE TABLE
                    break;
                case NOT_REGISTERED:
                    message = "You are not registered for this section.";
                    messageType = JOptionPane.WARNING_MESSAGE;
                    break;
                default:
                    message = "An unknown error occurred.";
                    messageType = JOptionPane.ERROR_MESSAGE;
                    break;
            }
            JOptionPane.showMessageDialog(registrationsPagePanel, message, "Drop Status", messageType);
        });

        // Initial load of the table
        refreshRegistrationsPage();

        registrationsPagePanel.add(registrationsTableScrollPane, BorderLayout.CENTER);
        registrationsPagePanel.add(btnDrop, BorderLayout.SOUTH);
        return registrationsPagePanel;
    }

    private JComponent buildTimetablePage() {
        JPanel root = pageScaffold("Timetable");

        String[] columns = {"Day/Time", "Code", "Title", "Room"};
        timetableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(timetableModel);
        decorateTable(table);
        table.setAutoCreateRowSorter(true);

        root.add(new JScrollPane(table), BorderLayout.CENTER);

        // Refresh Button
        JButton btnRefresh = new JButton("Refresh Timetable");
        btnRefresh.addActionListener(e -> refreshTimetable()); // <--- Calls the helper directly

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(btnRefresh);
        root.add(btnPanel, BorderLayout.SOUTH);

        // Load initial data
        refreshTimetable();

        return root;
    }

    private void refreshTimetable() {
        if (timetableModel == null) return;

        // 1. Clear existing rows
        timetableModel.setRowCount(0);

        // 2. Fetch fresh data from DB
        java.util.List<edu.univ.erp.domain.RegistrationItem> items = studentApi.getMyRegistrations();

        // 3. Populate table
        for (edu.univ.erp.domain.RegistrationItem item : items) {
            timetableModel.addRow(new Object[]{
                    item.getSchedule(),
                    item.getCourseCode(),
                    item.getTitle(),
                    item.getRoom()
            });
        }
    }

    private JComponent buildGradesPage() {
        JPanel root = pageScaffold("Grades");

        // 1. Fetch Real Data
        java.util.List<edu.univ.erp.domain.StudentGradeView> grades = studentApi.getMyGrades();

        // 2. Prepare Table Data
        String[] columns = {"Course", "Component", "Score", "Max"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0);

        for (edu.univ.erp.domain.StudentGradeView g : grades) {
            model.addRow(new Object[]{
                    g.getCourseCode(),
                    g.getComponent(),
                    g.getScore(),
                    g.getMaxScore()
            });
        }

        JTable table = new JTable(model);
        decorateTable(table);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        // Add refresh button
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> showPage(PAGE_GRADES));
        root.add(btnRefresh, BorderLayout.SOUTH);

        return root;
    }

    private JComponent buildTranscriptPage() {
        JPanel root = pageScaffold("Transcript");

        JTextArea transcriptArea = new JTextArea();
        transcriptArea.setEditable(false);
        transcriptArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        transcriptArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Fetch & Calculate Grades
        java.util.List<edu.univ.erp.domain.StudentGradeView> grades = studentApi.getMyGrades();

        // Helper map to aggregate scores: CourseCode -> FinalPercentage
        java.util.Map<String, Double> courseTotals = new java.util.HashMap<>();
        java.util.Map<String, String> courseTitles = new java.util.HashMap<>();

        for (edu.univ.erp.domain.StudentGradeView g : grades) {
            // Formula: (Score / Max) * Weight
            double weightedScore = (g.getScore() / g.getMaxScore()) * g.getWeight();

            courseTotals.put(g.getCourseCode(),
                    courseTotals.getOrDefault(g.getCourseCode(), 0.0) + weightedScore
            );
            courseTitles.put(g.getCourseCode(), g.getCourseTitle());
        }

        // 2. Build Display Text
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s %-40s %s\n", "CODE", "TITLE", "GRADE"));
        sb.append("------------------------------------------------------------\n");

        for (String code : courseTotals.keySet()) {
            double finalPct = courseTotals.get(code);
            String letterGrade = getLetterGrade(finalPct);
            sb.append(String.format("%-10s %-40s %.2f%% (%s)\n",
                    code, courseTitles.get(code), finalPct, letterGrade));
        }

        transcriptArea.setText(sb.toString());

        // 3. Export Button Logic
        JButton exportCsv = new JButton("Export CSV");
        exportCsv.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Transcript");
            fileChooser.setSelectedFile(new java.io.File("transcript.csv"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (java.io.PrintWriter pw = new java.io.PrintWriter(fileChooser.getSelectedFile())) {
                    pw.println("Code,Title,Percentage,Grade");
                    for (String code : courseTotals.keySet()) {
                        double finalPct = courseTotals.get(code);
                        pw.printf("%s,%s,%.2f,%s%n",
                                code, courseTitles.get(code), finalPct, getLetterGrade(finalPct));
                    }
                    JOptionPane.showMessageDialog(this, "Export Successful!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error exporting file.");
                    ex.printStackTrace();
                }
            }
        });

        root.add(new JScrollPane(transcriptArea), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(exportCsv);
        root.add(btnPanel, BorderLayout.SOUTH);

        return root;
    }

    // Helper for Letter Grades
    private String getLetterGrade(double percentage) {
        if (percentage >= 90) return "A";
        if (percentage >= 80) return "B";
        if (percentage >= 70) return "C";
        if (percentage >= 60) return "D";
        return "F";
    }

    private JComponent buildProfilePage() {
        JPanel root = pageScaffold("Profile");

        // 1. Fetch Real Data
        edu.univ.erp.domain.StudentProfile profile = studentApi.getProfile();

        if (profile == null) {
            root.add(new JLabel("Profile data not found. Please contact Admin."), BorderLayout.CENTER);
            return root;
        }

        // 2. Build Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Helper to add rows
        addProfileRow(form, gc, 0, "Name:", profile.getName());
        addProfileRow(form, gc, 1, "Roll No:", profile.getRollNo());
        addProfileRow(form, gc, 2, "Program:", profile.getProgram());
        addProfileRow(form, gc, 3, "Year:", String.valueOf(profile.getYear()));

        // Add a "logout" hint or button
        gc.gridy = 4;
        gc.gridx = 1;
        gc.weighty = 1.0; // push to top
        gc.anchor = GridBagConstraints.NORTHWEST;
        JLabel hint = new JLabel("To change details, contact Administrator.");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 12));
        hint.setForeground(ColorPalette.TEXT_MUTED);
        form.add(hint, gc);

        root.add(form, BorderLayout.CENTER);
        return root;
    }

    private void addProfileRow(JPanel panel, GridBagConstraints gc, int row, String label, String value) {
        gc.gridy = row;

        // Label
        gc.gridx = 0;
        gc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Raleway", Font.BOLD, 14));
        lbl.setForeground(ColorPalette.TEXT_DARK);
        panel.add(lbl, gc);

        // Field (Read-only)
        gc.gridx = 1;
        gc.weightx = 1.0;
        JTextField txt = new JTextField(value);
        txt.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txt.setEditable(false); // Make it read-only
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8))
        );
        panel.add(txt, gc);
    }

    private JPanel pageScaffold(String heading) {
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(true);
        root.setBackground(ColorPalette.APP_BG);
        JLabel title = new JLabel(heading);
        title.setBorder(new EmptyBorder(16, 20, 8, 20));
        title.setFont(new Font("Raleway", Font.BOLD, 20));
        title.setForeground(ColorPalette.TEXT_DARK);
        root.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 20, 20, 20));
        root.add(content, BorderLayout.CENTER);

        return root;
    }

    private void decorateTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(26);
        table.setShowGrid(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Fetches fresh data from the API and rebuilds the "My Registrations" table.
     */
    private void refreshRegistrationsPage() {
        registrationItems = studentApi.getMyRegistrations();

        // UPDATED Columns: Added "Code" at the start
        String[] columnNames = {"Code", "Section", "Title", "Schedule", "Room", "Status"};

        Object[][] data = new Object[registrationItems.size()][6];
        for (int i = 0; i < registrationItems.size(); i++) {
            RegistrationItem item = registrationItems.get(i);
            data[i][0] = item.getCourseCode(); // <--- Show Code
            data[i][1] = item.getSection();
            data[i][2] = item.getTitle();
            data[i][3] = item.getSchedule();
            data[i][4] = item.getRoom();
            data[i][5] = item.getStatus();
        }

        // ... (Keep the rest of the table setup code: JTable creation, listener, scroll pane update) ...
        JTable table = new JTable(data, columnNames);
        table.setDefaultEditor(Object.class, null);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        decorateTable(table);

        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
                selectedRegistrationSectionId = registrationItems.get(modelRow).getSectionId();
                btnDrop.setEnabled(true);
            } else {
                selectedRegistrationSectionId = -1;
                btnDrop.setEnabled(false);
            }
        });

        if (registrationsTableScrollPane == null) {
            registrationsTableScrollPane = new JScrollPane(table);
        } else {
            registrationsTableScrollPane.setViewportView(table);
        }
        btnDrop.setEnabled(false);
    }
}
