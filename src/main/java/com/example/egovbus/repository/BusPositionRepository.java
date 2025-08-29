package com.example.egovbus.repository;

import com.example.egovbus.model.Bus;
import com.example.egovbus.model.BusPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface BusPositionRepository extends JpaRepository<BusPosition, Long> {
    @Query("select p from BusPosition p where p.bus = :bus and p.timestamp >= :since order by p.timestamp desc")
    List<BusPosition> findRecentPositions(Bus bus, Instant since);

    @Query("select p from BusPosition p where p.timestamp >= :since order by p.timestamp desc")
    List<BusPosition> findAllRecent(Instant since);
}
