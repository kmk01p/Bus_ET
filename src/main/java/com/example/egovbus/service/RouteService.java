package com.example.egovbus.service;

import com.example.egovbus.model.Route;
import com.example.egovbus.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Route Service Implementation
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RouteService {
    
    private final RouteRepository routeRepository;
    
    /**
     * 모든 노선 조회
     */
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }
    
    /**
     * 활성 노선만 조회
     */
    public List<Route> getActiveRoutes() {
        return routeRepository.findByIsActive(true);
    }
    
    /**
     * 노선 ID로 조회
     */
    public Optional<Route> getRouteById(Long id) {
        return routeRepository.findById(id);
    }
    
    /**
     * 노선 번호로 조회
     */
    public Optional<Route> getRouteByNumber(String routeNumber) {
        return routeRepository.findByRouteNumber(routeNumber);
    }
    
    /**
     * 정류장으로 노선 검색
     */
    public List<Route> getRoutesByStop(String stopName) {
        return routeRepository.findByStop(stopName);
    }
    
    /**
     * 출발지/도착지로 노선 검색
     */
    public List<Route> getRoutesByEndpoints(String point) {
        return routeRepository.findByStartOrEndPoint(point);
    }
    
    /**
     * 요금 범위로 노선 검색
     */
    public List<Route> getRoutesByMaxFare(Double maxFare) {
        return routeRepository.findByFareLessThanEqual(maxFare);
    }
    
    /**
     * 노선 생성
     */
    public Route createRoute(Route route) {
        route.setIsActive(true);
        return routeRepository.save(route);
    }
    
    /**
     * 노선 업데이트
     */
    public Route updateRoute(Long id, Route routeDetails) {
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Route not found with id: " + id));
        
        route.setRouteNumber(routeDetails.getRouteNumber());
        route.setRouteName(routeDetails.getRouteName());
        route.setStartPoint(routeDetails.getStartPoint());
        route.setEndPoint(routeDetails.getEndPoint());
        route.setStops(routeDetails.getStops());
        route.setTotalDistance(routeDetails.getTotalDistance());
        route.setEstimatedTime(routeDetails.getEstimatedTime());
        route.setFare(routeDetails.getFare());
        route.setOperatingHours(routeDetails.getOperatingHours());
        
        return routeRepository.save(route);
    }
    
    /**
     * 노선 활성화/비활성화
     */
    public Route toggleRouteStatus(Long id) {
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Route not found with id: " + id));
        
        route.setIsActive(!route.getIsActive());
        return routeRepository.save(route);
    }
    
    /**
     * 노선 삭제
     */
    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
    }
    
    /**
     * 정류장 추가
     */
    public Route addStop(Long routeId, String stopName, int position) {
        Route route = routeRepository.findById(routeId)
            .orElseThrow(() -> new RuntimeException("Route not found with id: " + routeId));
        
        List<String> stops = route.getStops();
        if (position >= 0 && position <= stops.size()) {
            stops.add(position, stopName);
        } else {
            stops.add(stopName);
        }
        
        route.setStops(stops);
        return routeRepository.save(route);
    }
    
    /**
     * 정류장 제거
     */
    public Route removeStop(Long routeId, String stopName) {
        Route route = routeRepository.findById(routeId)
            .orElseThrow(() -> new RuntimeException("Route not found with id: " + routeId));
        
        route.getStops().remove(stopName);
        return routeRepository.save(route);
    }
}