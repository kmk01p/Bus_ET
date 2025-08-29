package com.example.egovbus.model;

/**
 * Payment Method Enumeration - Ethiopian payment systems
 */
public enum PaymentMethod {
    TELEBIRR("Telebirr Mobile Money"),
    CBE_MOBILE("CBE Mobile Banking"),
    CBE_BIRR("CBE Birr"),
    AMOLE("Amole by Dashen Bank"),
    MPESA("M-Pesa"),
    HELLOCASH("HelloCash"),
    CASH("Cash Payment"),
    BANK_TRANSFER("Bank Transfer"),
    CARD("Debit/Credit Card");
    
    private final String description;
    
    PaymentMethod(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}