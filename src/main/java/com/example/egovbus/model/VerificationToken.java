package com.example.egovbus.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Verification Token for email/SMS verification and password reset
 */
@Entity
@Table(name = "verification_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @Column(nullable = false)
    private String otp; // 6-digit OTP for SMS verification
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    private TokenType type;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime expiryDate;
    
    private Boolean used = false;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // Token expires in 24 hours, OTP in 10 minutes
        if (type == TokenType.PASSWORD_RESET || type == TokenType.SMS_VERIFICATION) {
            expiryDate = LocalDateTime.now().plusMinutes(10);
        } else {
            expiryDate = LocalDateTime.now().plusHours(24);
        }
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}