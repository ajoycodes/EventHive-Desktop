package app.controllers;

import app.dao.EventDAO;
import app.dao.TicketDAO;
import app.models.Event;
import app.models.Ticket;
import app.utils.SceneManager;
import app.utils.TicketCodeGenerator;
import app.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller for the Book Ticket screen
 */
public class BookTicketController implements Initializable {
    @FXML
    private Label eventNameLabel;

    @FXML
    private Label eventDateLabel;

    @FXML
    private Label eventLocationLabel;

    @FXML
    private Label eventPriceLabel;

    @FXML
    private Label eventSeatsLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private javafx.scene.control.Button confirmButton;

    @FXML
    private javafx.scene.control.Button cancelButton;

    private static Event selectedEvent;
    private EventDAO eventDAO;
    private TicketDAO ticketDAO;

    public static void setSelectedEvent(Event event) {
        selectedEvent = event;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventDAO = new EventDAO();
        ticketDAO = new TicketDAO();
        errorLabel.setText("");

        if (selectedEvent != null) {
            displayEventDetails(selectedEvent);
        } else {
            errorLabel.setText("No event selected");
        }
    }

    /**
     * Display event details on the screen
     */
    private void displayEventDetails(Event event) {
        eventNameLabel.setText("Event: " + event.getName());
        eventDateLabel.setText("Date: " + event.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        eventLocationLabel.setText("Location: " + event.getLocation());
        eventPriceLabel.setText("Price: $" + String.format("%.2f", event.getTicketPrice()));
        eventSeatsLabel.setText("Available Seats: " + event.getAvailableSeats());
    }

    /**
     * Handle confirm booking button click
     */
    @FXML
    private void handleConfirmBooking() {
        if (!UserSession.getInstance().isLoggedIn()) {
            errorLabel.setText("Please login to book tickets");
            return;
        }

        if (selectedEvent == null) {
            errorLabel.setText("No event selected");
            return;
        }

        // Refresh event data from database
        Event currentEvent = eventDAO.findById(selectedEvent.getId());
        if (currentEvent == null) {
            errorLabel.setText("Event not found");
            return;
        }

        // Check if seats are available
        if (!currentEvent.hasAvailableSeats()) {
            errorLabel.setText("No seats available for this event");
            return;
        }

        // Check if user has already booked this event
        int userId = UserSession.getInstance().getCurrentUser().getId();
        if (ticketDAO.hasUserBookedEvent(userId, currentEvent.getId())) {
            errorLabel.setText("You have already booked a ticket for this event");
            return;
        }

        // Create ticket
        String ticketCode = TicketCodeGenerator.generateTicketCode();
        Ticket ticket = new Ticket(
                ticketCode,
                currentEvent.getId(),
                userId,
                LocalDateTime.now(),
                currentEvent.getTicketPrice());

        // Save ticket and update available seats (transaction-like behavior)
        boolean ticketCreated = ticketDAO.createTicket(ticket);
        if (ticketCreated) {
            boolean seatsUpdated = eventDAO.decreaseAvailableSeats(currentEvent.getId());
            if (seatsUpdated) {
                // Show success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Booking Confirmed");
                alert.setHeaderText("Ticket Booked Successfully!");
                alert.setContentText("Your ticket code is: " + ticketCode);
                alert.showAndWait();

                // Navigate back to event list
                SceneManager.switchScene("/fxml/EventList.fxml");
            } else {
                errorLabel.setText("Error updating seats. Please try again.");
            }
        } else {
            errorLabel.setText("Error creating ticket. Please try again.");
        }
    }

    /**
     * Handle cancel button click
     */
    @FXML
    private void handleCancel() {
        SceneManager.switchScene("/fxml/EventList.fxml");
    }
}
