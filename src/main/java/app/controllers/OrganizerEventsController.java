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
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller for the Organizer Events screen
 * Displays all events created by the current organizer
 */
public class OrganizerEventsController implements Initializable {
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

    private EventDAO eventDAO;
    private ObservableList<Event> events;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventDAO = new EventDAO();
        events = FXCollections.observableArrayList();
        errorLabel.setText("");

        // Set up table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("ticketPrice"));
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

        // Add status column
        TableColumn<Event, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> {
            Event event = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(event.getStatus());
        });
        eventsTable.getColumns().add(statusColumn);

        // Add action buttons column
        actionColumn.setCellFactory(param -> new TableCell<Event, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button statusButton = new Button("Status");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonBox = new HBox(5, editButton, statusButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    Event selectedEvent = getTableView().getItems().get(getIndex());
                    handleEditEvent(selectedEvent);
                });

                statusButton.setOnAction(event -> {
                    Event selectedEvent = getTableView().getItems().get(getIndex());
                    handleChangeStatus(selectedEvent);
                });

                deleteButton.setOnAction(event -> {
                    Event selectedEvent = getTableView().getItems().get(getIndex());
                    handleDeleteEvent(selectedEvent);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Event event = getTableView().getItems().get(getIndex());
                    // Update status button text based on current status
                    String currentStatus = event.getStatus();
                    if ("ACTIVE".equals(currentStatus)) {
                        statusButton.setText("Hold");
                    } else if ("HOLD".equals(currentStatus)) {
                        statusButton.setText("Activate");
                    } else {
                        statusButton.setText("Status");
                    }
                    setGraphic(buttonBox);
                }
            }
        });

        // Load events
        loadEvents();
    }

    /**
     * Load events for the current organizer
     */
    private void loadEvents() {
        if (!UserSession.getInstance().isLoggedIn()) {
            errorLabel.setText("Please login");
            return;
        }

        int organizerId = UserSession.getInstance().getCurrentUser().getId();
        events.clear();
        events.addAll(eventDAO.getEventsByOrganizer(organizerId));
        eventsTable.setItems(events);
    }

    /**
     * Handle edit event button click
     */
    private void handleEditEvent(Event event) {
        EditEventController.setSelectedEvent(event);
        SceneManager.switchScene("/fxml/EditEvent.fxml");
    }

    /**
     * Handle change status button click
     */
    private void handleChangeStatus(Event event) {
        String currentStatus = event.getStatus();
        
        // Show status selection dialog
        Alert statusAlert = new Alert(Alert.AlertType.CONFIRMATION);
        statusAlert.setTitle("Change Event Status");
        statusAlert.setHeaderText("Select New Status for: " + event.getName());
        
        // Create buttons for each status
        statusAlert.getButtonTypes().clear();
        if (!"ACTIVE".equals(currentStatus)) {
            statusAlert.getButtonTypes().add(new ButtonType("ACTIVE"));
        }
        if (!"HOLD".equals(currentStatus)) {
            statusAlert.getButtonTypes().add(new ButtonType("HOLD"));
        }
        if (!"CANCELLED".equals(currentStatus)) {
            statusAlert.getButtonTypes().add(new ButtonType("CANCELLED"));
        }
        statusAlert.getButtonTypes().add(ButtonType.CANCEL);

        statusAlert.showAndWait().ifPresent(response -> {
            if (response != ButtonType.CANCEL) {
                String newStatus = response.getText();
                boolean success = eventDAO.updateEventStatus(event.getId(), newStatus);
                if (success) {
                    event.setStatus(newStatus);
                    loadEvents(); // Refresh the table
                } else {
                    errorLabel.setText("Error changing event status");
                }
            }
        });
    }

    /**
     * Handle delete event button click
     */
    private void handleDeleteEvent(Event event) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Event");
        confirmAlert.setContentText("Are you sure you want to delete this event? This action cannot be undone.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = eventDAO.deleteEvent(event.getId());
                if (success) {
                    loadEvents(); // Refresh the table
                } else {
                    errorLabel.setText("Error deleting event");
                }
            }
        });
    }

    /**
     * Handle create event button click
     */
    @FXML
    private void handleCreateEvent() {
        SceneManager.switchScene("/fxml/CreateEvent.fxml");
    }

    /**
     * Handle back button click
     */
    @FXML
    private void handleBack() {
        SceneManager.switchScene("/fxml/OrganizerDashboard.fxml");
    }
}

