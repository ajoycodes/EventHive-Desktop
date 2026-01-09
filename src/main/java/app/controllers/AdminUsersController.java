package app.controllers;

import app.dao.UserDAO;
import app.models.User;
import app.utils.SceneManager;
import app.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for Admin Users Management screen
 */
public class AdminUsersController implements Initializable {
    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, Void> actionColumn;

    @FXML
    private Label errorLabel;

    private UserDAO userDAO;
    private ObservableList<User> users;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userDAO = new UserDAO();
        users = FXCollections.observableArrayList();

        // Set up table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Add action button column
        actionColumn.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    User selectedUser = getTableView().getItems().get(getIndex());
                    handleDeleteUser(selectedUser);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    // Don't allow deleting yourself
                    if (UserSession.getInstance().isLoggedIn() && 
                        user.getId() == UserSession.getInstance().getCurrentUser().getId()) {
                        deleteButton.setDisable(true);
                    } else {
                        deleteButton.setDisable(false);
                    }
                    setGraphic(deleteButton);
                }
            }
        });

        loadUsers();
    }

    /**
     * Load all users
     */
    private void loadUsers() {
        users.clear();
        users.addAll(userDAO.getAllUsers());
        usersTable.setItems(users);
    }

    /**
     * Handle delete user button click
     */
    private void handleDeleteUser(User user) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete User");
        confirmAlert.setContentText("Are you sure you want to delete user: " + user.getUsername() + "?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = userDAO.deleteUser(user.getId());
                if (success) {
                    loadUsers(); // Refresh the table
                } else {
                    errorLabel.setText("Error deleting user");
                }
            }
        });
    }

    /**
     * Handle back button click
     */
    @FXML
    private void handleBack() {
        SceneManager.switchScene("/fxml/AdminDashboard.fxml");
    }
}

