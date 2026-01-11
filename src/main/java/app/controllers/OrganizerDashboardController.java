package app.controllers;

import app.utils.DashboardStats;
import app.utils.SceneManager;
import app.utils.UserSession;
import app.utils.AuthorizationManager;
import app.utils.Constants;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

/**
 * Controller for the Organizer Dashboard
 */
public class OrganizerDashboardController {
    @FXML
    private Label welcomeLabel;

    @FXML
    private Label statsLabel;

    @FXML
    private Label eventsCountLabel;

    @FXML
    private Label ticketsSoldLabel;

    @FXML
    private Label revenueLabel;

    @FXML
    private javafx.scene.control.Button createEventButton;

    @FXML
    private javafx.scene.control.Button myEventsButton;

    @FXML
    private javafx.scene.control.Button logoutButton;

    @FXML
    public void initialize() {
        try {
            // Authorization check - only organizer or admin can access
            AuthorizationManager.requireAnyRole(Constants.ROLE_ORGANIZER, Constants.ROLE_ADMIN);

            if (UserSession.getInstance().isLoggedIn()) {
                welcomeLabel.setText("Welcome, " + UserSession.getInstance().getCurrentUser().getUsername());
                loadStatistics();
            }
        } catch (AuthorizationManager.UnauthorizedException e) {
            AuthorizationManager.showUnauthorizedAlert(e.getMessage());
            SceneManager.switchScene("/fxml/Login.fxml");
        } catch (Exception e) {
            System.err.println("Error initializing OrganizerDashboardController:");
            e.printStackTrace();
        }
    }

    /**
     * Load and display organizer statistics
     */
    private void loadStatistics() {
        if (UserSession.getInstance().isLoggedIn()) {
            int organizerId = UserSession.getInstance().getCurrentUser().getId();
            DashboardStats.OrganizerStats stats = DashboardStats.getOrganizerStats(organizerId);

            eventsCountLabel.setText(String.valueOf(stats.totalEvents));
            ticketsSoldLabel.setText(String.valueOf(stats.totalTicketsSold));
            revenueLabel.setText("$" + String.format("%.2f", stats.totalRevenue));

            if (stats.totalEvents == 0) {
                statsLabel.setText("You haven't created any events yet. Create your first event to get started!");
            } else {
                statsLabel.setText("You have " + stats.totalEvents + " event(s) with " + stats.totalTicketsSold
                        + " ticket(s) sold");
            }
        }
    }

    /**
     * Handle create event button click
     */
    @FXML
    private void handleCreateEvent() {
        SceneManager.switchScene("/fxml/CreateEvent.fxml");
    }

    /**
     * Handle my events button click
     */
    @FXML
    private void handleMyEvents() {
        SceneManager.switchScene("/fxml/OrganizerEvents.fxml");
    }

    /**
     * Handle logout button click
     */
    @FXML
    private void handleLogout() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Logout");
        confirmation.setHeaderText("Are you sure you want to logout?");
        confirmation.setContentText("You will need to login again to access the organizer dashboard.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Clear session
            UserSession.clearSession();
            // Navigate to login
            SceneManager.switchScene("/fxml/Login.fxml");
        }
    }
}
