package edu.univ.erp.auth;

import edu.univ.erp.api.auth.LoginResult;
import edu.univ.erp.api.auth.LoginStatus;
import edu.univ.erp.auth.store.AuthStore;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    private final AuthStore authStore = new AuthStore();

    /**
     * Attempts to log in a user.
     * @param username The username.
     * @param password The plain-text password.
     * @return A LoginResult object with the outcome.
     */
    public LoginResult login(String username, String password) {
        UserAuth user = authStore.findUserByUsername(username);

        if (user == null) {
            return new LoginResult(LoginStatus.USER_NOT_FOUND, null, 0);
        }

        // 1. CHECK LOCKOUT STATUS
        if (user.getLockoutUntil() != null) {
            long now = System.currentTimeMillis();
            long unlockTime = user.getLockoutUntil().getTime();

            if (now < unlockTime) {
                // Still locked
                return new LoginResult(LoginStatus.ACCOUNT_LOCKED, null, 0);
            }
            // Lock expired -> Proceed (the DB will reset attempts on next success, or increment on fail)
        }

        // 2. CHECK PASSWORD
        if (BCrypt.checkpw(password, user.getPasswordHash())) {
            // SUCCESS: Reset bad attempts counter
            authStore.resetFailedAttempts(user.getUserId());
            return new LoginResult(LoginStatus.SUCCESS, user.getRole(), user.getUserId());
        } else {
            // FAILURE: Increment counter
            authStore.incrementFailedAttempts(user.getUserId());

            // Calculate attempts
            int currentFailures = user.getFailedAttempts() + 1; // +1 because we just incremented it
            int maxAttempts = 5;
            int remaining = maxAttempts - currentFailures;

            // Check if they hit the limit
            if (currentFailures >= maxAttempts) {
                // Lock for 30 seconds
                authStore.lockAccount(user.getUserId(), 5);
                return new LoginResult(LoginStatus.ACCOUNT_LOCKED, null, 0);
            }

            // Return INVALID_PASSWORD with the remaining count
            return new LoginResult(LoginStatus.INVALID_PASSWORD, null, 0, remaining);
        }
    }

    // ... existing code ...

    public boolean changePassword(int userId, String newPassword) {
        // Hash the password using BCrypt
        String hash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        return authStore.updatePassword(userId, hash);
    }
}
