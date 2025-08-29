package com.example.egovbus.repository;

import com.example.egovbus.model.BusLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * BusLocation Repository Interface
 */
@Repository
public interface BusLocationRepository extends JpaRepository<BusLocation, Long> {
    
    List<BusLocation> findByBusIdOrderByTimestampDesc(Long busId);
    
    @Query("SELECT bl FROM BusLocation bl WHERE bl.bus.id = :busId " +
           "AND bl.timestamp BETWEEN :startTime AND :endTime ORDER BY bl.timestamp DESC")
    List<BusLocation> findByBusAndTimeRange(@Param("busId") Long busId,
                                           @Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT bl FROM BusLocation bl WHERE bl.bus.id = :busId ORDER BY bl.timestamp DESC")
    List<BusLocation> findLatestLocationByBus(@Param("busId") Long busId);
    
    void deleteByTimestampBefore(LocalDateTime timestamp);
}