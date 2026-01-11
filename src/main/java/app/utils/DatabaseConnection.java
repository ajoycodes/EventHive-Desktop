package app.utils;

import java.sql.*;

/**
 * Manages SQLite database connection and initialization
 */
public class DatabaseConnection {
    // Use user.home for portability across OS
    private static final String APP_DIR = System.getProperty("user.home") + "/.eventhive";
    private static final String DB_URL = "jdbc:sqlite:" + APP_DIR + "/eventhive.db";
    private static Connection connection = null;

    /**
     * Get database connection (singleton pattern)
     */
    public static Connection getConnection() {
        try {
            // Ensure application directory exists
            java.io.File directory = new java.io.File(APP_DIR);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    System.out.println("Created application directory: " + APP_DIR);
                }
            }

            // Load SQLite driver explicitly
            Class.forName("org.sqlite.JDBC");

            if (connection == null || connection.isClosed()) {
                System.out.println("Connecting to database at: " + DB_URL);
                connection = DriverManager.getConnection(DB_URL);
            }
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("CRITICAL ERROR: Could not connect to database at " + DB_URL);
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Initialize database schema and default data
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            // Create users table
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS users (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            username TEXT UNIQUE NOT NULL,
                            email TEXT UNIQUE NOT NULL,
                            password TEXT NOT NULL,
                            role TEXT NOT NULL DEFAULT 'user',
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        )
                    """);

            // Create events table
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS events (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            title TEXT NOT NULL,
                            description TEXT,
                            location TEXT NOT NULL,
                            event_date TIMESTAMP NOT NULL,
                            total_seats INTEGER NOT NULL,
                            available_seats INTEGER NOT NULL,
                            ticket_price REAL NOT NULL,
                            organizer_id INTEGER NOT NULL,
                            status TEXT DEFAULT 'Active',
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (organizer_id) REFERENCES users(id)
                        )
                    """);

            // Create tickets table
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS tickets (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            event_id INTEGER NOT NULL,
                            user_id INTEGER NOT NULL,
                            ticket_code TEXT UNIQUE NOT NULL,
                            quantity INTEGER NOT NULL DEFAULT 1,
                            total_price REAL NOT NULL,
                            booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            status TEXT DEFAULT 'Active',
                            FOREIGN KEY (event_id) REFERENCES events(id),
                            FOREIGN KEY (user_id) REFERENCES users(id)
                        )
                    """);

            // Insert default users if not exists
            insertDefaultUsers(conn);

            System.out.println("Database initialized successfully!");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Insert default users for testing
     */
    private static void insertDefaultUsers(Connection conn) throws SQLException {
        String checkUserSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        String insertUserSql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";

        // Default admin
        try (PreparedStatement checkStmt = conn.prepareStatement(checkUserSql)) {
            checkStmt.setString(1, "admin");
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertUserSql)) {
                    insertStmt.setString(1, "admin");
                    insertStmt.setString(2, "admin@eventhive.com");
                    insertStmt.setString(3, "admin123");
                    insertStmt.setString(4, "admin");
                    insertStmt.executeUpdate();
                    System.out.println("Default admin user created");
                }
            }
        }

        // Default organizer
        try (PreparedStatement checkStmt = conn.prepareStatement(checkUserSql)) {
            checkStmt.setString(1, "organizer1");
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertUserSql)) {
                    insertStmt.setString(1, "organizer1");
                    insertStmt.setString(2, "organizer1@eventhive.com");
                    insertStmt.setString(3, "org123");
                    insertStmt.setString(4, "organizer");
                    insertStmt.executeUpdate();
                    System.out.println("Default organizer user created");
                }
            }
        }

        // Default user
        try (PreparedStatement checkStmt = conn.prepareStatement(checkUserSql)) {
            checkStmt.setString(1, "user1");
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertUserSql)) {
                    insertStmt.setString(1, "user1");
                    insertStmt.setString(2, "user1@eventhive.com");
                    insertStmt.setString(3, "user123");
                    insertStmt.setString(4, "user");
                    insertStmt.executeUpdate();
                    System.out.println("Default user created");
                }
            }
        }
    }

    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
