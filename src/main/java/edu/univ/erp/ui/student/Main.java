package edu.univ.erp.ui.Student;

import com.formdev.flatlaf.FlatLightLaf;
import edu.univ.erp.ui.Student.DashboardPanel;
import edu.univ.erp.ui.Student.Sidebar.Sidebar;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(Main::launchUI);
    }

    private static void launchUI() {
        JFrame frame = new JFrame("University ERP — Student");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Center area with CardLayout pages
        DashboardPanel dashboard = new DashboardPanel();

        // Sidebar wires clicks to the center CardLayout
        Sidebar sidebar = new Sidebar(pageKey -> {
            if ("logout".equals(pageKey)) {
                int res = JOptionPane.showConfirmDialog(frame, "Log out now?", "Confirm Logout",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (res == JOptionPane.YES_OPTION) {
                    // TODO: route back to LoginFrame after clearing session
                    JOptionPane.showMessageDialog(frame, "Logged out (placeholder).");
                }
                return;
            }
            dashboard.showPage(pageKey);
        });

        frame.add(sidebar, BorderLayout.WEST);
        frame.add(dashboard, BorderLayout.CENTER);

        // Example: toggle maintenance mode banner (hook to Admin toggle later)
        dashboard.setMaintenanceMode(false);

        frame.setVisible(true);
    }
}
