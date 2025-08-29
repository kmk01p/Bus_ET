package com.example.egovbus.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Reservation Entity - Passenger seat reservations
 */
@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger;
    
    @ManyToOne
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;
    
    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;
    
    @Column(nullable = false)
    private String boardingStop; // Starting stop
    
    @Column(nullable = false)
    private String alightingStop; // Destination stop
    
    private Integer seatNumber;
    
    private LocalDateTime reservationTime;
    
    private LocalDateTime scheduledDepartureTime;
    
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private Payment payment;
    
    private String confirmationCode;
    
    private Boolean notificationSent = false;
    
    // QR code for validation
    private String qrCode;
    
    @PrePersist
    protected void onCreate() {
        reservationTime = LocalDateTime.now();
        confirmationCode = generateConfirmationCode();
    }
    
    private String generateConfirmationCode() {
        return "ETH" + System.currentTimeMillis() % 1000000;
    }
}