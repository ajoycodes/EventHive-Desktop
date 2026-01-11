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
 * Controller for the Admin Dashboard
 */
public class AdminDashboardController {
    @FXML
    private Label welcomeLabel;

    @FXML
    private Label statsLabel;

    @FXML
    private Label totalEventsLabel;

    @FXML
    private Label totalTicketsLabel;

    @FXML
    private Label totalRevenueLabel;

    @FXML
    private javafx.scene.control.Button viewUsersButton;

    @FXML
    private javafx.scene.control.Button viewAllEventsButton;

    @FXML
    private javafx.scene.control.Button viewStatisticsButton;

    @FXML
    private javafx.scene.control.Button logoutButton;

    @FXML
    public void initialize() {
        try {
            // Authorization check - only admin can access
            AuthorizationManager.requireRole(Constants.ROLE_ADMIN);

            if (UserSession.getInstance().isLoggedIn()) {
                welcomeLabel.setText("Welcome, " + UserSession.getInstance().getCurrentUser().getUsername());
                loadStatistics();
            }
        } catch (AuthorizationManager.UnauthorizedException e) {
            AuthorizationManager.showUnauthorizedAlert(e.getMessage());
            SceneManager.switchScene("/fxml/Login.fxml");
        } catch (Exception e) {
            System.err.println("Error initializing AdminDashboardController:");
            e.printStackTrace();
        }
    }

    /**
     * Load and display admin statistics
     */
    private void loadStatistics() {
        DashboardStats.AdminStats stats = DashboardStats.getAdminStats();

        totalEventsLabel.setText(String.valueOf(stats.totalEvents));
        totalTicketsLabel.setText(String.valueOf(stats.totalTickets));
        totalRevenueLabel.setText("$" + String.format("%.2f", stats.totalRevenue));

        statsLabel
                .setText("System Overview - " + stats.totalEvents + " events, " + stats.totalTickets + " tickets sold");
    }

    /**
     * Handle view users button click
     */
    @FXML
    private void handleViewUsers() {
        SceneManager.switchScene("/fxml/AdminUsers.fxml");
    }

    /**
     * Handle view all events button click
     */
    @FXML
    private void handleViewAllEvents() {
        SceneManager.switchScene("/fxml/AdminEvents.fxml");
    }

    /**
     * Handle view statistics button click
     */
    @FXML
    private void handleViewStatistics() {
        loadStatistics();
    }

    /**
     * Handle logout button click
     */
    @FXML
    private void handleLogout() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Logout");
        confirmation.setHeaderText("Are you sure you want to logout?");
        confirmation.setContentText("You will need to login again to access the admin panel.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Clear session
            UserSession.clearSession();
            // Navigate to login
            SceneManager.switchScene("/fxml/Login.fxml");
        }
    }
}
