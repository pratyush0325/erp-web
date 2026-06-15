package edu.univ.erp.auth;

import edu.univ.erp.api.auth.LoginResult;
import edu.univ.erp.api.auth.LoginStatus;
import edu.univ.erp.auth.store.AuthStore;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthStore authStore;

    public AuthService(AuthStore authStore) {
        this.authStore = authStore;
    }

    public LoginResult login(String username, String password) {
        UserAuth user = authStore.findUserByUsername(username);

        if (user == null) {
            return new LoginResult(LoginStatus.USER_NOT_FOUND, null, 0);
        }

        if (!"Active".equalsIgnoreCase(user.getStatus())) {
            return new LoginResult(LoginStatus.ACCOUNT_INACTIVE, null, 0);
        }

        if (user.getLockoutUntil() != null) {
            long now = System.currentTimeMillis();
            if (now < user.getLockoutUntil().getTime()) {
                return new LoginResult(LoginStatus.ACCOUNT_LOCKED, null, 0);
            }
        }

        if (BCrypt.checkpw(password, user.getPasswordHash())) {
            authStore.resetFailedAttempts(user.getUserId());
            return new LoginResult(LoginStatus.SUCCESS, user.getRole(), user.getUserId());
        } else {
            authStore.incrementFailedAttempts(user.getUserId());
            int currentFailures = user.getFailedAttempts() + 1;
            int remaining = 5 - currentFailures;
            if (currentFailures >= 5) {
                authStore.lockAccount(user.getUserId(), 5);
                return new LoginResult(LoginStatus.ACCOUNT_LOCKED, null, 0);
            }
            return new LoginResult(LoginStatus.INVALID_PASSWORD, null, 0, remaining);
        }
    }

    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        UserAuth user = authStore.findUserById(userId);
        if (user == null) return false;
        if (!BCrypt.checkpw(currentPassword, user.getPasswordHash())) return false;
        return authStore.updatePassword(userId, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
    }
}
