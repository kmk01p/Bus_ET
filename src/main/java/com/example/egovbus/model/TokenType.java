package com.example.egovbus.model;

/**
 * Token Type Enumeration
 */
public enum TokenType {
    EMAIL_VERIFICATION("Email Verification"),
    SMS_VERIFICATION("SMS Verification"),
    PASSWORD_RESET("Password Reset"),
    REFRESH_TOKEN("Refresh Token");
    
    private final String description;
    
    TokenType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}