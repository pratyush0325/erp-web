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
                           String extra1, String extra2, String extra3) { // <--- Added extra3
        Connection connAuth = null;
        Connection connErp = null;
        int createdUserId = -1;

        try {
            // 1. Connect and Create User in Auth DB
            connAuth = DriverManager.getConnection(dbUrlAuth, dbUser, dbPassword);
            connAuth.setAutoCommit(false);

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

            connAuth.commit(); // Commit Auth User

            // 2. Connect and Create Profile in ERP DB
            connErp = DriverManager.getConnection(dbUrlErp, dbUser, dbPassword);
            connErp.setAutoCommit(false);

            if ("student".equalsIgnoreCase(role)) {
                String stuSql = "INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = connErp.prepareStatement(stuSql)) {
                    stmt.setInt(1, createdUserId);
                    stmt.setString(2, extra1); // Roll No
                    stmt.setString(3, extra3); // Program (Passed from UI)
                    stmt.setInt(4, Integer.parseInt(extra2)); // Year
                    stmt.executeUpdate();
                }
            } else if ("instructor".equalsIgnoreCase(role)) {
                String instSql = "INSERT INTO instructors (user_id, department) VALUES (?, ?)";
                try (PreparedStatement stmt = connErp.prepareStatement(instSql)) {
                    stmt.setInt(1, createdUserId);
                    stmt.setString(2, extra1); // Department
                    stmt.executeUpdate();
                }
            }

            connErp.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try { if (connErp != null) connErp.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }

            // Clean up orphan auth user if ERP failed
            if (createdUserId != -1 && connAuth != null) {
                try {
                    connAuth.setAutoCommit(true);
                    String cleanupSql = "DELETE FROM users_auth WHERE user_id = ?";
                    try (PreparedStatement stmt = connAuth.prepareStatement(cleanupSql)) {
                        stmt.setInt(1, createdUserId);
                        stmt.executeUpdate();
                    }
                } catch (SQLException ex) { ex.printStackTrace(); }
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

    // ... inside AdminStore class ...

    public java.util.List<edu.univ.erp.domain.SectionAdminItem> getAllSections() {
        java.util.List<edu.univ.erp.domain.SectionAdminItem> list = new java.util.ArrayList<>();

        // UPDATED QUERY: Added s.semester, s.year
        String query = "SELECT s.section_id, s.course_id, u.username, s.day_time, s.capacity, s.semester, s.year " +
                "FROM sections s " +
                "JOIN instructors i ON s.instructor_id = i.user_id " +
                "JOIN auth_db.users_auth u ON i.user_id = u.user_id " +
                "ORDER BY s.year DESC, s.semester, s.course_id"; // Better sorting

        try (Connection conn = DriverManager.getConnection(dbUrlErp, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new edu.univ.erp.domain.SectionAdminItem(
                        rs.getInt("section_id"),
                        rs.getString("course_id"),
                        rs.getString("username"),
                        rs.getString("day_time"),
                        rs.getInt("capacity"),
                        rs.getString("semester"), // <--- Pass to constructor
                        rs.getInt("year")         // <--- Pass to constructor
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // UPDATED: Accepts semester and year
    public boolean addSection(String courseId, int instructorId, String dayTime, String room, int capacity, String semester, int year) {
        String query = "INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbUrlErp, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseId);
            stmt.setInt(2, instructorId);
            stmt.setString(3, dayTime);
            stmt.setString(4, room);
            stmt.setInt(5, capacity);
            stmt.setString(6, semester); // <--- Set Semester
            stmt.setInt(7, year);        // <--- Set Year
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserStatus(int userId, String newStatus) {
        String query = "UPDATE users_auth SET status = ? WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrlAuth, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, userId);
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

    /**
     * Fetches counts for the Admin Dashboard.
     */
    public edu.univ.erp.domain.AdminStats getDashboardStats() {
        int users = 0;
        int courses = 0;
        int sections = 0;
        boolean maint = false;

        // 1. Count Users
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(dbUrlAuth, dbUser, dbPassword);
             java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM users_auth")) {
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) users = rs.getInt(1);
            }
        } catch (java.sql.SQLException e) { e.printStackTrace(); }

        // 2. Count Courses & Sections (ERP DB)
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(dbUrlErp, dbUser, dbPassword)) {

            try (java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM courses")) {
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) courses = rs.getInt(1);
                }
            }

            try (java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM sections")) {
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) sections = rs.getInt(1);
                }
            }

            // 3. Check Maintenance Status
            try (java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT settings_value FROM settings WHERE settings_key = 'maintenance_on'")) {
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) maint = "true".equalsIgnoreCase(rs.getString(1));
                }
            }

        } catch (java.sql.SQLException e) { e.printStackTrace(); }

        return new edu.univ.erp.domain.AdminStats(users, courses, sections, maint);
    }

    // --- DELETE / EDIT OPERATIONS ---

    public boolean deleteUser(int userId) {
        Connection connAuth = null;
        Connection connErp = null;

        try {
            connAuth = DriverManager.getConnection(dbUrlAuth, dbUser, dbPassword);
            connErp = DriverManager.getConnection(dbUrlErp, dbUser, dbPassword);

            connAuth.setAutoCommit(false);
            connErp.setAutoCommit(false);

            // --- PRE-CHECK: Is this an Instructor teaching a Section? ---
            // If we try to delete them, the DB will throw an error. Let's catch it early.
            String checkInstructor = "SELECT COUNT(*) FROM sections WHERE instructor_id = ?";
            try (PreparedStatement stmt = connErp.prepareStatement(checkInstructor)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("Deletion Blocked: Instructor " + userId + " is assigned to sections.");
                        return false; // Silently fail (UI shows "Could not delete")
                    }
                }
            }
            // -------------------------------------------------------------

            // --- STEP A: Clean up Student Dependencies (ERP DB) ---

            // 1. Delete Scores
            try (PreparedStatement stmt = connErp.prepareStatement("DELETE FROM student_scores WHERE student_id = ?")) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }

            // 2. Delete Grades
            String cleanGrades = "DELETE FROM grades WHERE enrollment_id IN (SELECT enrollment_id FROM enrollments WHERE student_id = ?)";
            try (PreparedStatement stmt = connErp.prepareStatement(cleanGrades)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }

            // 3. Delete Enrollments
            try (PreparedStatement stmt = connErp.prepareStatement("DELETE FROM enrollments WHERE student_id = ?")) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }

            // --- STEP B: Delete the Profile (ERP DB) ---

            // 4. Delete from Students
            try (PreparedStatement stmt = connErp.prepareStatement("DELETE FROM students WHERE user_id = ?")) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }

            // 5. Delete from Instructors
            try (PreparedStatement stmt = connErp.prepareStatement("DELETE FROM instructors WHERE user_id = ?")) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }

            // CRITICAL: Commit ERP changes
            connErp.commit();

        } catch (SQLException e) {
            try { if (connErp != null) connErp.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        }

        // --- STEP 2: Delete Auth Data (Login) ---
        try {
            String sqlAuth = "DELETE FROM users_auth WHERE user_id = ?";
            try (PreparedStatement stmt = connAuth.prepareStatement(sqlAuth)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }
            connAuth.commit();
            return true;
        } catch (SQLException e) {
            try { if (connAuth != null) connAuth.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { if (connErp != null) connErp.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            try { if (connAuth != null) connAuth.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    public boolean deleteCourse(String code) {
        String query = "DELETE FROM courses WHERE code = ?";
        try (Connection conn = DriverManager.getConnection(dbUrlErp, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Likely FK constraint (sections exist)
            return false;
        }
    }

    public boolean deleteSection(int sectionId) {
        String query = "DELETE FROM sections WHERE section_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrlErp, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, sectionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Likely FK constraint (students enrolled)
            return false;
        }
    }

    public boolean updateSection(int sectionId, String dayTime, String room, int capacity) {
        String query = "UPDATE sections SET day_time = ?, room = ?, capacity = ? WHERE section_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrlErp, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, dayTime);
            stmt.setString(2, room);
            stmt.setInt(3, capacity);
            stmt.setInt(4, sectionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}