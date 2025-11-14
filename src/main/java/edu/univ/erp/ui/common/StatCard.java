package edu.univ.erp.ui.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatCard extends JPanel {
    private final JLabel titleLabel = new JLabel();
    private final JLabel valueLabel = new JLabel();
    private final JLabel subLabel   = new JLabel();
    private final JLabel iconLabel  = new JLabel();

    public StatCard(String title, String value, String subtitle, Icon icon) {
        setOpaque(true);
        setBackground(ColorPalette.CARD_BG);
        setBorder(new EmptyBorder(16, 16, 16, 16));
        setLayout(new BorderLayout(12, 0));

        iconLabel.setIcon(icon);
        add(iconLabel, BorderLayout.WEST);

        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));

        titleLabel.setText(title);
        titleLabel.setFont(new Font("Raleway", Font.BOLD, 14));
        titleLabel.setForeground(ColorPalette.TEXT_MUTED);

        valueLabel.setText(value);
        valueLabel.setFont(new Font("Raleway", Font.BOLD, 28));
        valueLabel.setForeground(ColorPalette.TEXT_DARK);

        subLabel.setText(subtitle);
        subLabel.setFont(new Font("Raleway", Font.PLAIN, 12));
        subLabel.setForeground(ColorPalette.TEXT_MUTED);

        texts.add(titleLabel);
        texts.add(Box.createVerticalStrut(2));
        texts.add(valueLabel);
        texts.add(Box.createVerticalStrut(4));
        texts.add(subLabel);

        add(texts, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // soft rounded card
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc = 18;
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        g2.dispose();
        super.paintComponent(g);
    }

    public void setValue(String value) { valueLabel.setText(value); }
    public void setSubtitle(String text) { subLabel.setText(text); }
}
