package app.controllers;

import app.utils.DashboardStats;
import app.utils.SceneManager;
import app.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

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
        if (UserSession.getInstance().isLoggedIn()) {
            welcomeLabel.setText("Welcome, " + UserSession.getInstance().getCurrentUser().getUsername());
            loadStatistics();
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
        UserSession.getInstance().clearSession();
        SceneManager.switchScene("/fxml/Login.fxml");
    }
}
