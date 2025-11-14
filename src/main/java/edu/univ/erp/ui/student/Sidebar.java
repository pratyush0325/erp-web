package edu.univ.erp.ui.common;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Sidebar extends JPanel {
    private final List<MenuItem> menuItems = new ArrayList<>();

    public Sidebar() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(32, 34, 37));
        setPreferredSize(new Dimension(240, 600));

        addMenuItem("Dashboard", "🏠");
        addMenuItem("Instructor", "👨‍🏫");
        addMenuItem("Admin", "🛠️");
        addMenuItem("Settings", "⚙️");
    }

    private void addMenuItem(String text, String icon) {
        MenuItem item = new MenuItem(text, icon);
        menuItems.add(item);
        add(item.getPanel());

        item.setOnClick(() -> {
            for (MenuItem m : menuItems) m.setSelected(m == item);
            System.out.println("Selected: " + text);
            // Switch dashboard content here (CardLayout / JPanel swap)
        });
    }
}
