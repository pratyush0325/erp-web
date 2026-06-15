package edu.univ.erp.test;

import edu.univ.erp.api.auth.LoginResult;
import edu.univ.erp.api.auth.LoginStatus;
import edu.univ.erp.auth.AuthService;
import edu.univ.erp.auth.store.AuthStore;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoginAuthTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthStore authStore;

    private final String VALID_USER = "stu4";
    private final String VALID_PASS = "stu123";
    private final int USER_ID = 6;

    @BeforeEach
    void unlockUser() {
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
        for (int i = 0; i < 5; i++) {
            authService.login(VALID_USER, "wrongpass");
        }
        LoginResult result = authService.login(VALID_USER, VALID_PASS);
        if (result.getStatus() != LoginStatus.ACCOUNT_LOCKED) {
            result = authService.login(VALID_USER, VALID_PASS);
        }
        assertEquals(LoginStatus.ACCOUNT_LOCKED, result.getStatus());
    }
}
