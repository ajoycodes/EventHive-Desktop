package app.controllers;

import app.dao.EventDAO;
import app.models.Event;
import app.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller for Admin Events view
 */
public class AdminEventsController implements Initializable {
    @FXML
    private TableView<Event> eventsTable;

    @FXML
    private TableColumn<Event, String> nameColumn;

    @FXML
    private TableColumn<Event, String> dateColumn;

    @FXML
    private TableColumn<Event, String> locationColumn;

    @FXML
    private TableColumn<Event, String> statusColumn;

    @FXML
    private TableColumn<Event, Integer> seatsColumn;

    @FXML
    private Label errorLabel;

    private EventDAO eventDAO;
    private ObservableList<Event> events;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventDAO = new EventDAO();
        events = FXCollections.observableArrayList();

        // Set up table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
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

        loadEvents();
    }

    /**
     * Load all events
     */
    private void loadEvents() {
        events.clear();
        events.addAll(eventDAO.getAllEvents());
        eventsTable.setItems(events);
    }

    /**
     * Handle back button click
     */
    @FXML
    private void handleBack() {
        SceneManager.switchScene("/fxml/AdminDashboard.fxml");
    }
}

