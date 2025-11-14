package edu.univ.erp.ui.login;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main extends JFrame {

    public Main() {
        setTitle("University");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        PanelLogin loginPanel = new PanelLogin();
        setLocationRelativeTo(null);
        add(loginPanel);
//        setLayout(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.anchor = GridBagConstraints.CENTER;
//        add(loginPanel, gbc);
        pack();

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}