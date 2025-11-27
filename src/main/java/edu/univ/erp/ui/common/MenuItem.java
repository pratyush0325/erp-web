package edu.univ.erp.ui.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuItem {
    private final JPanel itemPanel;

    private final Color hoverColor    = ColorPalette.HOVER_OVERLAY;
    private final Color selectedColor = ColorPalette.SELECT_OVERLAY;

    // We need to store the selected state to help the mouse listener
    private boolean isSelected = false;

    public MenuItem(JPanel itemPanel) {
        this.itemPanel = itemPanel;

        // --- FIX 2: Add mouse listener ONLY ONE TIME in the constructor ---
        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Only show hover if NOT already selected
                if (!isSelected) {
                    itemPanel.setBackground(hoverColor);
                    itemPanel.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Only remove hover if NOT selected
                if (!isSelected) {
                    itemPanel.setBackground(ColorPalette.TRANSPARENT); // Set background back to transparent
                    itemPanel.repaint();
                }
            }
        });
    }

    // Add this method
    public JPanel getPanel() {
        return itemPanel;
    }

    // Add this method
    public void setSelected(boolean selected) {
        this.isSelected = selected;
        itemPanel.setBackground(selected ? selectedColor : ColorPalette.TRANSPARENT);

        // Find the text label (not the icon) and update its font
        for (Component c : itemPanel.getComponents()) {
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                if (!label.getFont().getFamily().equals("Segoe UI Emoji")) {
                    label.setFont(new Font("Raleway", selected ? Font.BOLD : Font.PLAIN, 15));
                }
            }
        }
        itemPanel.repaint();
    }

    public void addMenuItem(String text, boolean selected) {
        // Store the selected state
        this.isSelected = selected;

        // --- FIX: Always keep it false so the parent gradient paints first ---
        itemPanel.setOpaque(false);
        // --------------------------------------------------------------------

        itemPanel.setBackground(selected ? selectedColor : ColorPalette.TRANSPARENT);
        itemPanel.setMaximumSize(new Dimension(240, 44));
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        itemPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel menuLabel = new JLabel(text);
        menuLabel.setFont(new Font("Raleway", selected ? Font.BOLD : Font.PLAIN, 15));
        menuLabel.setForeground(Color.WHITE);

        itemPanel.removeAll();
        itemPanel.add(menuLabel);

        itemPanel.revalidate();
        itemPanel.repaint();
    }
}