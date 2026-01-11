package app.utils;

import java.sql.*;

/**
 * Utility class for calculating dashboard statistics
 */
public class DashboardStats {

    /**
     * Inner class to hold admin statistics
     */
    public static class AdminStats {
        public int totalUsers;
        public int totalEvents;
        public int totalTickets;
        public double totalRevenue;
    }

    /**
     * Inner class to hold organizer statistics
     */
    public static class OrganizerStats {
        public int totalEvents;
        public int activeEvents;
        public int totalTicketsSold;
        public double totalRevenue;
    }

    /**
     * Inner class to hold user statistics
     */
    public static class UserStats {
        public int totalTickets;
        public double totalSpent;
    }

    /**
     * Get admin statistics
     */
    public static AdminStats getAdminStats() {
        AdminStats stats = new AdminStats();
        stats.totalUsers = getTotalUsers();
        stats.totalEvents = getTotalEvents();
        stats.totalTickets = getTotalTickets();
        stats.totalRevenue = getTotalRevenue();
        return stats;
    }

    /**
     * Get user statistics
     */
    public static UserStats getUserStats(int userId) {
        UserStats stats = new UserStats();
        stats.totalTickets = getTicketsByUser(userId);
        stats.totalSpent = getTotalSpentByUser(userId);
        return stats;
    }

    /**
     * Get organizer statistics
     */
    public static OrganizerStats getOrganizerStats(int organizerId) {
        OrganizerStats stats = new OrganizerStats();
        stats.totalEvents = getEventsByOrganizer(organizerId);
        stats.activeEvents = getActiveEventsByOrganizer(organizerId);
        stats.totalTicketsSold = getTicketsSoldByOrganizer(organizerId);
        stats.totalRevenue = getRevenueByOrganizer(organizerId);
        return stats;
    }

    /**
     * Get total number of users in the system
     */
    public static int getTotalUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total users: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get total number of events in the system
     */
    public static int getTotalEvents() {
        String sql = "SELECT COUNT(*) FROM events";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total events: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get number of active events
     */
    public static int getActiveEvents() {
        String sql = "SELECT COUNT(*) FROM events WHERE status = 'Active'";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting active events: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get total number of tickets sold
     */
    public static int getTotalTickets() {
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM tickets";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total tickets: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get total revenue from ticket sales
     */
    public static double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_price), 0) FROM tickets";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total revenue: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Get number of events created by a specific organizer
     */
    public static int getEventsByOrganizer(int organizerId) {
        String sql = "SELECT COUNT(*) FROM events WHERE organizer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, organizerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting events by organizer: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get number of active events by a specific organizer
     */
    public static int getActiveEventsByOrganizer(int organizerId) {
        String sql = "SELECT COUNT(*) FROM events WHERE organizer_id = ? AND status = 'Active'";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, organizerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting active events by organizer: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get number of tickets purchased by a specific user
     */
    public static int getTicketsByUser(int userId) {
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM tickets WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting tickets by user: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get revenue from events organized by a specific organizer
     */
    public static double getRevenueByOrganizer(int organizerId) {
        String sql = """
                    SELECT COALESCE(SUM(t.total_price), 0)
                    FROM tickets t
                    JOIN events e ON t.event_id = e.id
                    WHERE e.organizer_id = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, organizerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting revenue by organizer: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Get tickets sold for events by a specific organizer
     */
    public static int getTicketsSoldByOrganizer(int organizerId) {
        String sql = """
                    SELECT COALESCE(SUM(t.quantity), 0)
                    FROM tickets t
                    JOIN events e ON t.event_id = e.id
                    WHERE e.organizer_id = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, organizerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting tickets sold by organizer: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get total amount spent by a specific user
     */
    public static double getTotalSpentByUser(int userId) {
        String sql = "SELECT COALESCE(SUM(total_price), 0) FROM tickets WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total spent by user: " + e.getMessage());
        }
        return 0.0;
    }
}
