package edu.univ.erp.ui.login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class UserNameField extends JTextField {
    private String hint = "";

    public UserNameField() {
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setFont(new Font("sansserif", Font.PLAIN, 13));

        // --- FIX: Change text color to Black (was #BF38B) ---
        setForeground(Color.BLACK);
        // ----------------------------------------------------

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Only paint hint if empty and NOT focused
        if (hint != null && !hint.isEmpty() && getText().isEmpty() && !isFocusOwner()) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Hint color (Gray)
            g2.setColor(Color.GRAY);

            Insets insets = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(hint, insets.left, y);
        }
    }
}