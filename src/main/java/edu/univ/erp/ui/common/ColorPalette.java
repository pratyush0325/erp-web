package edu.univ.erp.ui.common;

import java.awt.*;

public final class ColorPalette {
    private ColorPalette() {}

    // Primary brand gradient (used in Sidebar)
    public static final Color PRIMARY_START = Color.decode("#00AEEF");
    public static final Color PRIMARY_END   = Color.decode("#004E92");

    // Backgrounds
    public static final Color APP_BG        = new Color(247, 249, 251);
    public static final Color CARD_BG       = Color.WHITE;

    // Text
    public static final Color TEXT_DARK     = new Color(36, 41, 47);
    public static final Color TEXT_MUTED    = new Color(110, 119, 129);
    public static final Color TEXT_LIGHT    = Color.WHITE;

    // Accents
    public static final Color ACCENT_OK     = new Color(0, 158, 115);
    public static final Color ACCENT_WARN   = new Color(252, 191, 73);
    public static final Color ACCENT_ERR    = new Color(222, 68, 55);

    // Effects
    public static final Color HOVER_OVERLAY = new Color(255, 255, 255, 30);
    public static final Color SELECT_OVERLAY= new Color(255, 255, 255, 50);
    public static final Color TRANSPARENT   = new Color(0, 0, 0, 0);

    // Maintenance banner
    public static final Color MAINT_BG      = new Color(255, 242, 204);
    public static final Color MAINT_TXT     = new Color(133, 77, 14);
}
