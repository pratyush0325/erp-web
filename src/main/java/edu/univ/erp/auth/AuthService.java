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
        // 1. Find the user in the database
        UserAuth user = authStore.findUserByUsername(username);

        if (user == null) {
            return new LoginResult(LoginStatus.USER_NOT_FOUND, null, 0);
        }

        // 2. Check the password
        if (BCrypt.checkpw(password, user.getPasswordHash())) {
            // Success!
            return new LoginResult(LoginStatus.SUCCESS, user.getRole(), user.getUserId());
        } else {
            // Wrong password
            return new LoginResult(LoginStatus.INVALID_PASSWORD, null, 0);
        }
    }

    // ... existing code ...

    public boolean changePassword(int userId, String newPassword) {
        // Hash the password using BCrypt
        String hash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        return authStore.updatePassword(userId, hash);
    }
}
