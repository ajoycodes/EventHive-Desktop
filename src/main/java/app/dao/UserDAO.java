package app.dao;

import app.models.User;
import app.utils.DatabaseConnection;
import app.utils.PasswordHasher;
import app.utils.TokenGenerator;
import app.utils.Constants;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Data Access Object for User operations
 */
public class UserDAO {
    /**
     * Create a new user
     */
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Find user by username
     */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
        }

        return null;
    }

    /**
     * Find user by email
     */
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding user by email: " + e.getMessage());
        }

        return null;
    }

    /**
     * Authenticate user (check username/email and password)
     * Now uses BCrypt to verify hashed passwords
     */
    public User authenticate(String usernameOrEmail, String password) {
        String sql = "SELECT * FROM users WHERE (username = ? OR email = ?)";
        System.out.println("[UserDAO] Authenticating: " + usernameOrEmail);

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usernameOrEmail);
            pstmt.setString(2, usernameOrEmail);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = mapResultSetToUser(rs);

                // Verify password using BCrypt
                if (PasswordHasher.verifyPassword(password, user.getPassword())) {
                    System.out.println("[UserDAO] Authentication successful.");
                    return user;
                } else {
                    System.out.println("[UserDAO] Password verification failed.");
                    return null;
                }
            } else {
                System.out.println("[UserDAO] User NOT found.");
            }

        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get user by ID
     */
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Check if username exists
     */
    public boolean usernameExists(String username) {
        return findByUsername(username) != null;
    }

    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        return findByEmail(email) != null;
    }

    /**
     * Update user roles
     */
    public boolean updateUserRoles(int userId, String roles) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, roles);
            pstmt.setInt(2, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating user roles: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all users (for admin)
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username ASC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }

        return users;
    }

    /**
     * Delete user (admin only)
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Record a login attempt for security tracking
     */
    public void recordLoginAttempt(String username, boolean success) {
        String sql = "INSERT INTO login_attempts (username, success) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setBoolean(2, success);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error recording login attempt: " + e.getMessage());
        }
    }

    /**
     * Get failed login count for a user in the last 15 minutes
     */
    public int getRecentFailedLoginCount(String username) {
        String sql = "SELECT COUNT(*) FROM login_attempts WHERE username = ? AND success = 0 " +
                "AND attempted_at > datetime('now', '-15 minutes')";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting failed login count: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Lock a user account for specified minutes
     */
    public boolean lockAccount(int userId, int minutes) {
        String sql = "UPDATE users SET account_status = ?, locked_until = datetime('now', '+" + minutes + " minutes') "
                +
                "WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, Constants.STATUS_LOCKED);
            pstmt.setInt(2, userId);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error locking account: " + e.getMessage());
        }

        return false;
    }

    /**
     * Check if account is currently locked
     */
    public boolean isAccountLocked(int userId) {
        String sql = "SELECT account_status, locked_until FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String status = rs.getString("account_status");
                Timestamp lockedUntil = rs.getTimestamp("locked_until");

                if (Constants.STATUS_LOCKED.equals(status)) {
                    // Check if lock has expired
                    if (lockedUntil != null && lockedUntil.getTime() > System.currentTimeMillis()) {
                        return true;
                    } else {
                        // Unlock account if lock period expired
                        unlockAccount(userId);
                        return false;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking account lock: " + e.getMessage());
        }

        return false;
    }

    /**
     * Unlock a user account
     */
    public boolean unlockAccount(int userId) {
        String sql = "UPDATE users SET account_status = ?, locked_until = NULL, failed_login_attempts = 0 " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, Constants.STATUS_ACTIVE);
            pstmt.setInt(2, userId);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error unlocking account: " + e.getMessage());
        }

        return false;
    }

    /**
     * Update last login timestamp
     */
    public boolean updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP, failed_login_attempts = 0 WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
        }

        return false;
    }

    /**
     * Increment failed login attempts
     */
    public boolean incrementFailedAttempts(int userId) {
        String sql = "UPDATE users SET failed_login_attempts = failed_login_attempts + 1 WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error incrementing failed attempts: " + e.getMessage());
        }

        return false;
    }

    /**
     * Create a password reset token
     */
    public String createPasswordResetToken(int userId) {
        String token = TokenGenerator.generateResetToken();
        long expiryTime = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(Constants.RESET_TOKEN_EXPIRY_HOURS);
        Timestamp expiresAt = new Timestamp(expiryTime);

        String sql = "INSERT INTO password_reset_tokens (user_id, token, expires_at) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, token);
            pstmt.setTimestamp(3, expiresAt);
            pstmt.executeUpdate();

            return token;

        } catch (SQLException e) {
            System.err.println("Error creating reset token: " + e.getMessage());
        }

        return null;
    }

    /**
     * Validate a password reset token
     */
    public Integer validateResetToken(String token) {
        String sql = "SELECT user_id, expires_at, used FROM password_reset_tokens WHERE token = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, token);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                boolean used = rs.getBoolean("used");
                Timestamp expiresAt = rs.getTimestamp("expires_at");

                if (used) {
                    return null; // Token already used
                }

                if (expiresAt.getTime() < System.currentTimeMillis()) {
                    return null; // Token expired
                }

                return rs.getInt("user_id");
            }

        } catch (SQLException e) {
            System.err.println("Error validating reset token: " + e.getMessage());
        }

        return null;
    }

    /**
     * Update user password (with hashed password)
     */
    public boolean updatePassword(int userId, String newHashedPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newHashedPassword);
            pstmt.setInt(2, userId);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
        }

        return false;
    }

    /**
     * Mark a reset token as used
     */
    public boolean markTokenAsUsed(String token) {
        String sql = "UPDATE password_reset_tokens SET used = 1 WHERE token = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, token);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error marking token as used: " + e.getMessage());
        }

        return false;
    }

    /**
     * Cleanup expired password reset tokens
     */
    public void cleanupExpiredTokens() {
        String sql = "DELETE FROM password_reset_tokens WHERE expires_at < CURRENT_TIMESTAMP";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            int deleted = stmt.executeUpdate(sql);
            if (deleted > 0) {
                System.out.println("Cleaned up " + deleted + " expired tokens");
            }

        } catch (SQLException e) {
            System.err.println("Error cleaning up tokens: " + e.getMessage());
        }
    }

    /**
     * Map ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        return user;
    }
}
