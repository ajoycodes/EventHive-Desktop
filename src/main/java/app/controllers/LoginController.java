package app.controllers;

import app.dao.UserDAO;
import app.models.User;
import app.utils.SceneManager;
import app.utils.UserSession;
import app.utils.Constants;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.util.Optional;

/**
 * Controller for the Login screen
 */
public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private UserDAO userDAO;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        errorLabel.setText("");
    }

    /**
     * Handle login button click
     */
    @FXML
    private void handleLogin() {
        String usernameOrEmail = usernameField.getText().trim();
        String password = passwordField.getText();

        System.out.println("[LoginController] Login attempt for: " + usernameOrEmail);

        // Clear previous error
        errorLabel.setText("");

        // Validation
        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            System.out.println("[LoginController] Validation failed: Empty fields");
            errorLabel.setText("Please fill in all fields");
            return;
        }

        // Check if account exists first (for lockout checking)
        User existingUser = userDAO.findByUsername(usernameOrEmail);
        if (existingUser == null) {
            existingUser = userDAO.findByEmail(usernameOrEmail);
        }

        // Check if account is locked
        if (existingUser != null && userDAO.isAccountLocked(existingUser.getId())) {
            errorLabel.setText("Account is temporarily locked. Please try again later.");
            showAlert("Account Locked",
                    "Your account has been locked due to multiple failed login attempts.\n" +
                            "Please try again in " + Constants.ACCOUNT_LOCK_MINUTES + " minutes.");
            return;
        }

        // Check recent failed login attempts (even if user doesn't exist)
        int recentFailures = userDAO.getRecentFailedLoginCount(usernameOrEmail);
        if (recentFailures >= Constants.MAX_LOGIN_ATTEMPTS) {
            errorLabel.setText("Too many failed attempts. Please try again later.");
            showAlert("Too Many Attempts",
                    "Too many failed login attempts. Please try again in a few minutes.");
            return;
        }

        // Authenticate user
        try {
            User user = userDAO.authenticate(usernameOrEmail, password);

            if (user != null) {
                System.out.println("[LoginController] Authentication successful for user: " + user.getUsername()
                        + ", Roles: " + user.getRole());

                // Record successful login
                userDAO.recordLoginAttempt(usernameOrEmail, true);
                userDAO.updateLastLogin(user.getId());

                // Set current user session
                UserSession.getInstance().setCurrentUser(user);

                // Check if user has multiple roles
                String[] roles = user.getRoles();
                if (roles.length > 1) {
                    System.out.println("[LoginController] User has multiple roles, showing dialog...");
                    // User has multiple roles, show selection dialog
                    String selectedRole = showRoleSelectionDialog(roles);
                    if (selectedRole != null) {
                        System.out.println("[LoginController] Role selected: " + selectedRole);
                        navigateToDashboard(selectedRole);
                    } else {
                        System.out.println("[LoginController] Role selection cancelled");
                    }
                } else {
                    // Single role, navigate directly
                    String role = roles.length > 0 ? roles[0] : "USER";
                    System.out.println("[LoginController] Single role found: " + role);
                    navigateToDashboard(role);
                }
            } else {
                System.out.println("[LoginController] Authentication failed: Invalid credentials");

                // Record failed login
                userDAO.recordLoginAttempt(usernameOrEmail, false);

                if (existingUser != null) {
                    // Increment failed attempts
                    userDAO.incrementFailedAttempts(existingUser.getId());

                    // Check if we should lock the account
                    int totalFailures = userDAO.getRecentFailedLoginCount(usernameOrEmail);
                    if (totalFailures >= Constants.MAX_LOGIN_ATTEMPTS - 1) {
                        userDAO.lockAccount(existingUser.getId(), Constants.ACCOUNT_LOCK_MINUTES);
                        errorLabel.setText("Account locked due to multiple failed attempts.");
                    } else {
                        int attemptsLeft = Constants.MAX_LOGIN_ATTEMPTS - totalFailures - 1;
                        errorLabel.setText("Invalid credentials. " + attemptsLeft + " attempts remaining.");
                    }
                } else {
                    errorLabel.setText("Invalid username/email or password");
                }
            }
        } catch (Exception e) {
            System.err.println("Login Error: " + e.getMessage());
            e.printStackTrace();
            errorLabel.setText("An error occurred. Please try again.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Show role selection dialog for users with multiple roles
     */
    private String showRoleSelectionDialog(String[] roles) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Select Role");
        alert.setHeaderText("You have multiple roles. Please select which role to login as:");

        StringBuilder content = new StringBuilder();
        for (int i = 0; i < roles.length; i++) {
            content.append((i + 1)).append(". ").append(roles[i].trim()).append("\n");
        }
        alert.setContentText(content.toString());

        // Create custom buttons for each role
        alert.getButtonTypes().clear();
        for (String role : roles) {
            alert.getButtonTypes().add(new ButtonType(role.trim()));
        }
        alert.getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() != ButtonType.CANCEL) {
            return result.get().getText();
        }
        return null;
    }

    /**
     * Navigate to appropriate dashboard based on role
     */
    private void navigateToDashboard(String role) {
        System.out.println("[LoginController] Navigating to dashboard for role: " + role);
        switch (role.toUpperCase()) {
            case "ADMIN":
                SceneManager.switchScene("/fxml/AdminDashboard.fxml");
                break;
            case "ORGANIZER":
                SceneManager.switchScene("/fxml/OrganizerDashboard.fxml");
                break;
            case "USER":
            default:
                SceneManager.switchScene("/fxml/UserDashboard.fxml");
                break;
        }
    }

    /**
     * Handle signup button click - navigate to signup screen
     */
    @FXML
    private void handleSignup() {
        SceneManager.switchScene("/fxml/Signup.fxml");
    }

    /**
     * Handle forgot password link click - navigate to forgot password screen
     */
    @FXML
    private void handleForgotPassword() {
        SceneManager.switchScene("/fxml/ForgotPassword.fxml");
    }
}
