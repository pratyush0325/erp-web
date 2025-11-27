package edu.univ.erp.auth;

import java.sql.Timestamp;

public class UserAuth {
    private final int userId;
    private final String username;
    private final String role;
    private final String passwordHash;
    private final int failedAttempts;
    private final Timestamp lockoutUntil;
    private final String status; // <--- NEW

    public UserAuth(int userId, String username, String role, String passwordHash, int failedAttempts, Timestamp lockoutUntil, String status) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.passwordHash = passwordHash;
        this.failedAttempts = failedAttempts;
        this.lockoutUntil = lockoutUntil;
        this.status = status;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getPasswordHash() { return passwordHash; }
    public int getFailedAttempts() { return failedAttempts; }
    public Timestamp getLockoutUntil() { return lockoutUntil; }
    public String getStatus() { return status; } // <--- Getter
}