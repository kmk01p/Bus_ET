package com.example.egovbus.controller;

import com.example.egovbus.model.Route;
import com.example.egovbus.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Route REST Controller
 * 노선 관련 API 엔드포인트
 */
@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RouteController {
    
    private final RouteService routeService;
    
    /**
     * 모든 노선 조회
     */
    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }
    
    /**
     * 활성 노선만 조회
     */
    @GetMapping("/active")
    public ResponseEntity<List<Route>> getActiveRoutes() {
        return ResponseEntity.ok(routeService.getActiveRoutes());
    }
    
    /**
     * 특정 노선 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable Long id) {
        return routeService.getRouteById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 노선 번호로 조회
     */
    @GetMapping("/number/{routeNumber}")
    public ResponseEntity<Route> getRouteByNumber(@PathVariable String routeNumber) {
        return routeService.getRouteByNumber(routeNumber)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 정류장으로 노선 검색
     */
    @GetMapping("/stop/{stopName}")
    public ResponseEntity<List<Route>> getRoutesByStop(@PathVariable String stopName) {
        return ResponseEntity.ok(routeService.getRoutesByStop(stopName));
    }
    
    /**
     * 출발지/도착지로 노선 검색
     */
    @GetMapping("/endpoint/{point}")
    public ResponseEntity<List<Route>> getRoutesByEndpoint(@PathVariable String point) {
        return ResponseEntity.ok(routeService.getRoutesByEndpoints(point));
    }
    
    /**
     * 요금 범위로 노선 검색
     */
    @GetMapping("/fare")
    public ResponseEntity<List<Route>> getRoutesByMaxFare(@RequestParam Double maxFare) {
        return ResponseEntity.ok(routeService.getRoutesByMaxFare(maxFare));
    }
    
    /**
     * 노선 생성
     */
    @PostMapping
    public ResponseEntity<Route> createRoute(@RequestBody Route route) {
        Route createdRoute = routeService.createRoute(route);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoute);
    }
    
    /**
     * 노선 업데이트
     */
    @PutMapping("/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable Long id, @RequestBody Route route) {
        try {
            Route updatedRoute = routeService.updateRoute(id, route);
            return ResponseEntity.ok(updatedRoute);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 노선 활성화/비활성화 토글
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Route> toggleRouteStatus(@PathVariable Long id) {
        try {
            Route updatedRoute = routeService.toggleRouteStatus(id);
            return ResponseEntity.ok(updatedRoute);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 정류장 추가
     */
    @PostMapping("/{id}/stops")
    public ResponseEntity<Route> addStop(
            @PathVariable Long id,
            @RequestBody Map<String, Object> stopData) {
        try {
            String stopName = (String) stopData.get("stopName");
            Integer position = (Integer) stopData.getOrDefault("position", -1);
            
            Route updatedRoute = routeService.addStop(id, stopName, position);
            return ResponseEntity.ok(updatedRoute);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 정류장 제거
     */
    @DeleteMapping("/{id}/stops/{stopName}")
    public ResponseEntity<Route> removeStop(
            @PathVariable Long id,
            @PathVariable String stopName) {
        try {
            Route updatedRoute = routeService.removeStop(id, stopName);
            return ResponseEntity.ok(updatedRoute);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 노선 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }
}