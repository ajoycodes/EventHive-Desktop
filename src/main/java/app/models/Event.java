package app.models;

import java.time.LocalDateTime;

/**
 * Event model representing an event in the system
 */
public class Event {
    private int id;
    private String name;
    private LocalDateTime dateTime;
    private String location;
    private String description;
    private double ticketPrice;
    private int totalSeats;
    private int availableSeats;
    private int organizerId; // User ID of the organizer who created this event
    private String status; // ACTIVE, HOLD, CANCELLED

    public Event() {
    }

    public Event(String name, LocalDateTime dateTime, String location, String description,
                 double ticketPrice, int totalSeats, int organizerId) {
        this.name = name;
        this.dateTime = dateTime;
        this.location = location;
        this.description = description;
        this.ticketPrice = ticketPrice;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.organizerId = organizerId;
        this.status = "ACTIVE"; // Default status
    }

    public Event(int id, String name, LocalDateTime dateTime, String location, String description,
                 double ticketPrice, int totalSeats, int availableSeats, int organizerId) {
        this.id = id;
        this.name = name;
        this.dateTime = dateTime;
        this.location = location;
        this.description = description;
        this.ticketPrice = ticketPrice;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.organizerId = organizerId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(int organizerId) {
        this.organizerId = organizerId;
    }

    public boolean hasAvailableSeats() {
        return availableSeats > 0 && "ACTIVE".equals(status);
    }

    public String getStatus() {
        return status != null ? status : "ACTIVE";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public boolean isHold() {
        return "HOLD".equals(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }
}

