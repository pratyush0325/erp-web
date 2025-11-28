package edu.univ.erp.ui.login;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main extends JFrame {

    public Main() {
        setTitle("University ERP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PanelLogin loginPanel = new PanelLogin();
        add(loginPanel);

        // 1. Calculate the size of the window based on components
        pack();

        // 2. NOW center it (must be done after pack)
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}