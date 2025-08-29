package com.example.egovbus.dto;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class SignupRequest {
    @NotBlank
    private String fullName;
    
    @NotBlank
    @Pattern(regexp = "\\+?251[0-9]{9}", message = "Invalid Ethiopian phone number")
    private String phoneNumber;
    
    @Email
    private String email;
    
    private String username;
    
    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @NotBlank
    private String role; // PASSENGER or DRIVER
    
    private String preferredLanguage = "am";
    
    // Driver specific fields
    private String driverLicenseNumber;
    private String licenseExpiryDate;
}