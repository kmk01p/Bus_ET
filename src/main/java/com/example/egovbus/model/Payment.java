package com.example.egovbus.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Entity - Ethiopian payment systems integration
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // in Ethiopian Birr (ETB)
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    
    private String transactionId;
    
    private String telebirrTransactionId; // For Telebirr payments
    
    private String cbeAccountNumber; // For CBE Mobile Banking
    
    private String mpesaCode; // For M-Pesa if available
    
    private LocalDateTime paymentTime;
    
    private LocalDateTime confirmationTime;
    
    private String receiptNumber;
    
    // For refunds
    private Boolean isRefunded = false;
    private BigDecimal refundAmount;
    private LocalDateTime refundTime;
    private String refundReason;
    
    @PrePersist
    protected void onCreate() {
        paymentTime = LocalDateTime.now();
        receiptNumber = generateReceiptNumber();
    }
    
    private String generateReceiptNumber() {
        return "RCP" + System.currentTimeMillis() % 100000000;
    }
}