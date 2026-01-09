package app.controllers;

import app.dao.UserDAO;
import app.models.User;
import app.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
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
    private PasswordField confirmPasswordField;

    @FXML
    private CheckBox userRoleCheckBox;

    @FXML
    private CheckBox organizerRoleCheckBox;

    @FXML
    private CheckBox adminRoleCheckBox;

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
        String confirmPassword = confirmPasswordField.getText();

        // Build role string from checkboxes
        StringBuilder roleBuilder = new StringBuilder();
        if (userRoleCheckBox.isSelected()) {
            roleBuilder.append("USER");
        }
        if (organizerRoleCheckBox.isSelected()) {
            if (roleBuilder.length() > 0) roleBuilder.append(",");
            roleBuilder.append("ORGANIZER");
        }
        if (adminRoleCheckBox.isSelected()) {
            if (roleBuilder.length() > 0) roleBuilder.append(",");
            roleBuilder.append("ADMIN");
        }
        String role = roleBuilder.toString();

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Please fill in all fields");
            return;
        }

        if (role.isEmpty()) {
            errorLabel.setText("Please select at least one role");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match");
            return;
        }

        if (password.length() < 6) {
            errorLabel.setText("Password must be at least 6 characters");
            return;
        }

        if (!isValidEmail(email)) {
            errorLabel.setText("Invalid email format");
            return;
        }

        // Check if username already exists
        if (userDAO.usernameExists(username)) {
            errorLabel.setText("Username already exists");
            return;
        }

        // For email, if it exists, we'll add roles to existing user instead of creating new
        User existingUser = userDAO.findByEmail(email);
        if (existingUser != null) {
            // User exists, add new roles
            existingUser.addRole(role);
            // Update user with new roles
            if (userDAO.updateUserRoles(existingUser.getId(), existingUser.getRole())) {
                SceneManager.switchScene("/fxml/Login.fxml");
                return;
            } else {
                errorLabel.setText("Error updating account. Please try again.");
                return;
            }
        }

        // Create new user
        User newUser = new User(username, email, password, role);
        boolean success = userDAO.createUser(newUser);

        if (success) {
            // Navigate back to login
            SceneManager.switchScene("/fxml/Login.fxml");
        } else {
            errorLabel.setText("Error creating account. Please try again.");
        }
    }

    /**
     * Handle back button click - navigate to login screen
     */
    @FXML
    private void handleBack() {
        SceneManager.switchScene("/fxml/Login.fxml");
    }

    /**
     * Basic email validation
     */
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }
}

