package app.controllers;

import app.dao.UserDAO;
import app.models.User;
import app.utils.SceneManager;
import app.utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller for the Forgot Password screen
 * Handles password reset requests
 */
public class ForgotPasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private Label messageLabel;

    private UserDAO userDAO;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        messageLabel.setText("");
    }

    /**
     * Handle submit button click - generate reset token
     */
    @FXML
    private void handleSubmit() {
        String email = emailField.getText().trim();

        // Clear previous message
        messageLabel.setText("");
        messageLabel.setStyle("");

        // Validation
        if (ValidationUtils.isEmpty(email)) {
            showError("Please enter your email address");
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            showError("Please enter a valid email address");
            return;
        }

        // Find user by email
        User user = userDAO.findByEmail(email);

        if (user != null) {
            // Generate reset token
            String token = userDAO.createPasswordResetToken(user.getId());

            if (token != null) {
                // Show success message with token (simulating email)
                showSuccessDialog(user.getEmail(), token);

                // Show success message on screen
                messageLabel.setText("Password reset instructions sent! Check the dialog for your reset token.");
                messageLabel.setStyle("-fx-text-fill: green;");

                // Clear the email field
                emailField.clear();
            } else {
                showError("Error generating reset token. Please try again.");
            }
        } else {
            // For security reasons, show same message even if email doesn't exist
            // This prevents email enumeration attacks
            messageLabel.setText("If an account exists with this email, you will receive reset instructions.");
            messageLabel.setStyle("-fx-text-fill: green;");
            emailField.clear();
        }
    }

    /**
     * Show success dialog with reset token (simulating email)
     */
    private void showSuccessDialog(String email, String token) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Password Reset");
        alert.setHeaderText("Password Reset Token Generated");
        alert.setContentText(
                "🔐 PASSWORD RESET EMAIL SIMULATION\n\n" +
                        "To: " + email + "\n\n" +
                        "Your password reset token is:\n\n" +
                        token + "\n\n" +
                        "This token will expire in 1 hour.\n\n" +
                        "To reset your password:\n" +
                        "1. Copy this token\n" +
                        "2. Navigate to the login screen\n" +
                        "3. Contact an administrator with this token\n\n" +
                        "Note: In a real application, this would be sent via email.");
        alert.showAndWait();
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: red;");
    }

    /**
     * Handle back to login link click
     */
    @FXML
    private void handleBackToLogin() {
        SceneManager.switchScene("/fxml/Login.fxml");
    }
}
