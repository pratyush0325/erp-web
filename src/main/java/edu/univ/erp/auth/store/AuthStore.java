package edu.univ.erp.auth.store;

import edu.univ.erp.auth.UserAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class AuthStore {

    private static final Logger log = LoggerFactory.getLogger(AuthStore.class);

    private final DataSource dataSource;

    public AuthStore(@Qualifier("authDataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public UserAuth findUserByUsername(String username) {
        String query = "SELECT user_id, username, role, password_hash, failed_attempts, lockout_until, status FROM users_auth WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserAuth(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("role"),
                            rs.getString("password_hash"),
                            rs.getInt("failed_attempts"),
                            rs.getTimestamp("lockout_until"),
                            rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            log.error("Failed to find user by username: {}", username, e);
        }
        return null;
    }

    public void incrementFailedAttempts(int userId) {
        String query = "UPDATE users_auth SET failed_attempts = failed_attempts + 1 WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to increment failed attempts for userId={}", userId, e);
        }
    }

    public void resetFailedAttempts(int userId) {
        String query = "UPDATE users_auth SET failed_attempts = 0, lockout_until = NULL WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to reset failed attempts for userId={}", userId, e);
        }
    }

    public void lockAccount(int userId, int seconds) {
        String query = "UPDATE users_auth SET lockout_until = DATE_ADD(NOW(), INTERVAL ? SECOND) WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, seconds);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to lock account for userId={}", userId, e);
        }
    }

    public UserAuth findUserById(int userId) {
        String query = "SELECT user_id, username, role, password_hash, failed_attempts, lockout_until, status FROM users_auth WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
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
                            rs.getTimestamp("lockout_until"),
                            rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            log.error("Failed to find user by id={}", userId, e);
        }
        return null;
    }

    public boolean updatePassword(int userId, String newHash) {
        String query = "UPDATE users_auth SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newHash);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Failed to update password for userId={}", userId, e);
            return false;
        }
    }
}
