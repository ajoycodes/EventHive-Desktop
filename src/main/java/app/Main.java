package app;

import app.utils.DatabaseConnection;
import app.utils.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main entry point for the EventHive Desktop Application
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        System.out.println("[Main] JavaFX start method called");

        // Initialize database
        System.out.println("[Main] Initializing database...");
        DatabaseConnection.initializeDatabase();

        // Set primary stage for scene management
        SceneManager.setPrimaryStage(primaryStage);

        // Set window properties
        primaryStage.setTitle("EventHive - Event Management System");
        primaryStage.setWidth(900);
        primaryStage.setHeight(700);
        primaryStage.setResizable(true);

        // Load and show login screen
        System.out.println("[Main] Switching to Login scene...");
        SceneManager.switchScene("/fxml/Login.fxml");

        // Handle window close
        primaryStage.setOnCloseRequest(e -> {
            DatabaseConnection.closeConnection();
        });
    }

    public static void main(String[] args) {
        System.out.println("[Main] Launching application...");
        launch(args);
    }
}
