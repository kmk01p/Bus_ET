package com.example.egovbus.model;

/**
 * Reservation Status Enumeration
 */
public enum ReservationStatus {
    PENDING("Pending Payment"),
    CONFIRMED("Confirmed"),
    BOARDING("Boarding"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    NO_SHOW("No Show"),
    REFUNDED("Refunded");
    
    private final String description;
    
    ReservationStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}