package com.example.egovbus.dto;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String phoneNumber;
    private String otp;
    private String newPassword;
}