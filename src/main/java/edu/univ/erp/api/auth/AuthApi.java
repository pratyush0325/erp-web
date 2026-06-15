package edu.univ.erp.api.auth;

import edu.univ.erp.auth.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthApi {

    private final AuthService authService;

    public AuthApi(AuthService authService) {
        this.authService = authService;
    }

    public LoginResult attemptLogin(String username, String password) {
        return authService.login(username, password);
    }

    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        return authService.changePassword(userId, currentPassword, newPassword);
    }
}
