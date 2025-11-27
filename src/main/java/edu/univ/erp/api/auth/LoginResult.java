package edu.univ.erp.api.auth;

public class LoginResult {
    private final LoginStatus status;
    private final String role;
    private final int userId;
    private final int remainingAttempts; // <--- NEW FIELD

    // Updated Constructor
    public LoginResult(LoginStatus status, String role, int userId, int remainingAttempts) {
        this.status = status;
        this.role = role;
        this.userId = userId;
        this.remainingAttempts = remainingAttempts;
    }

    // Helper constructor for simple cases (Success/User Not Found) where attempts don't matter
    public LoginResult(LoginStatus status, String role, int userId) {
        this(status, role, userId, -1);
    }

    public LoginStatus getStatus() { return status; }
    public String getRole() { return role; }
    public int getUserId() { return userId; }
    public int getRemainingAttempts() { return remainingAttempts; } // <--- Getter
}