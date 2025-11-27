package edu.univ.erp.auth.store;

import edu.univ.erp.auth.UserAuth;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthStore {

    private String dbUrl = "jdbc:mysql://localhost:3306/auth_db";
    private String dbUser = "root";
    private String dbPassword = "prabhi12";

    /**
     * Fetches a user's auth details from the database.
     * @param username The username to look up.
     * @return A UserAuth object if found, otherwise null.
     */
    public UserAuth findUserByUsername(String username) {
        // Updated query to fetch new columns
        String query = "SELECT user_id, username, role, password_hash, failed_attempts, lockout_until FROM users_auth WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserAuth(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("role"),
                            rs.getString("password_hash"),
                            rs.getInt("failed_attempts"),    // <--- New
                            rs.getTimestamp("lockout_until") // <--- New
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- NEW HELPERS ---

    public void incrementFailedAttempts(int userId) {
        String query = "UPDATE users_auth SET failed_attempts = failed_attempts + 1 WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void resetFailedAttempts(int userId) {
        // Reset count AND clear lockout
        String query = "UPDATE users_auth SET failed_attempts = 0, lockout_until = NULL WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void lockAccount(int userId, int seconds) {
        // Set lockout time to NOW + X seconds
        String query = "UPDATE users_auth SET lockout_until = DATE_ADD(NOW(), INTERVAL ? SECOND) WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, seconds);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public UserAuth findUserById(int userId) {
        String query = "SELECT user_id, username, role, password_hash, failed_attempts, lockout_until FROM users_auth WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserAuth(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("role"),
                            rs.getString("password_hash"),
                            rs.getInt("failed_attempts"),
                            rs.getTimestamp("lockout_until")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ... existing code ...

    public boolean updatePassword(int userId, String newHash) {
        String query = "UPDATE users_auth SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newHash);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
