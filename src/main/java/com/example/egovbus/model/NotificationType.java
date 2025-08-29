package com.example.egovbus.model;

/**
 * Notification Type Enumeration
 */
public enum NotificationType {
    BUS_ARRIVAL("Bus Arrival"),
    BUS_DEPARTURE("Bus Departure"),
    DELAY_ALERT("Delay Alert"),
    RESERVATION_CONFIRMATION("Reservation Confirmation"),
    PAYMENT_SUCCESS("Payment Success"),
    PAYMENT_FAILURE("Payment Failure"),
    ROUTE_CHANGE("Route Change"),
    EMERGENCY("Emergency Alert"),
    PROMOTION("Promotional Message");
    
    private final String description;
    
    NotificationType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}