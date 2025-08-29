package com.example.egovbus.model;

/**
 * Bus Status Enumeration
 */
public enum BusStatus {
    ACTIVE("운행중"),
    INACTIVE("운행종료"),
    MAINTENANCE("정비중"),
    EMERGENCY("긴급상황"),
    DELAYED("지연"),
    WAITING("대기중");
    
    private final String description;
    
    BusStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}