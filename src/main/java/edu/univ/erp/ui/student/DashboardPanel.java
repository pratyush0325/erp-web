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

        // 2. Show the requested card
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

        // Placeholder icons (use any)
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
        return root;
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
        JTextArea info = new JTextArea("Your registered sections by day/time.\n\n" +
                "Mon 10:00  CSE212-1  Data Structures (Lec)\n" +
                "Wed 10:00  CSE212-1  Data Structures (Lec)\n" +
                "Fri 14:00  CSE212-L1 Data Structures (Lab)");
        info.setEditable(false);
        info.setFont(new Font("Monospaced", Font.PLAIN, 13));
        root.add(new JScrollPane(info), BorderLayout.CENTER);
        return root;
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
        JTextArea info = new JTextArea("Completed courses and grades.\n" +
                "CSE101  Introduction to Programming   A\n" +
                "MTH100  Calculus I                    B+\n" +
                "\nExport options: CSV / PDF");
        info.setEditable(false);
        root.add(new JScrollPane(info), BorderLayout.CENTER);
        JButton exportCsv = new JButton("Export CSV");
        JButton exportPdf = new JButton("Export PDF");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actions.setOpaque(false);
        actions.add(exportCsv);
        actions.add(exportPdf);
        root.add(actions, BorderLayout.SOUTH);
        return root;
    }

    private JComponent buildProfilePage() {
        JPanel root = pageScaffold("Profile");
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;

        int r = 0;
        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Name"), gc);
        gc.gridx = 1; form.add(new JTextField("Student Name", 20), gc);
        r++;
        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Roll No"), gc);
        gc.gridx = 1; form.add(new JTextField("2025XXXX", 20), gc);
        r++;
        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Program"), gc);
        gc.gridx = 1; form.add(new JTextField("B.Tech CSE", 20), gc);
        r++;
        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Year"), gc);
        gc.gridx = 1; form.add(new JTextField("3", 20), gc);

        root.add(form, BorderLayout.NORTH);
        return root;
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
        // 1. Fetch data from the API
        registrationItems = studentApi.getMyRegistrations(); // studentApi field already exists

        // 2. Define columns
        String[] columnNames = {"Section", "Title", "Schedule", "Room", "Status"};

        // 3. Convert List to Object[][] for the JTable
        Object[][] data = new Object[registrationItems.size()][5];
        for (int i = 0; i < registrationItems.size(); i++) {
            RegistrationItem item = registrationItems.get(i);
            data[i][0] = item.getSection();
            data[i][1] = item.getTitle();
            data[i][2] = item.getSchedule();
            data[i][3] = item.getRoom();
            data[i][4] = item.getStatus();
        }

        // 4. Create the table
        JTable table = new JTable(data, columnNames);
        table.setDefaultEditor(Object.class, null);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        decorateTable(table);

        // 5. Add table listener to enable/disable the drop button
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
                // Get the hidden sectionId from our stored List
                selectedRegistrationSectionId = registrationItems.get(modelRow).getSectionId();
                btnDrop.setEnabled(true);
            } else {
                selectedRegistrationSectionId = -1;
                btnDrop.setEnabled(false);
            }
        });

        // 6. Update the ScrollPane's view
        if (registrationsTableScrollPane == null) {
            registrationsTableScrollPane = new JScrollPane(table);
        } else {
            registrationsTableScrollPane.setViewportView(table);
        }

        // Ensure button is disabled after refresh
        btnDrop.setEnabled(false);
    }
}
