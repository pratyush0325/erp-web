package edu.univ.erp.auth;

public class UserAuth {
    private final int userId;
    private final String username;
    private final String role;
    private final String passwordHash;

    public UserAuth(int userId, String username, String role, String passwordHash) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.passwordHash = passwordHash;
    }

    // --- Getters ---
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getPasswordHash() { return passwordHash; }
}