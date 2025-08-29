package com.example.egovbus.controller;

import com.example.egovbus.model.Bus;
import com.example.egovbus.model.Notification;
import com.example.egovbus.service.BusService;
import com.example.egovbus.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.Map;

/**
 * WebSocket Controller for real-time updates
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final BusService busService;
    private final NotificationService notificationService;
    
    /**
     * Broadcast bus location updates every 5 seconds
     */
    @Scheduled(fixedDelay = 5000)
    public void broadcastBusLocations() {
        List<Bus> activeBuses = busService.getActiveBuses();
        messagingTemplate.convertAndSend("/topic/bus-locations", activeBuses);
    }
    
    /**
     * Handle driver location update
     */
    @MessageMapping("/update-location")
    @SendTo("/topic/location-update")
    public Map<String, Object> updateDriverLocation(Map<String, Object> location) {
        try {
            Long busId = Long.parseLong(location.get("busId").toString());
            Double latitude = Double.parseDouble(location.get("latitude").toString());
            Double longitude = Double.parseDouble(location.get("longitude").toString());
            Double speed = Double.parseDouble(location.getOrDefault("speed", "0").toString());
            
            Bus updatedBus = busService.updateBusLocation(busId, latitude, longitude, speed);
            
            // Check for arrival notifications
            notificationService.checkAndSendArrivalNotifications(updatedBus);
            
            location.put("status", "success");
            location.put("timestamp", System.currentTimeMillis());
            
            log.info("Location updated for bus {}: ({}, {})", busId, latitude, longitude);
            
        } catch (Exception e) {
            log.error("Error updating location: {}", e.getMessage());
            location.put("status", "error");
            location.put("message", e.getMessage());
        }
        
        return location;
    }
    
    /**
     * Send notification to specific user
     */
    public void sendNotificationToUser(Long userId, Notification notification) {
        messagingTemplate.convertAndSend("/queue/user-" + userId + "/notifications", notification);
    }
    
    /**
     * Broadcast emergency alert
     */
    @MessageMapping("/emergency")
    @SendTo("/topic/emergency-alerts")
    public Map<String, Object> broadcastEmergency(Map<String, Object> emergency) {
        log.warn("Emergency alert: {}", emergency);
        notificationService.sendEmergencyAlert(emergency);
        return emergency;
    }
    
    /**
     * Handle reservation updates
     */
    @MessageMapping("/reservation-update")
    @SendTo("/topic/reservations")
    public Map<String, Object> updateReservation(Map<String, Object> reservation) {
        log.info("Reservation update: {}", reservation);
        return reservation;
    }
}