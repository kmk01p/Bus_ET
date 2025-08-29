package com.example.egovbus.repository;

import com.example.egovbus.model.Bus;
import com.example.egovbus.model.BusStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Bus Repository Interface
 */
@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    
    Optional<Bus> findByBusNumber(String busNumber);
    
    List<Bus> findByStatus(BusStatus status);
    
    List<Bus> findByRouteId(Long routeId);
    
    @Query("SELECT b FROM Bus b WHERE b.status = :status AND b.route.id = :routeId")
    List<Bus> findByStatusAndRoute(@Param("status") BusStatus status, @Param("routeId") Long routeId);
    
    @Query("SELECT b FROM Bus b WHERE b.currentLatitude BETWEEN :minLat AND :maxLat " +
           "AND b.currentLongitude BETWEEN :minLon AND :maxLon")
    List<Bus> findBusesInArea(@Param("minLat") Double minLat, @Param("maxLat") Double maxLat,
                               @Param("minLon") Double minLon, @Param("maxLon") Double maxLon);
    
    @Query("SELECT b FROM Bus b WHERE b.currentPassengers < b.capacity")
    List<Bus> findAvailableBuses();
    
    @Query("SELECT COUNT(b) FROM Bus b WHERE b.status = :status")
    Long countByStatus(@Param("status") BusStatus status);
}