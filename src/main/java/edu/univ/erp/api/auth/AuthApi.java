package edu.univ.erp.api.auth;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.auth.session.UserSession;

public class AuthApi {

    private final AuthService authService = new AuthService();

    /**
     * The main login method for the UI to call.
     * @param username The username from the text field.
     * @param password The password from the password field.
     * @return A LoginResult telling the UI what happened.
     */
    public LoginResult attemptLogin(String username, String password) {

        // 1. Ask the AuthService to check the login
        LoginResult result = authService.login(username, password);

        // 2. If login was successful, start the global session
        if (result.getStatus() == LoginStatus.SUCCESS) {
            UserSession.getInstance().startSession(
                    result.getUserId(),
                    username, // We pass username since UserAuth doesn't store it (it's in the DB)
                    result.getRole()
            );
        }

        // 3. Return the result to the UI
        return result;
    }

    /**
     * Logs the current user out.
     */
    public void logout() {
        UserSession.getInstance().endSession();
    }

    // ... existing code ...

    public boolean changePassword(String newPassword) {
        int userId = UserSession.getInstance().getUserId();
        return authService.changePassword(userId, newPassword);
    }
}