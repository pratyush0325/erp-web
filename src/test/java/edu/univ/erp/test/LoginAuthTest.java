package edu.univ.erp.test;

import edu.univ.erp.api.auth.LoginResult;
import edu.univ.erp.api.auth.LoginStatus;
import edu.univ.erp.auth.AuthService;
import edu.univ.erp.auth.store.AuthStore;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class LoginAuthTest {

    private static AuthService authService;
    private static AuthStore authStore;

    // Use a user that definitely exists in your seed data
    private final String VALID_USER = "stu4";
    private final String VALID_PASS = "stu123"; // Matches your GenerateHash.java
    private final int USER_ID = 6; // Check your DB for stu4's ID

    @BeforeAll
    static void setup() {
        authService = new AuthService();
        authStore = new AuthStore();
    }

    @BeforeEach
    void unlockUser() {
        // Ensure user is clean before every test
        authStore.resetFailedAttempts(USER_ID);
    }

    @Test
    @DisplayName("Valid Login returns SUCCESS")
    void testValidLogin() {
        LoginResult result = authService.login(VALID_USER, VALID_PASS);
        assertEquals(LoginStatus.SUCCESS, result.getStatus());
    }

    @Test
    @DisplayName("Invalid Password returns INVALID_PASSWORD")
    void testInvalidLogin() {
        LoginResult result = authService.login(VALID_USER, "wrongpass");
        assertEquals(LoginStatus.INVALID_PASSWORD, result.getStatus());
        assertTrue(result.getRemainingAttempts() < 5);
    }

    @Test
    @DisplayName("5 Failed Attempts triggers ACCOUNT_LOCKED")
    void testLockoutLogic() {
        // 1. Fail 5 times
        for (int i = 0; i < 5; i++) {
            authService.login(VALID_USER, "wrongpass");
        }

        // 2. Next attempt (even with correct password) should be locked
        LoginResult result = authService.login(VALID_USER, VALID_PASS);

        // If it returns LOCKED immediately, pass.
        // If logic allows 5 fails then locks on 6th try, this catches it.
        if (result.getStatus() != LoginStatus.ACCOUNT_LOCKED) {
            result = authService.login(VALID_USER, VALID_PASS);
        }

        assertEquals(LoginStatus.ACCOUNT_LOCKED, result.getStatus());
    }
}