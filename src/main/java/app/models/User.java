package app.models;

/**
 * User model representing a user in the system
 * Supports different roles: USER, ORGANIZER, ADMIN
 */
public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private String role; // USER, ORGANIZER, ADMIN

    public User() {
    }

    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(int id, String username, String email, String password, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isOrganizer() {
        return role != null && (role.toUpperCase().contains("ORGANIZER") || "ORGANIZER".equalsIgnoreCase(role));
    }

    public boolean isAdmin() {
        return role != null && (role.toUpperCase().contains("ADMIN") || "ADMIN".equalsIgnoreCase(role));
    }

    public boolean isUser() {
        return role != null && (role.toUpperCase().contains("USER") || "USER".equalsIgnoreCase(role));
    }

    /**
     * Check if user has a specific role
     */
    public boolean hasRole(String roleToCheck) {
        if (role == null)
            return false;
        String[] roles = role.split(",");
        for (String r : roles) {
            if (r.trim().equalsIgnoreCase(roleToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all roles as array
     */
    public String[] getRoles() {
        if (role == null || role.isEmpty())
            return new String[0];
        return role.split(",");
    }

    /**
     * Add a role to the user
     */
    public void addRole(String newRole) {
        if (role == null || role.isEmpty()) {
            role = newRole;
        } else if (!hasRole(newRole)) {
            role += "," + newRole;
        }
    }
}
