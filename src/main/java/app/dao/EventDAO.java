package app.dao;

import app.models.Event;
import app.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Event operations
 */
public class EventDAO {
    /**
     * Create a new event
     */
    public boolean createEvent(Event event) {
        String sql = "INSERT INTO events (title, event_date, location, description, ticket_price, total_seats, available_seats, organizer_id, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, event.getName());
            pstmt.setTimestamp(2, Timestamp.valueOf(event.getDateTime()));
            pstmt.setString(3, event.getLocation());
            pstmt.setString(4, event.getDescription());
            pstmt.setDouble(5, event.getTicketPrice());
            pstmt.setInt(6, event.getTotalSeats());
            pstmt.setInt(7, event.getAvailableSeats());
            pstmt.setInt(8, event.getOrganizerId());
            pstmt.setString(9, event.getStatus() != null ? event.getStatus() : "ACTIVE");

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error creating event: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all events
     */
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY event_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all events: " + e.getMessage());
        }

        return events;
    }

    /**
     * Get events by organizer ID
     */
    public List<Event> getEventsByOrganizer(int organizerId) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE organizer_id = ? ORDER BY event_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, organizerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting events by organizer: " + e.getMessage());
        }

        return events;
    }

    /**
     * Get event by ID
     */
    public Event findById(int id) {
        String sql = "SELECT * FROM events WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToEvent(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding event by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Update event
     */
    public boolean updateEvent(Event event) {
        String sql = "UPDATE events SET title = ?, event_date = ?, location = ?, description = ?, ticket_price = ?, total_seats = ?, available_seats = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, event.getName());
            pstmt.setTimestamp(2, Timestamp.valueOf(event.getDateTime()));
            pstmt.setString(3, event.getLocation());
            pstmt.setString(4, event.getDescription());
            pstmt.setDouble(5, event.getTicketPrice());
            pstmt.setInt(6, event.getTotalSeats());
            pstmt.setInt(7, event.getAvailableSeats());
            pstmt.setString(8, event.getStatus() != null ? event.getStatus() : "ACTIVE");
            pstmt.setInt(9, event.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating event: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete event
     */
    public boolean deleteEvent(int id) {
        String sql = "DELETE FROM events WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting event: " + e.getMessage());
            return false;
        }
    }

    /**
     * Decrease available seats when a ticket is booked
     */
    public boolean decreaseAvailableSeats(int eventId) {
        String sql = "UPDATE events SET available_seats = available_seats - 1 WHERE id = ? AND available_seats > 0";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, eventId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error decreasing available seats: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update event status
     */
    public boolean updateEventStatus(int eventId, String status) {
        String sql = "UPDATE events SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, eventId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating event status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get events by status
     */
    public List<Event> getEventsByStatus(String status) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE status = ? ORDER BY event_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting events by status: " + e.getMessage());
        }

        return events;
    }

    /**
     * Get events by location (search)
     */
    public List<Event> searchEventsByLocation(String location) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE LOWER(location) LIKE ? AND status = 'ACTIVE' ORDER BY event_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + location.toLowerCase() + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error searching events by location: " + e.getMessage());
        }

        return events;
    }

    /**
     * Get events by date range
     */
    public List<Event> getEventsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE event_date >= ? AND event_date <= ? AND status = 'ACTIVE' ORDER BY event_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting events by date range: " + e.getMessage());
        }

        return events;
    }

    /**
     * Map ResultSet to Event object
     */
    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setName(rs.getString("title"));

        Timestamp timestamp = rs.getTimestamp("event_date");
        event.setDateTime(timestamp != null ? timestamp.toLocalDateTime() : null);

        event.setLocation(rs.getString("location"));
        event.setDescription(rs.getString("description"));
        event.setTicketPrice(rs.getDouble("ticket_price"));
        event.setTotalSeats(rs.getInt("total_seats"));
        event.setAvailableSeats(rs.getInt("available_seats"));
        event.setOrganizerId(rs.getInt("organizer_id"));

        // Handle status (may not exist in old databases)
        try {
            event.setStatus(rs.getString("status"));
        } catch (SQLException e) {
            event.setStatus("ACTIVE"); // Default for old records
        }

        return event;
    }
}
