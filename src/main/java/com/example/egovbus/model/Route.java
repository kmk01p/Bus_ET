package com.example.egovbus.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.util.List;

/**
 * Route Entity - 버스 노선 정보
 */
@Entity
@Table(name = "routes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String routeNumber;  // 노선 번호
    
    @Column(nullable = false)
    private String routeName;  // 노선 이름
    
    private String startPoint;  // 출발지
    
    private String endPoint;  // 종착지
    
    @ElementCollection
    @CollectionTable(name = "route_stops", joinColumns = @JoinColumn(name = "route_id"))
    @Column(name = "stop_name")
    private List<String> stops;  // 정류장 목록
    
    private Double totalDistance;  // 총 거리 (km)
    
    private Integer estimatedTime;  // 예상 소요 시간 (분)
    
    private Double fare;  // 요금 (ETB - Ethiopian Birr)
    
    private String operatingHours;  // 운영 시간
    
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private List<Bus> buses;  // 해당 노선 버스들
    
    private Boolean isActive;  // 노선 운영 여부
}