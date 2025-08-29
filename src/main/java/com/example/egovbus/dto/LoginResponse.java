package com.example.egovbus.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private String fullName;
    private String role;
    private String phoneNumber;
    private Long busId;
    private String busNumber;
}