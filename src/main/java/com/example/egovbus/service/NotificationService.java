package com.example.egovbus.service;

import com.example.egovbus.model.*;
import com.example.egovbus.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Notification Service - Handles SMS and push notifications
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    // Ethiopian SMS gateway configuration
    private static final String ETHIO_TELECOM_SMS_API = "https://api.ethiotelecom.et/sms/v1/send";
    
    /**
     * Check and send bus arrival notifications
     */
    public void checkAndSendArrivalNotifications(Bus bus) {
        // Find passengers with reservations on this bus
        List<Notification> pendingNotifications = notificationRepository
            .findPendingArrivalNotifications(bus.getId());
        
        for (Notification notification : pendingNotifications) {
            // Calculate distance to stop
            double distance = calculateDistanceToStop(bus, notification.getStopName());
            
            // Send notification if bus is within 2km or 5 minutes away
            if (distance < 2.0 || notification.getEstimatedMinutes() <= 5) {
                sendArrivalNotification(notification);
            }
        }
    }
    
    /**
     * Send arrival notification
     */
    public void sendArrivalNotification(Notification notification) {
        User user = notification.getUser();
        
        String message = formatArrivalMessage(notification);
        
        if (user.getSmsNotifications()) {
            sendSMS(user.getPhoneNumber(), message);
        }
        
        if (user.getPushNotifications()) {
            sendPushNotification(user.getId(), notification);
        }
        
        notification.setIsSent(true);
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);
        
        log.info("Arrival notification sent to user: {}", user.getPhoneNumber());
    }
    
    /**
     * Send reservation confirmation
     */
    public void sendReservationConfirmation(Reservation reservation) {
        User user = reservation.getPassenger();
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setBus(reservation.getBus());
        notification.setType(NotificationType.RESERVATION_CONFIRMATION);
        notification.setTitle("Reservation Confirmed");
        notification.setMessage(String.format(
            "Your seat on bus %s from %s to %s is confirmed. Confirmation: %s",
            reservation.getBus().getBusNumber(),
            reservation.getBoardingStop(),
            reservation.getAlightingStop(),
            reservation.getConfirmationCode()
        ));
        notification.setMessageAmharic(formatAmharicMessage(notification.getMessage()));
        
        notificationRepository.save(notification);
        
        if (user.getSmsNotifications()) {
            sendSMS(user.getPhoneNumber(), notification.getMessage());
        }
        
        sendPushNotification(user.getId(), notification);
    }
    
    /**
     * Send payment confirmation SMS
     */
    public void sendPaymentSMS(Payment payment) {
        User user = payment.getUser();
        
        String message = String.format(
            "Payment confirmed! Amount: %.2f ETB. Receipt: %s. Thank you for using Ethiopia Bus Service.",
            payment.getAmount(),
            payment.getReceiptNumber()
        );
        
        if (user.getPreferredLanguage().equals("am")) {
            message = formatAmharicPaymentMessage(payment);
        }
        
        sendSMS(user.getPhoneNumber(), message);
        log.info("Payment SMS sent to: {}", user.getPhoneNumber());
    }
    
    /**
     * Send SMS via Ethiopian telecom
     */
    private void sendSMS(String phoneNumber, String message) {
        try {
            // Format phone number for Ethiopian format
            String formattedPhone = formatEthiopianPhone(phoneNumber);
            
            // In production, integrate with actual SMS gateway
            log.info("SMS to {}: {}", formattedPhone, message);
            
            // Simulate SMS API call
            simulateSMSSend(formattedPhone, message);
            
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage());
        }
    }
    
    /**
     * Send push notification via WebSocket
     */
    private void sendPushNotification(Long userId, Notification notification) {
        messagingTemplate.convertAndSend(
            "/queue/user-" + userId + "/notifications", 
            notification
        );
    }
    
    /**
     * Send emergency alert
     */
    public void sendEmergencyAlert(Map<String, Object> emergency) {
        String message = (String) emergency.get("message");
        Long busId = Long.parseLong(emergency.get("busId").toString());
        
        // Find all affected passengers
        List<User> affectedUsers = notificationRepository.findUsersWithReservationsOnBus(busId);
        
        for (User user : affectedUsers) {
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setType(NotificationType.EMERGENCY);
            notification.setTitle("Emergency Alert");
            notification.setMessage(message);
            notification.setMessageAmharic(formatAmharicMessage(message));
            
            notificationRepository.save(notification);
            
            if (user.getSmsNotifications()) {
                sendSMS(user.getPhoneNumber(), "EMERGENCY: " + message);
            }
            
            sendPushNotification(user.getId(), notification);
        }
        
        log.warn("Emergency alert sent to {} users", affectedUsers.size());
    }
    
    /**
     * Format Ethiopian phone number
     */
    private String formatEthiopianPhone(String phone) {
        // Ensure phone starts with +251
        if (!phone.startsWith("+251")) {
            if (phone.startsWith("0")) {
                phone = "+251" + phone.substring(1);
            } else if (phone.startsWith("251")) {
                phone = "+" + phone;
            } else {
                phone = "+251" + phone;
            }
        }
        return phone;
    }
    
    /**
     * Calculate distance to stop (simplified)
     */
    private double calculateDistanceToStop(Bus bus, String stopName) {
        // In production, implement actual distance calculation
        return 1.5; // Mock distance in km
    }
    
    /**
     * Format arrival message
     */
    private String formatArrivalMessage(Notification notification) {
        return String.format(
            "Bus %s arriving at %s in %d minutes",
            notification.getBus().getBusNumber(),
            notification.getStopName(),
            notification.getEstimatedMinutes()
        );
    }
    
    /**
     * Format Amharic message
     */
    private String formatAmharicMessage(String englishMessage) {
        // In production, implement actual translation
        return "አማርኛ መልዕክት: " + englishMessage;
    }
    
    /**
     * Format Amharic payment message
     */
    private String formatAmharicPaymentMessage(Payment payment) {
        return String.format(
            "ክፍያ ተረጋግጧል! መጠን: %.2f ብር። ደረሰኝ: %s። የኢትዮጵያ አውቶብስ አገልግሎትን ስለተጠቀሙ እናመሰግናለን።",
            payment.getAmount(),
            payment.getReceiptNumber()
        );
    }
    
    /**
     * Simulate SMS send (for development)
     */
    private void simulateSMSSend(String phone, String message) {
        log.info("SMS Sent - To: {}, Message: {}", phone, message);
    }
    
    public void sendPaymentConfirmation(Reservation reservation) {
        sendReservationConfirmation(reservation);
    }
    
    public void sendCancellationNotification(Reservation reservation) {
        User user = reservation.getPassenger();
        String message = "Your reservation " + reservation.getConfirmationCode() + " has been cancelled.";
        sendSMS(user.getPhoneNumber(), message);
    }
    
    public void sendRefundNotification(Payment payment) {
        User user = payment.getUser();
        String message = String.format("Refund processed: %.2f ETB. It will be credited within 3-5 business days.",
            payment.getRefundAmount());
        sendSMS(user.getPhoneNumber(), message);
    }
}