package com.example.egovbus.controller;

import com.example.egovbus.model.Bus;
import com.example.egovbus.model.BusStatus;
import com.example.egovbus.service.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Bus REST Controller
 * 버스 관련 API 엔드포인트
 */
@RestController
@RequestMapping("/api/buses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BusController {
    
    private final BusService busService;
    
    /**
     * 모든 버스 조회
     */
    @GetMapping
    public ResponseEntity<List<Bus>> getAllBuses() {
        return ResponseEntity.ok(busService.getAllBuses());
    }
    
    /**
     * 특정 버스 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<Bus> getBusById(@PathVariable Long id) {
        return busService.getBusById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 버스 번호로 조회
     */
    @GetMapping("/number/{busNumber}")
    public ResponseEntity<Bus> getBusByNumber(@PathVariable String busNumber) {
        return busService.getBusByNumber(busNumber)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 상태별 버스 조회
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Bus>> getBusesByStatus(@PathVariable String status) {
        try {
            BusStatus busStatus = BusStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(busService.getBusesByStatus(busStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 운행중인 버스 조회
     */
    @GetMapping("/active")
    public ResponseEntity<List<Bus>> getActiveBuses() {
        return ResponseEntity.ok(busService.getActiveBuses());
    }
    
    /**
     * 노선별 버스 조회
     */
    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<Bus>> getBusesByRoute(@PathVariable Long routeId) {
        return ResponseEntity.ok(busService.getBusesByRoute(routeId));
    }
    
    /**
     * 여석이 있는 버스 조회
     */
    @GetMapping("/available")
    public ResponseEntity<List<Bus>> getAvailableBuses() {
        return ResponseEntity.ok(busService.getAvailableBuses());
    }
    
    /**
     * 특정 지역 내 버스 조회
     */
    @GetMapping("/area")
    public ResponseEntity<List<Bus>> getBusesInArea(
            @RequestParam Double minLat,
            @RequestParam Double maxLat,
            @RequestParam Double minLon,
            @RequestParam Double maxLon) {
        return ResponseEntity.ok(busService.getBusesInArea(minLat, maxLat, minLon, maxLon));
    }
    
    /**
     * 버스 생성
     */
    @PostMapping
    public ResponseEntity<Bus> createBus(@RequestBody Bus bus) {
        Bus createdBus = busService.createBus(bus);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBus);
    }
    
    /**
     * 버스 정보 업데이트
     */
    @PutMapping("/{id}")
    public ResponseEntity<Bus> updateBus(@PathVariable Long id, @RequestBody Bus bus) {
        try {
            Bus updatedBus = busService.updateBus(id, bus);
            return ResponseEntity.ok(updatedBus);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 버스 위치 업데이트
     */
    @PatchMapping("/{id}/location")
    public ResponseEntity<Bus> updateBusLocation(
            @PathVariable Long id,
            @RequestBody Map<String, Double> location) {
        try {
            Double latitude = location.get("latitude");
            Double longitude = location.get("longitude");
            Double speed = location.getOrDefault("speed", 0.0);
            
            Bus updatedBus = busService.updateBusLocation(id, latitude, longitude, speed);
            return ResponseEntity.ok(updatedBus);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 버스 상태 변경
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Bus> updateBusStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            BusStatus busStatus = BusStatus.valueOf(status.toUpperCase());
            Bus updatedBus = busService.updateBusStatus(id, busStatus);
            return ResponseEntity.ok(updatedBus);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 탑승객 수 업데이트
     */
    @PatchMapping("/{id}/passengers")
    public ResponseEntity<Bus> updatePassengers(
            @PathVariable Long id,
            @RequestParam Integer count) {
        try {
            Bus updatedBus = busService.updatePassengerCount(id, count);
            return ResponseEntity.ok(updatedBus);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 버스 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 버스 통계 조회
     */
    @GetMapping("/statistics")
    public ResponseEntity<BusService.BusStatistics> getBusStatistics() {
        return ResponseEntity.ok(busService.getBusStatistics());
    }
}