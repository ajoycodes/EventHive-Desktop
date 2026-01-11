package app.utils;

import app.models.User;

/**
 * Manages the current user session (singleton pattern)
 */
public class UserSession {
    private static UserSession instance;

    private User currentUser;

    /**
     * Private constructor for singleton pattern
     */
    private UserSession() {
    }

    /**
     * Get the singleton instance
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Set user session from User object
     */
    public static void setUser(User user) {
        UserSession session = getInstance();
        session.currentUser = user;
    }

    /**
     * Set current user (alias for setUser for compatibility)
     */
    public void setCurrentUser(User user) {
        setUser(user);
    }

    /**
     * Get current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Clear the current session (logout)
     */
    public static void clearSession() {
        instance = null;
    }

    /**
     * Check if a user is logged in
     */
    public static boolean isLoggedIn() {
        return instance != null && instance.currentUser != null;
    }

    // Getters
    public static int getUserId() {
        return isLoggedIn() ? getInstance().currentUser.getId() : -1;
    }

    public static String getUsername() {
        return isLoggedIn() ? getInstance().currentUser.getUsername() : null;
    }

    public static String getEmail() {
        return isLoggedIn() ? getInstance().currentUser.getEmail() : null;
    }

    public static String getRole() {
        return isLoggedIn() ? getInstance().currentUser.getRole() : null;
    }

    /**
     * Check if current user has a specific role
     */
    public static boolean hasRole(String role) {
        return isLoggedIn() && getInstance().currentUser.hasRole(role);
    }

    /**
     * Check if current user is admin
     */
    public static boolean isAdmin() {
        return isLoggedIn() && getInstance().currentUser.isAdmin();
    }

    /**
     * Check if current user is organizer
     */
    public static boolean isOrganizer() {
        return isLoggedIn() && getInstance().currentUser.isOrganizer();
    }

    /**
     * Check if current user is regular user
     */
    public static boolean isUser() {
        return isLoggedIn() && getInstance().currentUser.isUser();
    }
}
