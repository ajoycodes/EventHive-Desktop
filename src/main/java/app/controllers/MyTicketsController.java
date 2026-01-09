package app.controllers;

import app.dao.EventDAO;
import app.dao.TicketDAO;
import app.models.Event;
import app.models.Ticket;
import app.utils.SceneManager;
import app.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for the My Tickets screen
 * Displays all tickets booked by the current user
 */
public class MyTicketsController implements Initializable {
    @FXML
    private TableView<TicketDisplay> ticketsTable;

    @FXML
    private TableColumn<TicketDisplay, String> ticketCodeColumn;

    @FXML
    private TableColumn<TicketDisplay, String> eventNameColumn;

    @FXML
    private TableColumn<TicketDisplay, String> bookingDateColumn;

    @FXML
    private TableColumn<TicketDisplay, Double> priceColumn;

    @FXML
    private Label errorLabel;

    private TicketDAO ticketDAO;
    private EventDAO eventDAO;
    private ObservableList<TicketDisplay> tickets;

    // Helper class to display ticket with event name
    public static class TicketDisplay {
        private String ticketCode;
        private String eventName;
        private String bookingDate;
        private double price;

        public TicketDisplay(String ticketCode, String eventName, String bookingDate, double price) {
            this.ticketCode = ticketCode;
            this.eventName = eventName;
            this.bookingDate = bookingDate;
            this.price = price;
        }

        public String getTicketCode() { return ticketCode; }
        public String getEventName() { return eventName; }
        public String getBookingDate() { return bookingDate; }
        public double getPrice() { return price; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ticketDAO = new TicketDAO();
        eventDAO = new EventDAO();
        tickets = FXCollections.observableArrayList();
        errorLabel.setText("");

        // Set up table columns
        ticketCodeColumn.setCellValueFactory(new PropertyValueFactory<>("ticketCode"));
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        bookingDateColumn.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setCellFactory(column -> new TableCell<TicketDisplay, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText("$" + String.format("%.2f", price));
                }
            }
        });

        // Add view details button column
        TableColumn<TicketDisplay, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setCellFactory(param -> new TableCell<TicketDisplay, Void>() {
            private final Button viewButton = new Button("View Details");

            {
                viewButton.setOnAction(event -> {
                    TicketDisplay ticket = getTableView().getItems().get(getIndex());
                    handleViewTicketDetails(ticket);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });
        ticketsTable.getColumns().add(actionColumn);

        // Load tickets
        loadTickets();
    }

    /**
     * Load tickets for the current user
     */
    private void loadTickets() {
        if (!UserSession.getInstance().isLoggedIn()) {
            errorLabel.setText("Please login to view tickets");
            return;
        }

        int userId = UserSession.getInstance().getCurrentUser().getId();
        List<Ticket> userTickets = ticketDAO.getTicketsByUser(userId);

        // Get all events to map event IDs to names
        List<Event> allEvents = eventDAO.getAllEvents();
        Map<Integer, String> eventMap = new HashMap<>();
        for (Event event : allEvents) {
            eventMap.put(event.getId(), event.getName());
        }

        // Convert tickets to display objects
        tickets.clear();
        for (Ticket ticket : userTickets) {
            String eventName = eventMap.getOrDefault(ticket.getEventId(), "Unknown Event");
            String bookingDate = ticket.getBookingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            tickets.add(new TicketDisplay(
                ticket.getTicketCode(),
                eventName,
                bookingDate,
                ticket.getPrice()
            ));
        }

        ticketsTable.setItems(tickets);
    }

    /**
     * Handle view ticket details
     */
    private void handleViewTicketDetails(TicketDisplay ticket) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ticket Details");
        alert.setHeaderText("Your Ticket Information");
        alert.setContentText(
            "Ticket Code: " + ticket.getTicketCode() + "\n\n" +
            "Event: " + ticket.getEventName() + "\n" +
            "Booking Date: " + ticket.getBookingDate() + "\n" +
            "Price: $" + String.format("%.2f", ticket.getPrice()) + "\n\n" +
            "Please keep this ticket code for entry to the event."
        );
        alert.showAndWait();
    }

    /**
     * Handle back button click
     */
    @FXML
    private void handleBack() {
        SceneManager.switchScene("/fxml/UserDashboard.fxml");
    }
}

