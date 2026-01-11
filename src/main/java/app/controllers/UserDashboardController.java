package app.controllers;

import app.utils.DashboardStats;
import app.utils.SceneManager;
import app.utils.UserSession;
import app.utils.AuthorizationManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

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
    private javafx.scene.control.Button logoutButton;

    @FXML
    private javafx.scene.control.Button viewEventsButton;

    @FXML
    private javafx.scene.control.Button myTicketsButton;

    @FXML
    public void initialize() {
        try {
            // Authorization check
            AuthorizationManager.requireLogin();

            if (UserSession.getInstance().isLoggedIn()) {
                if (welcomeLabel != null) {
                    welcomeLabel.setText("Welcome, " + UserSession.getInstance().getCurrentUser().getUsername());
                }
                loadStatistics();
            }
        } catch (AuthorizationManager.UnauthorizedException e) {
            AuthorizationManager.showUnauthorizedAlert(e.getMessage());
            SceneManager.switchScene("/fxml/Login.fxml");
        } catch (Exception e) {
            System.err.println("Error initializing UserDashboardController:");
            e.printStackTrace();
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
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Logout");
        confirmation.setHeaderText("Are you sure you want to logout?");
        confirmation.setContentText("You will need to login again to access your account.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Clear session
            UserSession.clearSession();
            // Navigate to login
            SceneManager.switchScene("/fxml/Login.fxml");
        }
    }
}
