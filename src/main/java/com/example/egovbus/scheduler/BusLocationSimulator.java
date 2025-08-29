package com.example.egovbus.scheduler;

import com.example.egovbus.model.Bus;
import com.example.egovbus.model.BusStatus;
import com.example.egovbus.service.BusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Random;

/**
 * Bus Location Simulator
 * 실시간 버스 위치 시뮬레이션
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BusLocationSimulator {
    
    private final BusService busService;
    private final Random random = new Random();
    
    // 아디스아바바 좌표 범위 (에티오피아 수도)
    private static final double ADDIS_ABABA_LAT = 9.03;
    private static final double ADDIS_ABABA_LON = 38.74;
    private static final double COORD_RANGE = 0.1; // 약 11km 범위
    
    /**
     * 초기 데이터 설정
     */
    @PostConstruct
    public void initializeData() {
        log.info("Initializing bus location simulator...");
        
        List<Bus> buses = busService.getAllBuses();
        if (buses.isEmpty()) {
            log.info("No buses found. Skipping initialization.");
            return;
        }
        
        // 활성 버스들의 초기 위치 설정
        for (Bus bus : buses) {
            if (bus.getStatus() == BusStatus.ACTIVE) {
                double lat = ADDIS_ABABA_LAT + (random.nextDouble() - 0.5) * COORD_RANGE;
                double lon = ADDIS_ABABA_LON + (random.nextDouble() - 0.5) * COORD_RANGE;
                double speed = 20 + random.nextDouble() * 30; // 20-50 km/h
                
                try {
                    busService.updateBusLocation(bus.getId(), lat, lon, speed);
                } catch (Exception e) {
                    log.error("Failed to initialize location for bus {}: {}", bus.getBusNumber(), e.getMessage());
                }
            }
        }
    }
    
    /**
     * 10초마다 버스 위치 업데이트
     */
    @Scheduled(fixedDelay = 10000)
    public void updateBusLocations() {
        List<Bus> activeBuses = busService.getActiveBuses();
        
        for (Bus bus : activeBuses) {
            try {
                // 현재 위치 가져오기
                Double currentLat = bus.getCurrentLatitude();
                Double currentLon = bus.getCurrentLongitude();
                
                if (currentLat == null || currentLon == null) {
                    // 초기 위치 설정
                    currentLat = ADDIS_ABABA_LAT + (random.nextDouble() - 0.5) * COORD_RANGE;
                    currentLon = ADDIS_ABABA_LON + (random.nextDouble() - 0.5) * COORD_RANGE;
                }
                
                // 새로운 위치 계산 (작은 변화)
                double deltaLat = (random.nextDouble() - 0.5) * 0.002; // 약 200m 이동
                double deltaLon = (random.nextDouble() - 0.5) * 0.002;
                
                double newLat = currentLat + deltaLat;
                double newLon = currentLon + deltaLon;
                
                // 범위 제한
                newLat = Math.max(ADDIS_ABABA_LAT - COORD_RANGE/2, 
                         Math.min(ADDIS_ABABA_LAT + COORD_RANGE/2, newLat));
                newLon = Math.max(ADDIS_ABABA_LON - COORD_RANGE/2, 
                         Math.min(ADDIS_ABABA_LON + COORD_RANGE/2, newLon));
                
                // 속도 계산 (20-50 km/h)
                double speed = 20 + random.nextDouble() * 30;
                
                // 위치 업데이트
                busService.updateBusLocation(bus.getId(), newLat, newLon, speed);
                
                // 랜덤하게 승객 수 변경 (10% 확률)
                if (random.nextDouble() < 0.1) {
                    int passengerChange = random.nextInt(5) - 2; // -2 to +2
                    int newPassengers = Math.max(0, 
                        Math.min(bus.getCapacity(), 
                        (bus.getCurrentPassengers() != null ? bus.getCurrentPassengers() : 0) + passengerChange));
                    busService.updatePassengerCount(bus.getId(), newPassengers);
                }
                
                log.debug("Updated location for bus {}: ({}, {}), speed: {} km/h", 
                    bus.getBusNumber(), newLat, newLon, speed);
                    
            } catch (Exception e) {
                log.error("Failed to update location for bus {}: {}", bus.getBusNumber(), e.getMessage());
            }
        }
    }
    
    /**
     * 1분마다 버스 상태 체크 및 변경
     */
    @Scheduled(fixedDelay = 60000)
    public void updateBusStatuses() {
        List<Bus> allBuses = busService.getAllBuses();
        
        for (Bus bus : allBuses) {
            try {
                // 5% 확률로 상태 변경
                if (random.nextDouble() < 0.05) {
                    BusStatus currentStatus = bus.getStatus();
                    BusStatus newStatus = currentStatus;
                    
                    if (currentStatus == BusStatus.ACTIVE) {
                        // 활성 버스는 대기 상태로
                        if (random.nextDouble() < 0.3) {
                            newStatus = BusStatus.WAITING;
                        }
                    } else if (currentStatus == BusStatus.WAITING) {
                        // 대기 버스는 활성 상태로
                        newStatus = BusStatus.ACTIVE;
                    } else if (currentStatus == BusStatus.INACTIVE) {
                        // 비활성 버스는 20% 확률로 활성화
                        if (random.nextDouble() < 0.2) {
                            newStatus = BusStatus.ACTIVE;
                        }
                    }
                    
                    if (newStatus != currentStatus) {
                        busService.updateBusStatus(bus.getId(), newStatus);
                        log.info("Bus {} status changed from {} to {}", 
                            bus.getBusNumber(), currentStatus, newStatus);
                    }
                }
            } catch (Exception e) {
                log.error("Failed to update status for bus {}: {}", bus.getBusNumber(), e.getMessage());
            }
        }
    }
}