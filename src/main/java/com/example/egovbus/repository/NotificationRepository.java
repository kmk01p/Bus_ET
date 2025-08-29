package com.example.egovbus.repository;

import com.example.egovbus.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndIsReadFalse(User user);
    
    @Query("SELECT n FROM Notification n WHERE n.bus.id = :busId AND n.type = 'BUS_ARRIVAL' AND n.isSent = false")
    List<Notification> findPendingArrivalNotifications(@Param("busId") Long busId);
    
    @Query("SELECT DISTINCT r.passenger FROM Reservation r WHERE r.bus.id = :busId AND r.status = 'CONFIRMED'")
    List<User> findUsersWithReservationsOnBus(@Param("busId") Long busId);
}