package app.dao;

import app.models.Ticket;
import app.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Ticket operations
 */
public class TicketDAO {
    /**
     * Create a new ticket
     */
    public boolean createTicket(Ticket ticket) {
        String sql = "INSERT INTO tickets (ticket_code, event_id, user_id, booking_date, price) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ticket.getTicketCode());
            pstmt.setInt(2, ticket.getEventId());
            pstmt.setInt(3, ticket.getUserId());
            pstmt.setTimestamp(4, Timestamp.valueOf(ticket.getBookingDate()));
            pstmt.setDouble(5, ticket.getPrice());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating ticket: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all tickets for a user
     */
    public List<Ticket> getTicketsByUser(int userId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE user_id = ? ORDER BY booking_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tickets.add(mapResultSetToTicket(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting tickets by user: " + e.getMessage());
        }
        
        return tickets;
    }

    /**
     * Get all tickets for an event
     */
    public List<Ticket> getTicketsByEvent(int eventId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE event_id = ? ORDER BY booking_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tickets.add(mapResultSetToTicket(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting tickets by event: " + e.getMessage());
        }
        
        return tickets;
    }

    /**
     * Get ticket by ticket code
     */
    public Ticket findByTicketCode(String ticketCode) {
        String sql = "SELECT * FROM tickets WHERE ticket_code = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ticketCode);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToTicket(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding ticket by code: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Check if user has already booked a ticket for an event
     */
    public boolean hasUserBookedEvent(int userId, int eventId) {
        String sql = "SELECT COUNT(*) FROM tickets WHERE user_id = ? AND event_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, eventId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking if user booked event: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Get ticket by ID
     */
    public Ticket findById(int id) {
        String sql = "SELECT * FROM tickets WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToTicket(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding ticket by ID: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Map ResultSet to Ticket object
     */
    private Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("id"));
        ticket.setTicketCode(rs.getString("ticket_code"));
        ticket.setEventId(rs.getInt("event_id"));
        ticket.setUserId(rs.getInt("user_id"));
        
        Timestamp timestamp = rs.getTimestamp("booking_date");
        ticket.setBookingDate(timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now());
        
        ticket.setPrice(rs.getDouble("price"));
        return ticket;
    }
}

