package app.controllers;

import app.utils.DashboardStats;
import app.utils.SceneManager;
import app.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

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
        if (UserSession.getInstance().isLoggedIn()) {
            welcomeLabel.setText("Welcome, " + UserSession.getInstance().getCurrentUser().getUsername());
            loadStatistics();
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
        UserSession.getInstance().clearSession();
        SceneManager.switchScene("/fxml/Login.fxml");
    }
}
