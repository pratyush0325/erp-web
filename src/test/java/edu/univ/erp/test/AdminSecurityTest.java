package edu.univ.erp.test;

import edu.univ.erp.data.AdminStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AdminSecurityTest {

    @Autowired
    private AdminStore adminStore;

    private final String DB_USER = "root";
    private final String DB_PASS = "prabhi12";
    private final String TEST_USER = "security_test_user";

    @BeforeEach
    void cleanSlate() {
        int userId = -1;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/auth_db", DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT user_id FROM users_auth WHERE username = ?")) {
            stmt.setString(1, TEST_USER);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) userId = rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }

        if (userId != -1) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/erp_db", DB_USER, DB_PASS);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM students WHERE user_id = " + userId);
                stmt.executeUpdate("DELETE FROM instructors WHERE user_id = " + userId);
            } catch (Exception e) { e.printStackTrace(); }

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/auth_db", DB_USER, DB_PASS);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM users_auth WHERE user_id = " + userId);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    @Test
    @DisplayName("Passwords must be stored as BCrypt Hash, not plain text")
    void testPasswordHashing() {
        String testPass = "mySecret123";
        boolean created = adminStore.addUser(TEST_USER, testPass, "student", "999", "1", "CS");
        assertTrue(created, "User creation failed");

        String storedHash = "";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/auth_db", DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT password_hash FROM users_auth WHERE username = ?")) {
            stmt.setString(1, TEST_USER);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) storedHash = rs.getString(1);
        } catch (Exception e) {
            fail("Database query failed: " + e.getMessage());
        }

        cleanSlate();

        assertNotNull(storedHash, "Hash should not be null");
        assertNotEquals(testPass, storedHash, "Security Alert: Password stored in plain text!");
        assertTrue(storedHash.startsWith("$2a$"), "Password should be a standard BCrypt hash");
    }
}
