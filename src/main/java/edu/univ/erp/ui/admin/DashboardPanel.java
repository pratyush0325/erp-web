package edu.univ.erp.ui.admin;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.domain.UserAdminItem;
import edu.univ.erp.ui.common.ColorPalette;
import edu.univ.erp.ui.common.StatCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    // API Instance
    private final AdminApi adminApi = new AdminApi();

    // Table Models
    private DefaultTableModel userTableModel;
    private DefaultTableModel courseModel;
    private DefaultTableModel sectionModel;

    // Tables
    private JTable userTable;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(ColorPalette.APP_BG);

        // Add all pages
        cards.add(buildHome(), "home");
        cards.add(buildUsers(), "users");
        cards.add(buildCourses(), "courses");
        cards.add(buildSections(), "sections");
        cards.add(buildMaintenance(), "maintenance");

        add(cards, BorderLayout.CENTER);
    }

    public void showPage(String key) {
        cardLayout.show(cards, key);
        // Refresh data when opening specific tabs
        if ("users".equals(key)) refreshUserTable();
        if ("courses".equals(key)) refreshCourseTable();
        if ("sections".equals(key)) refreshSectionTable();
    }

    // ---------------------------------------------------------
    // 1. HOME / DASHBOARD
    // ---------------------------------------------------------
    private JComponent buildHome() {
        JPanel root = scaffold("Admin Dashboard");
        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);
        Icon i1 = UIManager.getIcon("OptionPane.informationIcon");
        Icon i2 = UIManager.getIcon("OptionPane.warningIcon");
        Icon i3 = UIManager.getIcon("OptionPane.questionIcon");
        Icon i4 = UIManager.getIcon("OptionPane.errorIcon");

        // Note: These are currently static stats. You can make them dynamic later if needed.
        grid.add(new StatCard("Total Users", "12", "Admins + Instructors + Students", i1));
        grid.add(new StatCard("Courses", "30", "Across all departments", i2));
        grid.add(new StatCard("Sections", "45", "Current semester", i3));
        grid.add(new StatCard("Maintenance", "OFF", "Toggle available", i4));
        root.add(grid, BorderLayout.CENTER);
        return root;
    }

    // ---------------------------------------------------------
    // 2. USER MANAGEMENT
    // ---------------------------------------------------------
    private JComponent buildUsers() {
        JPanel root = scaffold("Manage Users");

        String[] columns = {"ID", "Username", "Role", "Status"};
        userTableModel = new DefaultTableModel(columns, 0);
        userTable = new JTable(userTableModel);
        decorateTable(userTable);

        root.add(new JScrollPane(userTable), BorderLayout.CENTER);

        JButton addBtn = new JButton("Add New User");
        addBtn.addActionListener(e -> showAddUserDialog());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(addBtn);
        root.add(btnPanel, BorderLayout.SOUTH);

        refreshUserTable();
        return root;
    }

    private void refreshUserTable() {
        if (userTableModel == null) return;
        userTableModel.setRowCount(0);
        List<UserAdminItem> users = adminApi.getUsers();
        for (UserAdminItem u : users) {
            userTableModel.addRow(new Object[]{u.getUserId(), u.getUsername(), u.getRole(), u.getStatus()});
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New User", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JComboBox<String> comboRole = new JComboBox<>(new String[]{"Student", "Instructor", "Admin"});
        JLabel lblExtra1 = new JLabel("Roll No:");
        JTextField txtExtra1 = new JTextField();
        JLabel lblExtra2 = new JLabel("Year:");
        JTextField txtExtra2 = new JTextField();

        form.add(new JLabel("Username:")); form.add(txtUser);
        form.add(new JLabel("Password:")); form.add(txtPass);
        form.add(new JLabel("Role:"));     form.add(comboRole);
        form.add(lblExtra1);               form.add(txtExtra1);
        form.add(lblExtra2);               form.add(txtExtra2);

        comboRole.addActionListener(e -> {
            String role = (String) comboRole.getSelectedItem();
            if ("Student".equals(role)) {
                lblExtra1.setText("Roll No:");
                lblExtra1.setVisible(true); txtExtra1.setVisible(true);
                lblExtra2.setText("Year:");
                lblExtra2.setVisible(true); txtExtra2.setVisible(true);
            } else if ("Instructor".equals(role)) {
                lblExtra1.setText("Department:");
                lblExtra1.setVisible(true); txtExtra1.setVisible(true);
                lblExtra2.setVisible(false); txtExtra2.setVisible(false);
            } else {
                lblExtra1.setVisible(false); txtExtra1.setVisible(false);
                lblExtra2.setVisible(false); txtExtra2.setVisible(false);
            }
            form.revalidate();
            form.repaint();
        });
        comboRole.setSelectedIndex(0);

        JButton saveBtn = new JButton("Create User");
        // Background thread to prevent UI freezing
        saveBtn.addActionListener(e -> {
            saveBtn.setEnabled(false);
            saveBtn.setText("Creating...");
            String uName = txtUser.getText();
            String pWord = new String(txtPass.getPassword());
            String roleVal = (String) comboRole.getSelectedItem();
            String e1 = txtExtra1.getText();
            String e2 = txtExtra2.getText();

            new SwingWorker<Boolean, Void>() {
                @Override protected Boolean doInBackground() {
                    return adminApi.addUser(uName, pWord, roleVal.toLowerCase(), e1, e2);
                }
                @Override protected void done() {
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(dialog, "User created successfully!");
                            dialog.dispose();
                            refreshUserTable();
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Error creating user.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) { ex.printStackTrace(); }
                    if (dialog.isVisible()) { saveBtn.setEnabled(true); saveBtn.setText("Create User"); }
                }
            }.execute();
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(saveBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ---------------------------------------------------------
    // 3. MANAGE COURSES
    // ---------------------------------------------------------
    private JComponent buildCourses() {
        JPanel root = scaffold("Manage Courses");

        String[] cols = {"Code", "Title", "Credits"};
        courseModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(courseModel);
        decorateTable(table);

        JButton btnAdd = new JButton("Create Course");
        btnAdd.addActionListener(e -> showAddCourseDialog());

        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnAdd);
        root.add(btnPanel, BorderLayout.SOUTH);

        refreshCourseTable();
        return root;
    }

    private void refreshCourseTable() {
        if (courseModel == null) return;
        courseModel.setRowCount(0);
        List<edu.univ.erp.domain.Course> list = adminApi.getCourses();
        for (edu.univ.erp.domain.Course c : list) {
            courseModel.addRow(new Object[]{c.getCode(), c.getTitle(), c.getCredits()});
        }
    }

    private void showAddCourseDialog() {
        JTextField txtCode = new JTextField();
        JTextField txtTitle = new JTextField();
        JTextField txtCredits = new JTextField("3");

        Object[] message = {
                "Course Code (e.g. CS101):", txtCode,
                "Course Title:", txtTitle,
                "Credits:", txtCredits
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Course", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int credits = Integer.parseInt(txtCredits.getText());
                boolean ok = adminApi.addCourse(txtCode.getText(), txtTitle.getText(), credits);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Course Added!");
                    refreshCourseTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Error adding course (Duplicate code?)");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid credits.");
            }
        }
    }

    // ---------------------------------------------------------
    // 4. MANAGE SECTIONS
    // ---------------------------------------------------------
    private JComponent buildSections() {
        JPanel root = scaffold("Manage Sections");

        String[] cols = {"ID", "Course", "Instructor", "Schedule", "Capacity"};
        sectionModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(sectionModel);
        decorateTable(table);

        JButton btnAdd = new JButton("Schedule Section");
        btnAdd.addActionListener(e -> showAddSectionDialog());

        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnAdd);
        root.add(btnPanel, BorderLayout.SOUTH);

        refreshSectionTable();
        return root;
    }

    private void refreshSectionTable() {
        if (sectionModel == null) return;
        sectionModel.setRowCount(0);
        List<edu.univ.erp.domain.SectionAdminItem> list = adminApi.getSections();
        for (edu.univ.erp.domain.SectionAdminItem s : list) {
            sectionModel.addRow(new Object[]{
                    s.getSectionId(), s.getCourseCode(), s.getInstructorName(), s.getSchedule(), s.getCapacity()
            });
        }
    }

    private void showAddSectionDialog() {
        List<edu.univ.erp.domain.Course> courses = adminApi.getCourses();
        java.util.Map<Integer, String> instructors = adminApi.getInstructors();

        if (courses.isEmpty() || instructors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please ensure you have Courses and Instructors created first.");
            return;
        }

        JComboBox<edu.univ.erp.domain.Course> comboCourse = new JComboBox<>(courses.toArray(new edu.univ.erp.domain.Course[0]));

        class InstructorOption {
            final int id; final String name;
            InstructorOption(int id, String name) { this.id = id; this.name = name; }
            public String toString() { return name; }
        }

        JComboBox<InstructorOption> comboInst = new JComboBox<>();
        instructors.forEach((id, name) -> comboInst.addItem(new InstructorOption(id, name)));

        JTextField txtTime = new JTextField("Mon 10:00");
        JTextField txtRoom = new JTextField("101");
        JTextField txtCap = new JTextField("60");

        Object[] msg = {
                "Course:", comboCourse,
                "Instructor:", comboInst,
                "Day/Time:", txtTime,
                "Room:", txtRoom,
                "Capacity:", txtCap
        };

        int opt = JOptionPane.showConfirmDialog(this, msg, "Schedule Section", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                edu.univ.erp.domain.Course c = (edu.univ.erp.domain.Course) comboCourse.getSelectedItem();
                InstructorOption i = (InstructorOption) comboInst.getSelectedItem();
                int cap = Integer.parseInt(txtCap.getText());

                boolean ok = adminApi.addSection(c.getCode(), i.id, txtTime.getText(), txtRoom.getText(), cap);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Section Scheduled!");
                    refreshSectionTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Error scheduling section.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        }
    }

    // ---------------------------------------------------------
    // 5. MAINTENANCE
    // ---------------------------------------------------------
    private JComponent buildMaintenance() {
        JPanel root = scaffold("Maintenance Mode");

        // 1. Create Checkbox
        JCheckBox toggle = new JCheckBox("Enable Maintenance Mode");
        toggle.setFont(new Font("Raleway", Font.BOLD, 16));
        toggle.setOpaque(false);

        // 2. Set initial state from DB
        toggle.setSelected(adminApi.isMaintenanceOn());

        // 3. Add Listener
        toggle.addActionListener(e -> {
            boolean isSelected = toggle.isSelected();
            adminApi.setMaintenance(isSelected);

            String status = isSelected ? "ON" : "OFF";
            JOptionPane.showMessageDialog(this, "Maintenance Mode is now " + status);
        });

        // Layout
        JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT));
        content.setOpaque(false);
        content.add(toggle);

        JTextArea hint = new JTextArea("When enabled:\n- Students cannot register/drop.\n- Instructors cannot edit grades.\n- A banner will be shown to all users.");
        hint.setEditable(false);
        hint.setOpaque(false);
        hint.setBorder(new EmptyBorder(10, 10, 10, 10));
        hint.setForeground(Color.DARK_GRAY);

        root.add(content, BorderLayout.CENTER);
        root.add(hint, BorderLayout.SOUTH);
        return root;
    }

    // ---------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------
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