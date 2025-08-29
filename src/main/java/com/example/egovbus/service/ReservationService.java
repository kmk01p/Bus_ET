package com.example.egovbus.service;

import com.example.egovbus.model.*;
import com.example.egovbus.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Reservation Service
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final BusRepository busRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * Create new reservation
     */
    public Reservation createReservation(Long userId, Long busId, String boardingStop, 
                                        String alightingStop, LocalDateTime departureTime) {
        
        User passenger = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> new RuntimeException("Bus not found"));
        
        // Check available seats
        int reservedSeats = reservationRepository.countByBusAndScheduledDepartureTimeAndStatusNot(
            bus, departureTime, ReservationStatus.CANCELLED);
        
        if (reservedSeats >= bus.getCapacity()) {
            throw new RuntimeException("No available seats");
        }
        
        Reservation reservation = new Reservation();
        reservation.setPassenger(passenger);
        reservation.setBus(bus);
        reservation.setRoute(bus.getRoute());
        reservation.setBoardingStop(boardingStop);
        reservation.setAlightingStop(alightingStop);
        reservation.setScheduledDepartureTime(departureTime);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setSeatNumber(reservedSeats + 1);
        reservation.setQrCode(generateQRCode());
        
        Reservation saved = reservationRepository.save(reservation);
        
        // Send notification
        notificationService.sendReservationConfirmation(saved);
        
        // Broadcast update via WebSocket
        messagingTemplate.convertAndSend("/topic/reservations", saved);
        
        log.info("Reservation created: {} for user {}", saved.getConfirmationCode(), userId);
        
        return saved;
    }
    
    /**
     * Confirm reservation after payment
     */
    public Reservation confirmReservation(Long reservationId, Payment payment) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservation.setPayment(payment);
            
            // Send confirmation notification
            notificationService.sendPaymentConfirmation(reservation);
            
            // Update via WebSocket
            messagingTemplate.convertAndSend("/topic/reservations", reservation);
            
            log.info("Reservation confirmed: {}", reservation.getConfirmationCode());
        } else {
            reservation.setStatus(ReservationStatus.CANCELLED);
            log.warn("Reservation cancelled due to payment failure: {}", reservation.getConfirmationCode());
        }
        
        return reservationRepository.save(reservation);
    }
    
    /**
     * Cancel reservation
     */
    public Reservation cancelReservation(Long reservationId, String reason) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            // Process refund
            paymentService.processRefund(reservation.getPayment(), reason);
        }
        
        reservation.setStatus(ReservationStatus.CANCELLED);
        
        // Send cancellation notification
        notificationService.sendCancellationNotification(reservation);
        
        log.info("Reservation cancelled: {}", reservation.getConfirmationCode());
        
        return reservationRepository.save(reservation);
    }
    
    /**
     * Get user reservations
     */
    public List<Reservation> getUserReservations(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return reservationRepository.findByPassengerOrderByScheduledDepartureTimeDesc(user);
    }
    
    /**
     * Check in passenger
     */
    public Reservation checkInPassenger(String confirmationCode) {
        Reservation reservation = reservationRepository.findByConfirmationCode(confirmationCode)
            .orElseThrow(() -> new RuntimeException("Invalid confirmation code"));
        
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new RuntimeException("Reservation not confirmed");
        }
        
        reservation.setStatus(ReservationStatus.BOARDING);
        
        // Update bus passenger count
        Bus bus = reservation.getBus();
        bus.setCurrentPassengers(bus.getCurrentPassengers() + 1);
        busRepository.save(bus);
        
        log.info("Passenger checked in: {}", confirmationCode);
        
        return reservationRepository.save(reservation);
    }
    
    /**
     * Generate QR code for reservation
     */
    private String generateQRCode() {
        return UUID.randomUUID().toString();
    }
}