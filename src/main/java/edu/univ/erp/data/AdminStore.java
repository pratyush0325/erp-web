package edu.univ.erp.data;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class AdminStore {
    private String dbUrlAuth = "jdbc:mysql://localhost:3306/auth_db";
    private String dbUrlErp = "jdbc:mysql://localhost:3306/erp_db";
    private String dbUser = "root";
    private String dbPassword = "prabhi12";

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

    public boolean addUser(String username, String rawPassword, String role,
                           String extra1, String extra2) {
        Connection connAuth = null;
        Connection connErp = null;
        int createdUserId = -1;

        try {
            // 1. Connect and Create User in Auth DB
            connAuth = DriverManager.getConnection(dbUrlAuth, dbUser, dbPassword);
            connAuth.setAutoCommit(false); // Start Transaction A

            String authSql = "INSERT INTO users_auth (username, role, password_hash, status) VALUES (?, ?, ?, 'Active')";

            try (PreparedStatement stmt = connAuth.prepareStatement(authSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, username);
                stmt.setString(2, role);
                stmt.setString(3, BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) createdUserId = rs.getInt(1);
                }
            }

            if (createdUserId == -1) throw new SQLException("Failed to get new User ID");

            // CRITICAL FIX: Commit Auth Transaction NOW so ERP DB can see the user
            connAuth.commit();
            // -------------------------------------------------------------------

            // 2. Connect and Create Profile in ERP DB
            connErp = DriverManager.getConnection(dbUrlErp, dbUser, dbPassword);
            connErp.setAutoCommit(false); // Start Transaction B

            if ("student".equalsIgnoreCase(role)) {
                String stuSql = "INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = connErp.prepareStatement(stuSql)) {
                    stmt.setInt(1, createdUserId);
                    stmt.setString(2, extra1);
                    stmt.setString(3, "General");
                    stmt.setInt(4, Integer.parseInt(extra2));
                    stmt.executeUpdate();
                }
            } else if ("instructor".equalsIgnoreCase(role)) {
                String instSql = "INSERT INTO instructors (user_id, department) VALUES (?, ?)";
                try (PreparedStatement stmt = connErp.prepareStatement(instSql)) {
                    stmt.setInt(1, createdUserId);
                    stmt.setString(2, extra1);
                    stmt.executeUpdate();
                }
            }

            // Commit ERP Transaction
            connErp.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();

            // ROLLBACK LOGIC
            // 1. If ERP failed, rollback ERP transaction
            try { if (connErp != null) connErp.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }

            // 2. If Auth was already committed but ERP failed, we must manually delete the orphan user
            if (createdUserId != -1 && connAuth != null) {
                try {
                    // Re-enable auto-commit to execute a quick delete
                    connAuth.setAutoCommit(true);
                    String cleanupSql = "DELETE FROM users_auth WHERE user_id = ?";
                    try (PreparedStatement stmt = connAuth.prepareStatement(cleanupSql)) {
                        stmt.setInt(1, createdUserId);
                        stmt.executeUpdate();
                        System.out.println("DEBUG: Cleaned up orphan user " + createdUserId);
                    }
                } catch (SQLException ex) {
                    System.err.println("CRITICAL: Failed to clean up user " + createdUserId);
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            try { if (connAuth != null) connAuth.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            try { if (connErp != null) connErp.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    // --- COURSE MANAGEMENT ---

    public java.util.List<edu.univ.erp.domain.Course> getAllCourses() {
        java.util.List<edu.univ.erp.domain.Course> list = new java.util.ArrayList<>();
        String query = "SELECT code, title, credits FROM courses ORDER BY code";
        try (Connection conn = DriverManager.getConnection(dbUrlErp, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new edu.univ.erp.domain.Course(
                        rs.getString("code"), rs.getString("title"), rs.getInt("credits")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addCourse(String code, String title, int credits) {
        String query = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbUrlErp, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
            stmt.setString(2, title);
            stmt.setInt(3, credits);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- SECTION MANAGEMENT ---

    public java.util.List<edu.univ.erp.domain.SectionAdminItem> getAllSections() {
        java.util.List<edu.univ.erp.domain.SectionAdminItem> list = new java.util.ArrayList<>();
        // Join with users_auth to get the instructor's actual username/name
        String query = "SELECT s.section_id, s.course_id, u.username, s.day_time, s.capacity " +
                "FROM sections s " +
                "JOIN instructors i ON s.instructor_id = i.user_id " +
                "JOIN auth_db.users_auth u ON i.user_id = u.user_id " +
                "ORDER BY s.course_id, s.section_id";

        try (Connection conn = DriverManager.getConnection(dbUrlErp, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new edu.univ.erp.domain.SectionAdminItem(
                        rs.getInt("section_id"),
                        rs.getString("course_id"),
                        rs.getString("username"),
                        rs.getString("day_time"),
                        rs.getInt("capacity")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addSection(String courseId, int instructorId, String dayTime, String room, int capacity) {
        String query = "INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) " +
                "VALUES (?, ?, ?, ?, ?, 'Fall', 2025)";
        try (Connection conn = DriverManager.getConnection(dbUrlErp, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseId);
            stmt.setInt(2, instructorId);
            stmt.setString(3, dayTime);
            stmt.setString(4, room);
            stmt.setInt(5, capacity);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper to populate Instructor dropdown
    public java.util.Map<Integer, String> getInstructorsMap() {
        java.util.Map<Integer, String> map = new java.util.LinkedHashMap<>();
        String query = "SELECT u.user_id, u.username FROM auth_db.users_auth u WHERE u.role = 'instructor'";
        try (Connection conn = DriverManager.getConnection(dbUrlAuth, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while(rs.next()) {
                map.put(rs.getInt("user_id"), rs.getString("username"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }
}