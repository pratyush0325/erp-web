package edu.univ.erp.test;

import edu.univ.erp.data.AdminStore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class AdminSecurityTest {

    private final AdminStore adminStore = new AdminStore();

    @Test
    @DisplayName("Passwords must be stored as BCrypt Hash, not plain text")
    void testPasswordHashing() {
        String testUser = "security_test_user";
        String testPass = "mySecret123";

        // 1. Create a user via the Store (which uses BCrypt)
        // Note: You might need to adjust parameters based on your exact addStudent/addUser signature
        boolean created = adminStore.addUser(testUser, testPass, "student", "999", "1", "CS");
        assertTrue(created, "User creation failed");

        // 2. Direct DB Query to inspect the password column
        String storedHash = "";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/auth_db", "root", "prabhi12");
             PreparedStatement stmt = conn.prepareStatement("SELECT password_hash FROM users_auth WHERE username = ?")) {

            stmt.setString(1, testUser);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                storedHash = rs.getString(1);
            }

            // Clean up (Delete the test user)
            stmt.executeUpdate("DELETE FROM users_auth WHERE username = '" + testUser + "'");

        } catch (Exception e) {
            fail("Database connection failed: " + e.getMessage());
        }

        // 3. Assertions
        assertNotEquals(testPass, storedHash, "Security Alert: Password stored in plain text!");
        assertTrue(storedHash.startsWith("$2a$"), "Password should be a standard BCrypt hash");
    }
}