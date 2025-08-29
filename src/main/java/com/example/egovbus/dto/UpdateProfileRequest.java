package com.example.egovbus.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String email;
    private String preferredLanguage;
    private Boolean smsNotifications;
    private Boolean pushNotifications;
}