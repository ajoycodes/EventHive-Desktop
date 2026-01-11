package app.controllers;

import app.dao.UserDAO;
import app.models.User;
import app.utils.SceneManager;
import app.utils.PasswordHasher;
import app.utils.ValidationUtils;
import app.utils.Constants;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for the Signup screen
 */
public class SignupController {
    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Label errorLabel;

    private UserDAO userDAO;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        errorLabel.setText("");
    }

    /**
     * Handle signup button click
     */
    @FXML
    private void handleSignup() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        // Clear previous errors
        errorLabel.setText("");

        // Validation
        if (ValidationUtils.isEmpty(username) || ValidationUtils.isEmpty(email) ||
                ValidationUtils.isEmpty(password)) {
            errorLabel.setText("Please fill in all fields");
            return;
        }

        if (role == null || role.isEmpty()) {
            errorLabel.setText("Please select a role");
            return;
        }

        // Validate username format
        if (!ValidationUtils.isValidUsername(username)) {
            errorLabel.setText("Username must be 3-20 characters, alphanumeric and underscore only");
            return;
        }

        // Validate email format
        if (!ValidationUtils.isValidEmail(email)) {
            errorLabel.setText("Please enter a valid email address");
            return;
        }

        // Check password strength
        PasswordHasher.PasswordStrength strength = PasswordHasher.checkPasswordStrength(password);
        if (!strength.isValid()) {
            errorLabel.setText(strength.getMessage());
            return;
        }

        // Check if username already exists
        if (userDAO.usernameExists(username)) {
            errorLabel.setText("Username already exists");
            return;
        }

        // For email, if it exists, we'll add roles to existing user instead of creating
        // new
        User existingUser = userDAO.findByEmail(email);
        if (existingUser != null) {
            // User exists, add new roles
            existingUser.addRole(role);
            // Update user with new roles
            if (userDAO.updateUserRoles(existingUser.getId(), existingUser.getRole())) {
                showSuccess("Role added successfully! You can now login.");
                SceneManager.switchScene("/fxml/Login.fxml");
                return;
            } else {
                errorLabel.setText("Error updating account. Please try again.");
                return;
            }
        }

        // Hash the password before storing
        String hashedPassword = PasswordHasher.hashPassword(password);

        // Create new user with hashed password
        User newUser = new User(username, email, hashedPassword, role);
        boolean success = userDAO.createUser(newUser);

        if (success) {
            showSuccess("Account created successfully! Please login.");
            // Navigate back to login
            SceneManager.switchScene("/fxml/Login.fxml");
        } else {
            errorLabel.setText("Error creating account. Please try again.");
        }
    }

    /**
     * Show success dialog
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Handle back button click - navigate to login screen
     */
    @FXML
    private void handleLogin() {
        SceneManager.switchScene("/fxml/Login.fxml");
    }
}
