package app.controllers;

import app.dao.EventDAO;
import app.models.Event;
import app.utils.SceneManager;
import app.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

/**
 * Controller for the Create Event screen
 */
public class CreateEventController implements Initializable {
    @FXML
    private TextField nameField;

    @FXML
    private TextField dateField;

    @FXML
    private TextField locationField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField seatsField;

    @FXML
    private Label errorLabel;

    private EventDAO eventDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventDAO = new EventDAO();
        errorLabel.setText("");
    }

    /**
     * Handle create event button click
     */
    @FXML
    private void handleCreate() {
        // Validation
        if (nameField.getText().trim().isEmpty() ||
            dateField.getText().trim().isEmpty() ||
            locationField.getText().trim().isEmpty() ||
            priceField.getText().trim().isEmpty() ||
            seatsField.getText().trim().isEmpty()) {
            errorLabel.setText("Please fill in all required fields");
            return;
        }

        // Parse date
        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(dateField.getText().trim(), 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            errorLabel.setText("Invalid date format. Use: yyyy-MM-dd HH:mm");
            return;
        }

        // Parse price
        double price;
        try {
            price = Double.parseDouble(priceField.getText().trim());
            if (price < 0) {
                errorLabel.setText("Price cannot be negative");
                return;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Invalid price format");
            return;
        }

        // Parse seats
        int seats;
        try {
            seats = Integer.parseInt(seatsField.getText().trim());
            if (seats <= 0) {
                errorLabel.setText("Total seats must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Invalid seats format");
            return;
        }

        // Check if user is logged in and is an organizer
        if (!UserSession.getInstance().isLoggedIn() || 
            !UserSession.getInstance().getCurrentUser().isOrganizer()) {
            errorLabel.setText("Only organizers can create events");
            return;
        }

        // Create event
        Event event = new Event(
            nameField.getText().trim(),
            dateTime,
            locationField.getText().trim(),
            descriptionField.getText().trim(),
            price,
            seats,
            UserSession.getInstance().getCurrentUser().getId()
        );

        boolean success = eventDAO.createEvent(event);

        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Event Created");
            alert.setContentText("Event has been created successfully!");
            alert.showAndWait();

            // Navigate back to organizer dashboard
            SceneManager.switchScene("/fxml/OrganizerDashboard.fxml");
        } else {
            errorLabel.setText("Error creating event. Please try again.");
        }
    }

    /**
     * Handle cancel button click
     */
    @FXML
    private void handleCancel() {
        SceneManager.switchScene("/fxml/OrganizerDashboard.fxml");
    }
}

