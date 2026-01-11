package app.utils;

import app.models.User;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Centralized authorization and role-based access control manager
 * Handles permission checks and access control throughout the application
 */
public class AuthorizationManager {

    /**
     * Check if current user has the required role
     * 
     * @param requiredRole The role to check for (ADMIN, ORGANIZER, USER)
     * @return true if user has the role
     */
    public static boolean hasRole(String requiredRole) {
        if (!UserSession.isLoggedIn()) {
            return false;
        }

        return UserSession.hasRole(requiredRole);
    }

    /**
     * Check if current user has any of the required roles
     * 
     * @param roles One or more roles to check
     * @return true if user has at least one of the roles
     */
    public static boolean hasAnyRole(String... roles) {
        if (!UserSession.isLoggedIn()) {
            return false;
        }

        for (String role : roles) {
            if (UserSession.hasRole(role)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Require a specific role, throw exception if not authorized
     * 
     * @param requiredRole The required role
     * @throws UnauthorizedException if user doesn't have the role
     */
    public static void requireRole(String requiredRole) {
        if (!hasRole(requiredRole)) {
            throw new UnauthorizedException("Access denied. Required role: " + requiredRole);
        }
    }

    /**
     * Require any of the specified roles
     * 
     * @param roles One or more acceptable roles
     * @throws UnauthorizedException if user doesn't have any of the roles
     */
    public static void requireAnyRole(String... roles) {
        if (!hasAnyRole(roles)) {
            String roleList = String.join(", ", roles);
            throw new UnauthorizedException("Access denied. Required role: " + roleList);
        }
    }

    /**
     * Check if user is logged in
     * 
     * @throws UnauthorizedException if not logged in
     */
    public static void requireLogin() {
        if (!UserSession.isLoggedIn()) {
            throw new UnauthorizedException("You must be logged in to access this resource");
        }
    }

    /**
     * Check if current user can manage an event (owner or admin)
     * 
     * @param organizerId The organizer ID of the event
     * @return true if user can manage the event
     */
    public static boolean canManageEvent(int organizerId) {
        if (!UserSession.isLoggedIn()) {
            return false;
        }

        // Admin can manage all events
        if (UserSession.isAdmin()) {
            return true;
        }

        // Organizer can only manage their own events
        if (UserSession.isOrganizer()) {
            return UserSession.getUserId() == organizerId;
        }

        return false;
    }

    /**
     * Check if current user can manage another user (admin only)
     * 
     * @param userId The user ID to manage
     * @return true if current user can manage the specified user
     */
    public static boolean canManageUser(int userId) {
        if (!UserSession.isLoggedIn()) {
            return false;
        }

        // Only admin can manage users
        if (!UserSession.isAdmin()) {
            return false;
        }

        // Admin cannot delete themselves (safety check)
        return UserSession.getUserId() != userId;
    }

    /**
     * Check if current user can view a resource
     * 
     * @param ownerId The owner ID of the resource
     * @return true if user can view
     */
    public static boolean canView(int ownerId) {
        if (!UserSession.isLoggedIn()) {
            return false;
        }

        // Admin can view everything
        if (UserSession.isAdmin()) {
            return true;
        }

        // Users can view their own resources
        return UserSession.getUserId() == ownerId;
    }

    /**
     * Show an unauthorized access alert
     */
    public static void showUnauthorizedAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Access Denied");
        alert.setHeaderText("Unauthorized Access");
        alert.setContentText("You do not have permission to access this resource.");
        alert.showAndWait();
    }

    /**
     * Show an unauthorized access alert with custom message
     * 
     * @param message The error message
     */
    public static void showUnauthorizedAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Access Denied");
        alert.setHeaderText("Unauthorized Access");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Custom exception for authorization failures
     */
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}
