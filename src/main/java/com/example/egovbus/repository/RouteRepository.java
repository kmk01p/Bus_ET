package com.example.egovbus.repository;

import com.example.egovbus.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Route Repository Interface
 */
@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    
    Optional<Route> findByRouteNumber(String routeNumber);
    
    List<Route> findByIsActive(Boolean isActive);
    
    @Query("SELECT r FROM Route r WHERE :stopName MEMBER OF r.stops")
    List<Route> findByStop(@Param("stopName") String stopName);
    
    @Query("SELECT r FROM Route r WHERE r.startPoint = :point OR r.endPoint = :point")
    List<Route> findByStartOrEndPoint(@Param("point") String point);
    
    List<Route> findByFareLessThanEqual(Double maxFare);
}