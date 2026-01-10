package app.models;

import java.time.LocalDateTime;

/**
 * Ticket model representing a booked ticket
 */
public class Ticket {
    private int id;
    private String ticketCode; // Unique ticket code (UUID or timestamp-based)
    private int eventId;
    private int userId;
    private LocalDateTime bookingDate;
    private double price;

    public Ticket() {
    }

    public Ticket(String ticketCode, int eventId, int userId, LocalDateTime bookingDate, double price) {
        this.ticketCode = ticketCode;
        this.eventId = eventId;
        this.userId = userId;
        this.bookingDate = bookingDate;
        this.price = price;
    }

    public Ticket(int id, String ticketCode, int eventId, int userId, LocalDateTime bookingDate, double price) {
        this.id = id;
        this.ticketCode = ticketCode;
        this.eventId = eventId;
        this.userId = userId;
        this.bookingDate = bookingDate;
        this.price = price;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

