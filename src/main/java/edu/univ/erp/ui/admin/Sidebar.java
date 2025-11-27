package edu.univ.erp.ui.admin;

import edu.univ.erp.ui.common.MenuItem;
import edu.univ.erp.ui.common.ColorPalette;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class Sidebar extends JPanel {

    public interface NavController {
        void showPage(String key);
    }

    private final NavController controller;
    // FIX: Change Map to store MenuItem, not JPanel
    private final Map<String, MenuItem> itemPanels = new LinkedHashMap<>();
    private String selectedKey = "home";

    public Sidebar(NavController controller) {
        this.controller = controller;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(260, 800));
        setBorder(new EmptyBorder(28, 16, 28, 16));
        setOpaque(false);

        JLabel appName = new JLabel("Admin Panel");
        appName.setFont(new Font("Raleway SemiBold", Font.BOLD, 22));
        appName.setForeground(Color.WHITE);
        appName.setBorder(new EmptyBorder(0, 8, 20, 0));
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(appName);

//        addSection("Navigation");
        addItem("home",  "Dashboard", true);
        addItem("users",  "Manage Users", false);
        addItem("courses",  "Manage Courses", false);
        addItem("sections",  "Manage Sections", false);
        addItem("maintenance",  "Maintenance Mode", false);

        add(Box.createVerticalGlue());
//        addSection("Account");
        addItem("logout", "Logout", false);
    }

    private void addSection(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Raleway", Font.BOLD, 13));
        label.setForeground(new Color(235, 245, 255));
        label.setBorder(new EmptyBorder(10, 8, 10, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(label);
        add(Box.createVerticalStrut(6));
    }

    private void addItem(String key, String text, boolean selected) {
        // --- FIX: Custom JPanel that clears artifacts ---
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                // 1. Clear the area first to prevent "ghosting"
                super.paintComponent(g);
                // 2. Paint our custom background (white overlay)
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // This MUST be false so the gradient shows through
        itemPanel.setOpaque(false);

        MenuItem builder = new MenuItem(itemPanel);
        builder.addMenuItem(text, selected);

        // Store the MenuItem wrapper
        itemPanels.put(key, builder);

        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(itemPanel);

        itemPanel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (controller != null) controller.showPage(key);
                setSelected(key);
                // Force redraw to clear any artifacts
                revalidate();
                repaint();
            }
        });

        if (selected) selectedKey = key;
    }

    private void setSelected(String key) {
        selectedKey = key;
        // FIX: Update using MenuItem methods
        for (Map.Entry<String, MenuItem> entry : itemPanels.entrySet()) {
            boolean isSel = entry.getKey().equals(selectedKey);
            entry.getValue().setSelected(isSel);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Paint Gradient
        Graphics2D g2 = (Graphics2D) g.create();
        GradientPaint gp = new GradientPaint(0, 0, ColorPalette.PRIMARY_START, 0, getHeight(), ColorPalette.PRIMARY_END);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();

        // Paint children on top
        super.paintComponent(g);
    }
}