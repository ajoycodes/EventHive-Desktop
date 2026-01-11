package app.utils;

/**
 * Application constants
 */
public class Constants {

    // FXML Paths
    public static final String VIEW_LOGIN = "/fxml/Login.fxml";
    public static final String VIEW_SIGNUP = "/fxml/Signup.fxml";

    public static final String VIEW_USER_DASHBOARD = "/fxml/UserDashboard.fxml";
    public static final String VIEW_ADMIN_DASHBOARD = "/fxml/AdminDashboard.fxml";
    public static final String VIEW_ORGANIZER_DASHBOARD = "/fxml/OrganizerDashboard.fxml";

    public static final String VIEW_EVENT_LIST = "/fxml/EventList.fxml";
    public static final String VIEW_MY_TICKETS = "/fxml/MyTickets.fxml";
    public static final String VIEW_CREATE_EVENT = "/fxml/CreateEvent.fxml";
    public static final String VIEW_ORGANIZER_EVENTS = "/fxml/OrganizerEvents.fxml";
    public static final String VIEW_ADMIN_USERS = "/fxml/AdminUsers.fxml";
    public static final String VIEW_ADMIN_EVENTS = "/fxml/AdminEvents.fxml";

    // Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_ORGANIZER = "ORGANIZER";
    public static final String ROLE_USER = "USER";

    // Security Settings
    public static final int SESSION_TIMEOUT_MINUTES = 30;
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final int ACCOUNT_LOCK_MINUTES = 15;
    public static final int RESET_TOKEN_EXPIRY_HOURS = 1;
    public static final int SESSION_WARNING_MINUTES = 5; // Warn user 5 minutes before session expires

    // Password Requirements
    public static final String PASSWORD_REQUIREMENTS = "Password must be at least 8 characters and contain:\n" +
            "• At least one uppercase letter\n" +
            "• At least one lowercase letter\n" +
            "• At least one number";

    // Account Status
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_LOCKED = "LOCKED";
    public static final String STATUS_SUSPENDED = "SUSPENDED";

    private Constants() {
        // Prevent instantiation
    }
}
