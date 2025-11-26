package edu.univ.erp.data;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class AdminStore {
    private String dbUrlAuth = "jdbc:mysql://localhost:3306/auth_db";
    private String dbUrlErp = "jdbc:mysql://localhost:3306/erp_db";
    private String dbUser = "root";
    private String dbPassword = "prabhi12";

    /**
     * Adds a user to Auth DB and the specific Role table in ERP DB.
     * Transactional: if one fails, both fail.
     */
    public boolean addUser(String username, String rawPassword, String role,
                           String extra1, String extra2) {
        Connection connAuth = null;
        Connection connErp = null;

        try {
            connAuth = DriverManager.getConnection(dbUrlAuth, dbUser, dbPassword);
            connErp = DriverManager.getConnection(dbUrlErp, dbUser, dbPassword);

            // Start transaction
            connAuth.setAutoCommit(false);
            connErp.setAutoCommit(false);

            // 1. Insert into Auth DB
            String authSql = "INSERT INTO users_auth (username, role, password_hash, status) VALUES (?, ?, ?, 'Active')";
            int userId = -1;

            try (PreparedStatement stmt = connAuth.prepareStatement(authSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, username);
                stmt.setString(2, role);
                stmt.setString(3, BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) userId = rs.getInt(1);
                }
            }

            if (userId == -1) throw new SQLException("Failed to create auth user");

            // 2. Insert into ERP DB based on role
            if ("student".equalsIgnoreCase(role)) {
                String stuSql = "INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = connErp.prepareStatement(stuSql)) {
                    stmt.setInt(1, userId);
                    stmt.setString(2, extra1); // Roll No
                    stmt.setString(3, "General"); // Default Program
                    stmt.setInt(4, Integer.parseInt(extra2)); // Year
                    stmt.executeUpdate();
                }
            } else if ("instructor".equalsIgnoreCase(role)) {
                String instSql = "INSERT INTO instructors (user_id, department) VALUES (?, ?)";
                try (PreparedStatement stmt = connErp.prepareStatement(instSql)) {
                    stmt.setInt(1, userId);
                    stmt.setString(2, extra1); // Department
                    stmt.executeUpdate();
                }
            }

            // Commit both
            connAuth.commit();
            connErp.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            // Rollback on error
            try { if (connAuth != null) connAuth.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            try { if (connErp != null) connErp.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { if (connAuth != null) connAuth.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            try { if (connErp != null) connErp.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    /**
     * Fetches all users from the auth database for the admin list.
     */
    public java.util.List<edu.univ.erp.domain.UserAdminItem> getAllUsers() {
        java.util.List<edu.univ.erp.domain.UserAdminItem> users = new java.util.ArrayList<>();
        String query = "SELECT user_id, username, role, status FROM users_auth ORDER BY user_id DESC";

        try (Connection conn = DriverManager.getConnection(dbUrlAuth, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(new edu.univ.erp.domain.UserAdminItem(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}