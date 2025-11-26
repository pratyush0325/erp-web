package edu.univ.erp.data;

import java.sql.*;

public class SettingsStore {
    private String dbUrl = "jdbc:mysql://localhost:3306/erp_db";
    private String dbUser = "root";
    private String dbPassword = "prabhi12";

    public boolean isMaintenanceMode() {
        String query = "SELECT settings_value FROM settings WHERE settings_key = 'maintenance_on'";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return "true".equalsIgnoreCase(rs.getString("settings_value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to off if error or not found
    }

    public void setMaintenanceMode(boolean enabled) {
        String query = "UPDATE settings SET settings_value = ? WHERE settings_key = 'maintenance_on'";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, enabled ? "true" : "false");
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}