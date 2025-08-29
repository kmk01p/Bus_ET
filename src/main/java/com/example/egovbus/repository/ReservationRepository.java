package com.example.egovbus.repository;

import com.example.egovbus.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByPassengerOrderByScheduledDepartureTimeDesc(User passenger);
    Optional<Reservation> findByConfirmationCode(String confirmationCode);
    List<Reservation> findByBusAndScheduledDepartureTime(Bus bus, LocalDateTime departureTime);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.bus = :bus AND r.scheduledDepartureTime = :time AND r.status != :status")
    int countByBusAndScheduledDepartureTimeAndStatusNot(@Param("bus") Bus bus, 
                                                        @Param("time") LocalDateTime time, 
                                                        @Param("status") ReservationStatus status);
}