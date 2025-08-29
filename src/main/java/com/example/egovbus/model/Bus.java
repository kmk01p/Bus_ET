package com.example.egovbus.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Bus Entity - 버스 정보를 저장하는 엔티티
 */
@Entity
@Table(name = "buses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String busNumber;  // 버스 번호 (예: AA-101)
    
    @Column(nullable = false)
    private String licensePlate;  // 차량 번호판
    
    private String driverName;  // 운전자 이름
    
    private String driverPhone;  // 운전자 연락처
    
    @OneToOne
    @JoinColumn(name = "driver_id")
    private User driver;  // 운전자 계정 연결
    
    @Column(nullable = false)
    private Integer capacity;  // 버스 정원
    
    private Integer currentPassengers;  // 현재 탑승객 수
    
    @Enumerated(EnumType.STRING)
    private BusStatus status;  // 버스 상태
    
    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;  // 운행 노선
    
    private Double currentLatitude;  // 현재 위도
    
    private Double currentLongitude;  // 현재 경도
    
    private Double speed;  // 현재 속도 (km/h)
    
    private LocalDateTime lastUpdated;  // 마지막 업데이트 시간
    
    private String nextStop;  // 다음 정류장
    
    private Integer estimatedArrival;  // 예상 도착 시간 (분)
    
    @PreUpdate
    @PrePersist
    public void updateTimestamp() {
        this.lastUpdated = LocalDateTime.now();
    }
}