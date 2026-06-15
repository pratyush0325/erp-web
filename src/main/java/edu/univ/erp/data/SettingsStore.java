package edu.univ.erp.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Repository
public class SettingsStore {

    private static final Logger log = LoggerFactory.getLogger(SettingsStore.class);

    private final DataSource dataSource;

    public SettingsStore(@Qualifier("erpDataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isMaintenanceMode() {
        String query = "SELECT settings_value FROM settings WHERE settings_key = 'maintenance_on'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return "true".equalsIgnoreCase(rs.getString("settings_value"));
        } catch (SQLException e) {
            log.error("Failed to read maintenance mode setting", e);
        }
        return false;
    }

    public void setMaintenanceMode(boolean enabled) {
        String query = "UPDATE settings SET settings_value = ? WHERE settings_key = 'maintenance_on'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, enabled ? "true" : "false");
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to set maintenance mode to {}", enabled, e);
        }
    }

    public LocalDate getRegistrationDeadline() {
        String query = "SELECT settings_value FROM settings WHERE settings_key = 'registration_deadline'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                String dateStr = rs.getString("settings_value");
                if (dateStr != null && !dateStr.isEmpty()) return LocalDate.parse(dateStr);
            }
        } catch (Exception e) {
            log.error("Failed to read registration deadline", e);
        }
        return null;
    }

    public void setRegistrationDeadline(String dateStr) {
        String query = "INSERT INTO settings (settings_key, settings_value) VALUES ('registration_deadline', ?) " +
                "ON DUPLICATE KEY UPDATE settings_value = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, dateStr);
            stmt.setString(2, dateStr);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to set registration deadline to {}", dateStr, e);
        }
    }
}
