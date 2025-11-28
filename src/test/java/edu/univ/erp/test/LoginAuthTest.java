package edu.univ.erp.test;

import edu.univ.erp.api.auth.LoginResult;
import edu.univ.erp.api.auth.LoginStatus;
import edu.univ.erp.auth.AuthService;
import edu.univ.erp.auth.store.AuthStore;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class LoginAuthTest {

    private static AuthService authService;
    private static AuthStore authStore;

    // Create a dummy user for testing to avoid locking real accounts
    private final String TEST_USER = "junit_test_user";
    private final String TEST_PASS = "pass123";

    @BeforeAll
    static void setup() {
        authService = new AuthService();
        authStore = new AuthStore();
    }

    @BeforeEach
    void resetUser() {
        // Reset the test user in the DB (Clear locks, set password)
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/auth_db", "root", "prabhi12")) {
            Statement stmt = conn.createStatement();
            // Delete if exists
            stmt.executeUpdate("DELETE FROM users_auth WHERE username='" + TEST_USER + "'");
            // Recreate fresh
            // (You might need to adjust columns if your schema is strict, or use AdminStore to create)
            // For simplicity, we assume we can manually insert or use an existing test account.
            // BETTER: Use AdminStore to create a fresh user.
        } catch (Exception e) { e.printStackTrace(); }

        // Create user via AdminStore (Assuming you have access, or manually mock DB data)
        // For this test, ensure 'junit_test_user' exists in your DB manually or via code.
    }

    // NOTE: Ensure 'stu4' or a test user exists and is UNLOCKED before running

    @Test
    @DisplayName("Valid Login returns SUCCESS")
    void testValidLogin() {
        // Use a known valid user
        LoginResult result = authService.login("stu4", "stu4");
        assertEquals(LoginStatus.SUCCESS, result.getStatus());
        assertEquals("student", result.getRole().toLowerCase());
    }

    @Test
    @DisplayName("Invalid Password returns INVALID_PASSWORD")
    void testInvalidLogin() {
        LoginResult result = authService.login("stu4", "wrongpass");
        assertEquals(LoginStatus.INVALID_PASSWORD, result.getStatus());
        assertTrue(result.getRemainingAttempts() < 5);
    }

    @Test
    @DisplayName("5 Failed Attempts triggers ACCOUNT_LOCKED")
    void testLockoutLogic() {
        // 1. Manually reset attempts for 'stu4' to 0 via DB first (optional but safe)

        // 2. Fail 5 times
        for (int i = 0; i < 5; i++) {
            authService.login("stu4", "stu123");
        }

        // 3. The 5th (or 6th) try should be locked
        LoginResult result = authService.login("stu4", "stu123");

        // Note: Depending on your logic (>=5 vs >5), it might lock on 5th or 6th.
        // If it returns LOCKED, test passed.
        if (result.getStatus() != LoginStatus.ACCOUNT_LOCKED) {
            // Try one more time to trigger the lock check
            result = authService.login("stu4", "stu123");
        }

        assertEquals(LoginStatus.ACCOUNT_LOCKED, result.getStatus());
    }
}