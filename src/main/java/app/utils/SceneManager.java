package app.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Manages JavaFX scene transitions and FXML loading
 */
public class SceneManager {
    private static Stage primaryStage;

    /**
     * Set the primary stage for the application
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Get the primary stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Switch to a new scene by loading an FXML file
     * 
     * @param fxmlPath Path to FXML file (e.g., "/fxml/Login.fxml")
     */
    public static void switchScene(String fxmlPath) {
        try {
            java.net.URL resource = SceneManager.class.getResource(fxmlPath);
            if (resource == null) {
                System.err.println("CRITICAL ERROR: FXML file not found: " + fxmlPath);
                System.err.println("Check if the file exists in 'src/main/resources/fxml/'");
                // Don't crash, just return to avoid scene corruption
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Switch to a new scene and return the controller
     * 
     * @param fxmlPath Path to FXML file
     * @return The controller instance for the loaded FXML
     */
    public static <T> T switchSceneWithController(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

            return loader.getController();

        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Load FXML and get controller without switching scene
     * 
     * @param fxmlPath Path to FXML file
     * @return The controller instance
     */
    public static <T> T loadController(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            loader.load();
            return loader.getController();
        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }
}
