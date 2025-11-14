package edu.univ.erp.ui.instructor;

import com.formdev.flatlaf.FlatLightLaf;
import edu.univ.erp.api.auth.AuthApi; // <-- 1. ADD THIS IMPORT

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(Main::launchUI);
    }

    private static void launchUI() {
        JFrame frame = new JFrame("University ERP — Instructor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // <-- 2. CREATE AN AuthApi INSTANCE
        AuthApi authApi = new AuthApi();
        DashboardPanel dashboard = new DashboardPanel();

        Sidebar sidebar = new Sidebar(pageKey -> {
            if ("logout".equals(pageKey)) {
                // <-- 3. UPDATE LOGOUT LOGIC
                authApi.logout(); // Clear the session
                JOptionPane.showMessageDialog(frame, "Logging out...");
                frame.dispose(); // Close this dashboard
                edu.univ.erp.ui.login.Main.main(null); // Re-open login screen
                return;
            }
            dashboard.showPage(pageKey);
        });

        frame.add(sidebar, BorderLayout.WEST);
        frame.add(dashboard, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}