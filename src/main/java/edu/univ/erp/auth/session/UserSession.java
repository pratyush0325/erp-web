package edu.univ.erp.auth.session;

public class UserSession {
    // Singleton instance
    private static UserSession instance;

    private int userId;
    private String username;
    private String role;
    private boolean loggedIn = false;

    // Private constructor to prevent outside instantiation
    private UserSession() {}

    /**
     * Gets the single instance of the UserSession.
     * @return The UserSession instance.
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Starts a new session for a user.
     */
    public void startSession(int userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.loggedIn = true;
    }

    /**
     * Ends the current session.
     */
    public void endSession() {
        this.userId = 0;
        this.username = null;
        this.role = null;
        this.loggedIn = false;
    }

    // --- Getters ---
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public boolean isLoggedIn() { return loggedIn; }

    public void setSession(int userId, String role, String username) {
        this.userId = userId;
        this.role = role;
        this.username = username;
    }
}