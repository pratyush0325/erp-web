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
        String query = "SELECT user_id, username, role, password_hash FROM users_auth WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserAuth(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("role"),
                            rs.getString("password_hash")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // In a real app, you'd log this error
        }
        return null; // User not found or DB error
    }
}