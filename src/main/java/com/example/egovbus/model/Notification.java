package com.example.egovbus.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Notification Entity - Bus arrival notifications
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;
    
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    
    private String title;
    
    @Column(length = 500)
    private String message;
    
    private String messageAmharic; // Message in Amharic
    
    private Boolean isRead = false;
    
    private Boolean isSent = false;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime sentAt;
    
    private LocalDateTime readAt;
    
    // For arrival notifications
    private String stopName;
    private Integer estimatedMinutes;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}