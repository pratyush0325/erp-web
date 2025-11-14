package edu.univ.erp.api.auth;

public class LoginResult {
    private final LoginStatus status;
    private final String role;
    private final int userId;

    public LoginResult(LoginStatus status, String role, int userId) {
        this.status = status;
        this.role = role;
        this.userId = userId;
    }

    // --- Getters ---
    public LoginStatus getStatus() { return status; }
    public String getRole() { return role; }
    public int getUserId() { return userId; }
}