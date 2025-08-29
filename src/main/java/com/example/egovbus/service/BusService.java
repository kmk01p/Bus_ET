package com.example.egovbus.service;

import com.example.egovbus.model.Bus;
import com.example.egovbus.model.BusStatus;
import com.example.egovbus.model.BusLocation;
import com.example.egovbus.repository.BusRepository;
import com.example.egovbus.repository.BusLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Bus Service Implementation
 */
@Service
@Transactional
@RequiredArgsConstructor
public class BusService {
    
    private final BusRepository busRepository;
    private final BusLocationRepository busLocationRepository;
    
    /**
     * 모든 버스 조회
     */
    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }
    
    /**
     * 버스 ID로 조회
     */
    public Optional<Bus> getBusById(Long id) {
        return busRepository.findById(id);
    }
    
    /**
     * 버스 번호로 조회
     */
    public Optional<Bus> getBusByNumber(String busNumber) {
        return busRepository.findByBusNumber(busNumber);
    }
    
    /**
     * 상태별 버스 조회
     */
    public List<Bus> getBusesByStatus(BusStatus status) {
        return busRepository.findByStatus(status);
    }
    
    /**
     * 운행중인 버스 조회
     */
    public List<Bus> getActiveBuses() {
        return busRepository.findByStatus(BusStatus.ACTIVE);
    }
    
    /**
     * 노선별 버스 조회
     */
    public List<Bus> getBusesByRoute(Long routeId) {
        return busRepository.findByRouteId(routeId);
    }
    
    /**
     * 버스 생성
     */
    public Bus createBus(Bus bus) {
        bus.setStatus(BusStatus.INACTIVE);
        bus.setCurrentPassengers(0);
        return busRepository.save(bus);
    }
    
    /**
     * 버스 정보 업데이트
     */
    public Bus updateBus(Long id, Bus busDetails) {
        Bus bus = busRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Bus not found with id: " + id));
        
        bus.setBusNumber(busDetails.getBusNumber());
        bus.setLicensePlate(busDetails.getLicensePlate());
        bus.setDriverName(busDetails.getDriverName());
        bus.setDriverPhone(busDetails.getDriverPhone());
        bus.setCapacity(busDetails.getCapacity());
        bus.setRoute(busDetails.getRoute());
        
        return busRepository.save(bus);
    }
    
    /**
     * 버스 위치 업데이트
     */
    public Bus updateBusLocation(Long busId, Double latitude, Double longitude, Double speed) {
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> new RuntimeException("Bus not found with id: " + busId));
        
        bus.setCurrentLatitude(latitude);
        bus.setCurrentLongitude(longitude);
        bus.setSpeed(speed);
        bus.setLastUpdated(LocalDateTime.now());
        
        // 위치 히스토리 저장
        BusLocation location = new BusLocation();
        location.setBus(bus);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setSpeed(speed);
        location.setPassengers(bus.getCurrentPassengers());
        location.setTimestamp(LocalDateTime.now());
        
        busLocationRepository.save(location);
        
        return busRepository.save(bus);
    }
    
    /**
     * 버스 상태 변경
     */
    public Bus updateBusStatus(Long busId, BusStatus status) {
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> new RuntimeException("Bus not found with id: " + busId));
        
        bus.setStatus(status);
        return busRepository.save(bus);
    }
    
    /**
     * 탑승객 수 업데이트
     */
    public Bus updatePassengerCount(Long busId, Integer passengers) {
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> new RuntimeException("Bus not found with id: " + busId));
        
        if (passengers > bus.getCapacity()) {
            throw new RuntimeException("Passenger count exceeds bus capacity");
        }
        
        bus.setCurrentPassengers(passengers);
        return busRepository.save(bus);
    }
    
    /**
     * 특정 지역 내 버스 조회
     */
    public List<Bus> getBusesInArea(Double minLat, Double maxLat, Double minLon, Double maxLon) {
        return busRepository.findBusesInArea(minLat, maxLat, minLon, maxLon);
    }
    
    /**
     * 여석이 있는 버스 조회
     */
    public List<Bus> getAvailableBuses() {
        return busRepository.findAvailableBuses();
    }
    
    /**
     * 버스 삭제
     */
    public void deleteBus(Long id) {
        busRepository.deleteById(id);
    }
    
    /**
     * 버스 통계 조회
     */
    public BusStatistics getBusStatistics() {
        BusStatistics stats = new BusStatistics();
        stats.setTotalBuses(busRepository.count());
        stats.setActiveBuses(busRepository.countByStatus(BusStatus.ACTIVE));
        stats.setInactiveBuses(busRepository.countByStatus(BusStatus.INACTIVE));
        stats.setMaintenanceBuses(busRepository.countByStatus(BusStatus.MAINTENANCE));
        return stats;
    }
    
    /**
     * 버스 통계 내부 클래스
     */
    public static class BusStatistics {
        private Long totalBuses;
        private Long activeBuses;
        private Long inactiveBuses;
        private Long maintenanceBuses;
        
        // getters and setters
        public Long getTotalBuses() { return totalBuses; }
        public void setTotalBuses(Long totalBuses) { this.totalBuses = totalBuses; }
        public Long getActiveBuses() { return activeBuses; }
        public void setActiveBuses(Long activeBuses) { this.activeBuses = activeBuses; }
        public Long getInactiveBuses() { return inactiveBuses; }
        public void setInactiveBuses(Long inactiveBuses) { this.inactiveBuses = inactiveBuses; }
        public Long getMaintenanceBuses() { return maintenanceBuses; }
        public void setMaintenanceBuses(Long maintenanceBuses) { this.maintenanceBuses = maintenanceBuses; }
    }
}