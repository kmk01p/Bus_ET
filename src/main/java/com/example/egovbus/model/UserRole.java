package com.example.egovbus.model;

/**
 * User Role Enumeration
 */
public enum UserRole {
    DRIVER("Driver"),
    PASSENGER("Passenger"),
    ADMIN("Administrator"),
    SUPERVISOR("Supervisor");
    
    private final String description;
    
    UserRole(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}