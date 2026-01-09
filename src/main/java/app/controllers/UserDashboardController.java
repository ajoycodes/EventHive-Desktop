package app.controllers;

import app.utils.DashboardStats;
import app.utils.SceneManager;
import app.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for the User Dashboard
 */
public class UserDashboardController {
    @FXML
    private Label welcomeLabel;

    @FXML
    private Label statsLabel;

    @FXML
    private Label ticketsCountLabel;

    @FXML
    private Label totalSpentLabel;

    @FXML
    public void initialize() {
        if (UserSession.getInstance().isLoggedIn()) {
            welcomeLabel.setText("Welcome, " + UserSession.getInstance().getCurrentUser().getUsername());
            loadStatistics();
        }
    }

    /**
     * Load and display user statistics
     */
    private void loadStatistics() {
        if (UserSession.getInstance().isLoggedIn()) {
            int userId = UserSession.getInstance().getCurrentUser().getId();
            DashboardStats.UserStats stats = DashboardStats.getUserStats(userId);
            
            ticketsCountLabel.setText(String.valueOf(stats.totalTickets));
            totalSpentLabel.setText("$" + String.format("%.2f", stats.totalSpent));
            
            if (stats.totalTickets == 0) {
                statsLabel.setText("You haven't booked any tickets yet. Browse events to get started!");
            } else {
                statsLabel.setText("You have " + stats.totalTickets + " ticket(s) booked");
            }
        }
    }

    /**
     * Handle view events button click
     */
    @FXML
    private void handleViewEvents() {
        SceneManager.switchScene("/fxml/EventList.fxml");
    }

    /**
     * Handle my tickets button click
     */
    @FXML
    private void handleMyTickets() {
        SceneManager.switchScene("/fxml/MyTickets.fxml");
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

