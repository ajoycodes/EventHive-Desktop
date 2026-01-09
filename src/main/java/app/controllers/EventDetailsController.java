package app.controllers;

import app.models.Event;
import app.utils.SceneManager;
import app.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller for the Event Details screen
 */
public class EventDetailsController implements Initializable {
    @FXML
    private Label eventNameLabel;

    @FXML
    private Label dateTimeLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label seatsLabel;

    @FXML
    private Label totalSeatsLabel;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Button bookButton;

    @FXML
    private Label errorLabel;

    private static Event selectedEvent;

    public static void setSelectedEvent(Event event) {
        selectedEvent = event;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (selectedEvent != null) {
            displayEventDetails(selectedEvent);
        } else {
            errorLabel.setText("No event selected");
        }
    }

    /**
     * Display event details
     */
    private void displayEventDetails(Event event) {
        eventNameLabel.setText(event.getName());
        dateTimeLabel.setText(event.getDateTime().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' HH:mm")));
        locationLabel.setText(event.getLocation());
        priceLabel.setText("$" + String.format("%.2f", event.getTicketPrice()));
        seatsLabel.setText(String.valueOf(event.getAvailableSeats()));
        totalSeatsLabel.setText(String.valueOf(event.getTotalSeats()));
        descriptionArea.setText(event.getDescription() != null && !event.getDescription().isEmpty() 
            ? event.getDescription() 
            : "No description available.");

        // Disable book button if no seats available
        bookButton.setDisable(!event.hasAvailableSeats());
        if (!event.hasAvailableSeats()) {
            bookButton.setText("Sold Out");
        }
    }

    /**
     * Handle book ticket button click
     */
    @FXML
    private void handleBookTicket() {
        if (!UserSession.getInstance().isLoggedIn()) {
            errorLabel.setText("Please login to book tickets");
            return;
        }

        if (selectedEvent != null) {
            BookTicketController.setSelectedEvent(selectedEvent);
            SceneManager.switchScene("/fxml/BookTicket.fxml");
        }
    }

    /**
     * Handle back button click
     */
    @FXML
    private void handleBack() {
        SceneManager.switchScene("/fxml/EventList.fxml");
    }
}

