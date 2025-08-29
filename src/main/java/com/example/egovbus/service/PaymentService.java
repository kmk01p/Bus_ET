package com.example.egovbus.service;

import com.example.egovbus.model.*;
import com.example.egovbus.repository.PaymentRepository;
import com.example.egovbus.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Payment Service - Integrates with Ethiopian payment systems
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;
    
    // Ethiopian payment gateway URLs (to be configured)
    private static final String TELEBIRR_API_URL = "https://api.telebirr.et/v1/payment";
    private static final String CBE_API_URL = "https://api.cbebirr.com/v1/payment";
    
    /**
     * Process payment via Telebirr
     */
    public Payment processTelebirrPayment(PaymentRequest request) {
        log.info("Processing Telebirr payment for amount: {} ETB", request.getAmount());
        
        Payment payment = new Payment();
        payment.setUser(request.getUser());
        payment.setReservation(request.getReservation());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(PaymentMethod.TELEBIRR);
        payment.setStatus(PaymentStatus.PROCESSING);
        
        try {
            // Simulate Telebirr API call
            Map<String, Object> telebirrRequest = new HashMap<>();
            telebirrRequest.put("phone", request.getUser().getPhoneNumber());
            telebirrRequest.put("amount", request.getAmount());
            telebirrRequest.put("reference", payment.getReceiptNumber());
            
            // In production, make actual API call
            String transactionId = simulateTelebirrPayment(telebirrRequest);
            
            payment.setTelebirrTransactionId(transactionId);
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setConfirmationTime(LocalDateTime.now());
            payment.setTransactionId(UUID.randomUUID().toString());
            
            // Send SMS confirmation
            notificationService.sendPaymentSMS(payment);
            
            log.info("Telebirr payment successful: {}", transactionId);
            
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            log.error("Telebirr payment failed: {}", e.getMessage());
            throw new RuntimeException("Payment failed: " + e.getMessage());
        }
        
        return paymentRepository.save(payment);
    }
    
    /**
     * Process payment via CBE Mobile Banking
     */
    public Payment processCBEPayment(PaymentRequest request) {
        log.info("Processing CBE payment for amount: {} ETB", request.getAmount());
        
        Payment payment = new Payment();
        payment.setUser(request.getUser());
        payment.setReservation(request.getReservation());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(PaymentMethod.CBE_MOBILE);
        payment.setStatus(PaymentStatus.PROCESSING);
        
        try {
            // Simulate CBE API call
            Map<String, Object> cbeRequest = new HashMap<>();
            cbeRequest.put("account", request.getCbeAccountNumber());
            cbeRequest.put("amount", request.getAmount());
            cbeRequest.put("reference", payment.getReceiptNumber());
            
            // In production, make actual API call
            String transactionId = simulateCBEPayment(cbeRequest);
            
            payment.setCbeAccountNumber(request.getCbeAccountNumber());
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setConfirmationTime(LocalDateTime.now());
            payment.setTransactionId(transactionId);
            
            // Send SMS confirmation
            notificationService.sendPaymentSMS(payment);
            
            log.info("CBE payment successful: {}", transactionId);
            
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            log.error("CBE payment failed: {}", e.getMessage());
            throw new RuntimeException("Payment failed: " + e.getMessage());
        }
        
        return paymentRepository.save(payment);
    }
    
    /**
     * Process refund
     */
    public Payment processRefund(Payment originalPayment, String reason) {
        if (originalPayment.getIsRefunded()) {
            throw new RuntimeException("Payment already refunded");
        }
        
        originalPayment.setIsRefunded(true);
        originalPayment.setRefundAmount(originalPayment.getAmount());
        originalPayment.setRefundTime(LocalDateTime.now());
        originalPayment.setRefundReason(reason);
        originalPayment.setStatus(PaymentStatus.REFUNDED);
        
        // Process refund based on payment method
        switch (originalPayment.getPaymentMethod()) {
            case TELEBIRR:
                processTelebirrRefund(originalPayment);
                break;
            case CBE_MOBILE:
            case CBE_BIRR:
                processCBERefund(originalPayment);
                break;
            default:
                log.info("Manual refund required for payment method: {}", 
                    originalPayment.getPaymentMethod());
        }
        
        // Send refund notification
        notificationService.sendRefundNotification(originalPayment);
        
        log.info("Refund processed: {} ETB to user {}", 
            originalPayment.getRefundAmount(), originalPayment.getUser().getPhoneNumber());
        
        return paymentRepository.save(originalPayment);
    }
    
    /**
     * Simulate Telebirr payment (for development)
     */
    private String simulateTelebirrPayment(Map<String, Object> request) {
        // In production, implement actual Telebirr API integration
        return "TEL" + System.currentTimeMillis();
    }
    
    /**
     * Simulate CBE payment (for development)
     */
    private String simulateCBEPayment(Map<String, Object> request) {
        // In production, implement actual CBE API integration
        return "CBE" + System.currentTimeMillis();
    }
    
    /**
     * Process Telebirr refund
     */
    private void processTelebirrRefund(Payment payment) {
        // Implement Telebirr refund API call
        log.info("Processing Telebirr refund for transaction: {}", 
            payment.getTelebirrTransactionId());
    }
    
    /**
     * Process CBE refund
     */
    private void processCBERefund(Payment payment) {
        // Implement CBE refund API call
        log.info("Processing CBE refund for account: {}", 
            payment.getCbeAccountNumber());
    }
}