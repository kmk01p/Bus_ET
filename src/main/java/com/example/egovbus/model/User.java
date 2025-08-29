package com.example.egovbus.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * User Entity - for drivers and passengers
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(unique = true, nullable = false)
    private String phoneNumber; // Ethiopian format: +251-xxx-xxx-xxx
    
    private String fullName;
    
    @Column(unique = true)
    private String email;
    
    @Enumerated(EnumType.STRING)
    private UserRole role; // DRIVER, PASSENGER, ADMIN
    
    private Boolean isActive = true;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime lastLogin;
    
    // For drivers only
    private String driverLicenseNumber;
    
    private LocalDateTime licenseExpiryDate;
    
    @OneToOne(mappedBy = "driver")
    private Bus assignedBus;
    
    // For passengers
    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL)
    private Set<Reservation> reservations;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Payment> payments;
    
    // Notification preferences
    private Boolean smsNotifications = true;
    private Boolean pushNotifications = true;
    private String preferredLanguage = "am"; // am = Amharic, en = English
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}