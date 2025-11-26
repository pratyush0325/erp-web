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
        // Refresh data when opening the users page
        if ("users".equals(key)) {
            refreshUserTable();
        }
    }

    // --- 1. USER MANAGEMENT UI ---

    private JTable userTable;
    private DefaultTableModel userTableModel;

    private JComponent buildUsers() {
        JPanel root = scaffold("Manage Users");

        // Table Setup
        String[] columns = {"ID", "Username", "Role", "Status"};
        userTableModel = new DefaultTableModel(columns, 0);
        userTable = new JTable(userTableModel);
        decorateTable(userTable);

        root.add(new JScrollPane(userTable), BorderLayout.CENTER);

        // Add User Button
        JButton addBtn = new JButton("Add New User");
        addBtn.addActionListener(e -> showAddUserDialog());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(addBtn);
        root.add(btnPanel, BorderLayout.SOUTH);

        // Load initial data
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

        // Form Fields
        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JComboBox<String> comboRole = new JComboBox<>(new String[]{"Student", "Instructor", "Admin"});

        // Dynamic Fields
        JLabel lblExtra1 = new JLabel("Roll No:");
        JTextField txtExtra1 = new JTextField();
        JLabel lblExtra2 = new JLabel("Year:");
        JTextField txtExtra2 = new JTextField();

        // Add components
        form.add(new JLabel("Username:")); form.add(txtUser);
        form.add(new JLabel("Password:")); form.add(txtPass);
        form.add(new JLabel("Role:"));     form.add(comboRole);
        form.add(lblExtra1);               form.add(txtExtra1);
        form.add(lblExtra2);               form.add(txtExtra2);

        // Role Logic
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
            } else { // Admin
                lblExtra1.setVisible(false); txtExtra1.setVisible(false);
                lblExtra2.setVisible(false); txtExtra2.setVisible(false);
            }
            form.revalidate();
            form.repaint();
        });

        // Trigger initial state
        comboRole.setSelectedIndex(0);

        JButton saveBtn = new JButton("Create User");
        saveBtn.addActionListener(e -> {
            String role = (String) comboRole.getSelectedItem();
            boolean success = adminApi.addUser(
                    txtUser.getText(),
                    new String(txtPass.getPassword()),
                    role.toLowerCase(), // Store as lowercase in DB
                    txtExtra1.getText(),
                    txtExtra2.getText()
            );

            if (success) {
                JOptionPane.showMessageDialog(dialog, "User created successfully!");
                dialog.dispose();
                refreshUserTable();
            } else {
                JOptionPane.showMessageDialog(dialog, "Error creating user. Check logs.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(saveBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // --- KEEP YOUR EXISTING METHODS FOR OTHER TABS ---

    private JComponent buildHome() {
        // ... (Keep existing code from previous turn)
        JPanel root = scaffold("Admin Dashboard");
        // ... (Keep the stat cards)
        return root;
    }

    private JComponent buildCourses() {
        // ... (Keep existing code or placeholder)
        JPanel root = scaffold("Manage Courses");
        // ...
        return root;
    }

    private JComponent buildSections() {
        // ... (Keep existing code or placeholder)
        JPanel root = scaffold("Manage Sections");
        // ...
        return root;
    }

    private JComponent buildMaintenance() {
        // ... (Keep existing code or placeholder)
        JPanel root = scaffold("Maintenance Mode");
        // ...
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