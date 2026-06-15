package edu.univ.erp.data;

import edu.univ.erp.domain.*;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AdminStore {

    private static final Logger log = LoggerFactory.getLogger(AdminStore.class);

    private final DataSource authDataSource;
    private final DataSource erpDataSource;

    public AdminStore(@Qualifier("authDataSource") DataSource authDataSource,
                      @Qualifier("erpDataSource") DataSource erpDataSource) {
        this.authDataSource = authDataSource;
        this.erpDataSource = erpDataSource;
    }

    public List<UserAdminItem> getAllUsers() {
        List<UserAdminItem> users = new ArrayList<>();
        String query = "SELECT user_id, username, role, status FROM users_auth ORDER BY user_id DESC";
        try (Connection conn = authDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new UserAdminItem(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            log.error("Failed to fetch all users", e);
        }
        return users;
    }

    public boolean addUser(String username, String rawPassword, String role,
                           String extra1, String extra2, String extra3) {
        Connection connAuth = null;
        Connection connErp = null;
        int createdUserId = -1;

        try {
            connAuth = authDataSource.getConnection();
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
            connAuth.commit();

            connErp = erpDataSource.getConnection();
            connErp.setAutoCommit(false);

            if ("student".equalsIgnoreCase(role)) {
                String stuSql = "INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = connErp.prepareStatement(stuSql)) {
                    stmt.setInt(1, createdUserId);
                    stmt.setString(2, extra1);
                    stmt.setString(3, extra3);
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

            connErp.commit();
            return true;

        } catch (Exception e) {
            log.error("Failed to add user: {}", username, e);
            try { if (connErp != null) connErp.rollback(); } catch (SQLException ex) { log.error("Rollback failed", ex); }
            if (createdUserId != -1 && connAuth != null) {
                try {
                    connAuth.setAutoCommit(true);
                    try (PreparedStatement stmt = connAuth.prepareStatement("DELETE FROM users_auth WHERE user_id = ?")) {
                        stmt.setInt(1, createdUserId);
                        stmt.executeUpdate();
                    }
                } catch (SQLException ex) { log.error("Cleanup of orphan auth user failed", ex); }
            }
            return false;
        } finally {
            try { if (connAuth != null) connAuth.close(); } catch (SQLException ex) { log.error("Close failed", ex); }
            try { if (connErp != null) connErp.close(); } catch (SQLException ex) { log.error("Close failed", ex); }
        }
    }

    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String query = "SELECT code, title, credits FROM courses ORDER BY code";
        try (Connection conn = erpDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Course(rs.getString("code"), rs.getString("title"), rs.getInt("credits")));
            }
        } catch (SQLException e) {
            log.error("Failed to fetch all courses", e);
        }
        return list;
    }

    public boolean addCourse(String code, String title, int credits) {
        String query = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";
        try (Connection conn = erpDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
            stmt.setString(2, title);
            stmt.setInt(3, credits);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Failed to add course: {}", code, e);
            return false;
        }
    }

    public List<SectionAdminItem> getAllSections() {
        List<SectionAdminItem> list = new ArrayList<>();
        String query = "SELECT s.section_id, s.course_id, u.username, s.day_time, s.capacity, s.semester, s.year " +
                "FROM sections s " +
                "JOIN instructors i ON s.instructor_id = i.user_id " +
                "JOIN auth_db.users_auth u ON i.user_id = u.user_id " +
                "ORDER BY s.year DESC, s.semester, s.course_id";
        try (Connection conn = erpDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new SectionAdminItem(
                        rs.getInt("section_id"),
                        rs.getString("course_id"),
                        rs.getString("username"),
                        rs.getString("day_time"),
                        rs.getInt("capacity"),
                        rs.getString("semester"),
                        rs.getInt("year")
                ));
            }
        } catch (SQLException e) {
            log.error("Failed to fetch all sections", e);
        }
        return list;
    }

    public boolean addSection(String courseId, int instructorId, String dayTime, String room,
                              int capacity, String semester, int year) {
        String query = "INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = erpDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseId);
            stmt.setInt(2, instructorId);
            stmt.setString(3, dayTime);
            stmt.setString(4, room);
            stmt.setInt(5, capacity);
            stmt.setString(6, semester);
            stmt.setInt(7, year);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Failed to add section for course={}", courseId, e);
            return false;
        }
    }

    public boolean updateUserStatus(int userId, String newStatus) {
        String query = "UPDATE users_auth SET status = ? WHERE user_id = ?";
        try (Connection conn = authDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Failed to update status for userId={}", userId, e);
            return false;
        }
    }

    public Map<Integer, String> getInstructorsMap() {
        Map<Integer, String> map = new LinkedHashMap<>();
        String query = "SELECT u.user_id, u.username FROM auth_db.users_auth u WHERE u.role = 'instructor'";
        try (Connection conn = authDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getInt("user_id"), rs.getString("username"));
            }
        } catch (SQLException e) {
            log.error("Failed to fetch instructors map", e);
        }
        return map;
    }

    public AdminStats getDashboardStats() {
        int users = 0, courses = 0, sections = 0;
        boolean maint = false;

        try (Connection conn = authDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM users_auth");
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) users = rs.getInt(1);
        } catch (SQLException e) {
            log.error("Failed to count users for stats", e);
        }

        try (Connection conn = erpDataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM courses");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) courses = rs.getInt(1);
            }
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM sections");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) sections = rs.getInt(1);
            }
            try (PreparedStatement stmt = conn.prepareStatement("SELECT settings_value FROM settings WHERE settings_key = 'maintenance_on'");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) maint = "true".equalsIgnoreCase(rs.getString(1));
            }
        } catch (SQLException e) {
            log.error("Failed to fetch ERP stats", e);
        }

        return new AdminStats(users, courses, sections, maint);
    }

    public boolean deleteUser(int userId) {
        Connection connAuth = null;
        Connection connErp = null;

        try {
            connAuth = authDataSource.getConnection();
            connErp = erpDataSource.getConnection();

            connAuth.setAutoCommit(false);
            connErp.setAutoCommit(false);

            String checkInstructor = "SELECT COUNT(*) FROM sections WHERE instructor_id = ?";
            try (PreparedStatement stmt = connErp.prepareStatement(checkInstructor)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        log.warn("Deletion blocked: instructor {} is assigned to sections", userId);
                        return false;
                    }
                }
            }

            try (PreparedStatement stmt = connErp.prepareStatement("DELETE FROM student_scores WHERE student_id = ?")) {
                stmt.setInt(1, userId); stmt.executeUpdate();
            }
            String cleanGrades = "DELETE FROM grades WHERE enrollment_id IN (SELECT enrollment_id FROM enrollments WHERE student_id = ?)";
            try (PreparedStatement stmt = connErp.prepareStatement(cleanGrades)) {
                stmt.setInt(1, userId); stmt.executeUpdate();
            }
            try (PreparedStatement stmt = connErp.prepareStatement("DELETE FROM enrollments WHERE student_id = ?")) {
                stmt.setInt(1, userId); stmt.executeUpdate();
            }
            try (PreparedStatement stmt = connErp.prepareStatement("DELETE FROM students WHERE user_id = ?")) {
                stmt.setInt(1, userId); stmt.executeUpdate();
            }
            try (PreparedStatement stmt = connErp.prepareStatement("DELETE FROM instructors WHERE user_id = ?")) {
                stmt.setInt(1, userId); stmt.executeUpdate();
            }
            connErp.commit();

        } catch (SQLException e) {
            try { if (connErp != null) connErp.rollback(); } catch (SQLException ex) { log.error("Rollback failed", ex); }
            log.error("Failed to delete ERP data for userId={}", userId, e);
            return false;
        }

        try {
            try (PreparedStatement stmt = connAuth.prepareStatement("DELETE FROM users_auth WHERE user_id = ?")) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }
            connAuth.commit();
            return true;
        } catch (SQLException e) {
            try { if (connAuth != null) connAuth.rollback(); } catch (SQLException ex) { log.error("Rollback failed", ex); }
            log.error("Failed to delete auth record for userId={}", userId, e);
            return false;
        } finally {
            try { if (connErp != null) connErp.close(); } catch (SQLException ex) { log.error("Close failed", ex); }
            try { if (connAuth != null) connAuth.close(); } catch (SQLException ex) { log.error("Close failed", ex); }
        }
    }

    public boolean deleteCourse(String code) {
        String query = "DELETE FROM courses WHERE code = ?";
        try (Connection conn = erpDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Failed to delete course={} (FK constraint?)", code, e);
            return false;
        }
    }

    public boolean deleteSection(int sectionId) {
        String query = "DELETE FROM sections WHERE section_id = ?";
        try (Connection conn = erpDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, sectionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Failed to delete sectionId={} (FK constraint?)", sectionId, e);
            return false;
        }
    }

    public boolean updateSection(int sectionId, String dayTime, String room, int capacity) {
        String query = "UPDATE sections SET day_time = ?, room = ?, capacity = ? WHERE section_id = ?";
        try (Connection conn = erpDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, dayTime);
            stmt.setString(2, room);
            stmt.setInt(3, capacity);
            stmt.setInt(4, sectionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Failed to update sectionId={}", sectionId, e);
            return false;
        }
    }
}
