package edu.univ.erp.ui.instructor;

import edu.univ.erp.ui.common.ColorPalette;
import edu.univ.erp.ui.common.MenuItem;

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
    private final Map<String, JPanel> itemPanels = new LinkedHashMap<>();
    private String selectedKey = "home";

    public Sidebar(NavController controller) {
        this.controller = controller;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(260, 800));
        setBorder(new EmptyBorder(28, 16, 28, 16));
        setOpaque(false);

        JLabel appName = new JLabel("Instructor Panel");
        appName.setFont(new Font("Raleway SemiBold", Font.BOLD, 22));
        appName.setForeground(Color.WHITE);
        appName.setBorder(new EmptyBorder(0, 8, 20, 0));
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(appName);

        addSection("Navigation");

        addItem("home", "🏠", "Dashboard", true);
        addItem("sections", "📘", "My Sections", false);
        addItem("grades", "🧾", "Gradebook", false);
        addItem("stats", "📊", "Statistics", false);

        add(Box.createVerticalGlue());
        addSection("Account");
        addItem("logout", "🚪", "Logout", false);
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

    private void addItem(String key, String icon, String text, boolean selected) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        MenuItem builder = new MenuItem(itemPanel);
        builder.addMenuItem(text, icon, selected);

        itemPanels.put(key, itemPanel);
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(itemPanel);

        itemPanel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (controller != null) controller.showPage(key);
                setSelected(key);
            }
        });
    }

    private void setSelected(String key) {
        selectedKey = key;
        for (Map.Entry<String, JPanel> entry : itemPanels.entrySet()) {
            boolean isSel = entry.getKey().equals(selectedKey);
            JPanel p = entry.getValue();
            p.setOpaque(isSel);
            p.setBackground(isSel ? ColorPalette.SELECT_OVERLAY : ColorPalette.TRANSPARENT);
            p.repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        GradientPaint gp = new GradientPaint(0, 0, ColorPalette.PRIMARY_START, 0, getHeight(), ColorPalette.PRIMARY_END);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
