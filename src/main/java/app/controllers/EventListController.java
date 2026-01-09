package app.controllers;

import app.dao.EventDAO;
import app.models.Event;
import app.utils.SceneManager;
import app.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

/**
 * Controller for the Event List screen (for users to view and book events)
 */
public class EventListController implements Initializable {
    @FXML
    private TableView<Event> eventsTable;

    @FXML
    private TableColumn<Event, String> nameColumn;

    @FXML
    private TableColumn<Event, String> dateColumn;

    @FXML
    private TableColumn<Event, String> locationColumn;

    @FXML
    private TableColumn<Event, Double> priceColumn;

    @FXML
    private TableColumn<Event, Integer> seatsColumn;

    @FXML
    private TableColumn<Event, Void> actionColumn;

    @FXML
    private Label errorLabel;

    @FXML
    private Button backButton;

    @FXML
    private TextField searchField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField dateFromField;

    @FXML
    private TextField dateToField;

    private EventDAO eventDAO;
    private ObservableList<Event> events;
    private ObservableList<Event> allEvents;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventDAO = new EventDAO();
        events = FXCollections.observableArrayList();
        allEvents = FXCollections.observableArrayList();
        errorLabel.setText("");

        // Set up table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("ticketPrice"));
        priceColumn.setCellFactory(column -> new TableCell<Event, Double>() {
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
        seatsColumn.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));

        // Format date column
        dateColumn.setCellValueFactory(cellData -> {
            Event event = cellData.getValue();
            if (event.getDateTime() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    event.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        // Add action buttons column
        actionColumn.setCellFactory(param -> new TableCell<Event, Void>() {
            private final Button viewButton = new Button("View Details");
            private final Button bookButton = new Button("Book Ticket");
            private final HBox buttonBox = new HBox(5, viewButton, bookButton);

            {
                viewButton.setOnAction(event -> {
                    Event selectedEvent = getTableView().getItems().get(getIndex());
                    handleViewDetails(selectedEvent);
                });

                bookButton.setOnAction(event -> {
                    Event selectedEvent = getTableView().getItems().get(getIndex());
                    handleBookTicket(selectedEvent);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Event event = getTableView().getItems().get(getIndex());
                    bookButton.setDisable(!event.hasAvailableSeats());
                    setGraphic(buttonBox);
                }
            }
        });

        // Load events
        loadEvents();
    }

    /**
     * Load all events from database
     */
    private void loadEvents() {
        allEvents.clear();
        allEvents.addAll(eventDAO.getAllEvents());
        events.clear();
        events.addAll(allEvents);
        eventsTable.setItems(events);
    }

    /**
     * Handle search field input with location and time filters
     */
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        String locationFilter = locationField.getText().toLowerCase().trim();
        String dateFrom = dateFromField.getText().trim();
        String dateTo = dateToField.getText().trim();
        
        events.clear();
        
        for (Event event : allEvents) {
            // Only show active events
            if (!event.isActive()) {
                continue;
            }
            
            // Name search filter
            boolean matchesName = searchText.isEmpty() || 
                event.getName().toLowerCase().contains(searchText) ||
                (event.getDescription() != null && event.getDescription().toLowerCase().contains(searchText));
            
            // Location filter
            boolean matchesLocation = locationFilter.isEmpty() || 
                event.getLocation().toLowerCase().contains(locationFilter);
            
            // Date range filter
            boolean matchesDate = true;
            if (!dateFrom.isEmpty() || !dateTo.isEmpty()) {
                try {
                    LocalDateTime eventDate = event.getDateTime();
                    if (eventDate != null) {
                        LocalDate eventLocalDate = eventDate.toLocalDate();
                        
                        if (!dateFrom.isEmpty()) {
                            LocalDate fromDate = LocalDate.parse(dateFrom, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            if (eventLocalDate.isBefore(fromDate)) {
                                matchesDate = false;
                            }
                        }
                        
                        if (!dateTo.isEmpty() && matchesDate) {
                            LocalDate toDate = LocalDate.parse(dateTo, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            if (eventLocalDate.isAfter(toDate)) {
                                matchesDate = false;
                            }
                        }
                    }
                } catch (DateTimeParseException e) {
                    // Invalid date format, ignore filter
                }
            }
            
            if (matchesName && matchesLocation && matchesDate) {
                events.add(event);
            }
        }
        
        eventsTable.setItems(events);
    }

    /**
     * Handle view details button click
     */
    private void handleViewDetails(Event event) {
        EventDetailsController.setSelectedEvent(event);
        SceneManager.switchScene("/fxml/EventDetails.fxml");
    }

    /**
     * Handle book ticket button click
     */
    private void handleBookTicket(Event event) {
        if (!UserSession.getInstance().isLoggedIn()) {
            errorLabel.setText("Please login to book tickets");
            return;
        }

        // Navigate to book ticket screen with event ID
        BookTicketController.setSelectedEvent(event);
        SceneManager.switchScene("/fxml/BookTicket.fxml");
    }

    /**
     * Handle back button click
     */
    @FXML
    private void handleBack() {
        SceneManager.switchScene("/fxml/UserDashboard.fxml");
    }
}

