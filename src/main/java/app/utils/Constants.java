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

    private Constants() {
        // Prevent instantiation
    }
}
