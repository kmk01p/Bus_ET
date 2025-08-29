# 🚌 Ethiopia Real-time Bus Monitoring Platform
# 에티오피아 실시간 버스 모니터링 플랫폼

<div align="center">
  
![Ethiopia Flag](https://img.shields.io/badge/🇪🇹_Ethiopia-Bus_Monitoring-green?style=for-the-badge)
![eGovFrame](https://img.shields.io/badge/eGovFrame-4.3.0-blue?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.7.18-brightgreen?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

</div>

## 📋 프로젝트 소개

에티오피아 실시간 버스 모니터링 플랫폼은 대한민국 전자정부 프레임워크(eGovFrame)를 기반으로 개발된 버스 위치 추적 및 관리 시스템입니다. 이 시스템은 에티오피아의 대중교통 효율성을 향상시키고 승객들에게 실시간 버스 정보를 제공하는 것을 목표로 합니다.

### 주요 특징

- 🗺️ **실시간 위치 추적**: GPS 기반 실시간 버스 위치 모니터링
- 📊 **통계 대시보드**: 운행 현황 및 통계 정보 시각화
- 🚏 **노선 관리**: 버스 노선 및 정류장 정보 관리
- 👥 **승객 정보**: 실시간 탑승객 수 및 여석 정보
- 📱 **반응형 디자인**: 모바일 및 태블릿 지원
- 🔄 **자동 업데이트**: 10초마다 자동으로 정보 갱신

## 🛠️ 기술 스택

### Backend
- **Framework**: eGovFrame 4.3.0 + Spring Boot 2.7.18
- **Language**: Java 17
- **Database**: H2 Database (In-Memory)
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven

### Frontend
- **HTML5/CSS3/JavaScript**
- **Map**: Leaflet.js
- **Icons**: Font Awesome
- **Style**: Responsive Design

## 📁 프로젝트 구조

```
egov-bus/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/egovbus/
│   │   │       ├── EgovBusApplication.java       # Main Application
│   │   │       ├── model/                        # Entity Models
│   │   │       │   ├── Bus.java
│   │   │       │   ├── BusStatus.java
│   │   │       │   ├── Route.java
│   │   │       │   └── BusLocation.java
│   │   │       ├── repository/                   # Data Access Layer
│   │   │       │   ├── BusRepository.java
│   │   │       │   ├── RouteRepository.java
│   │   │       │   └── BusLocationRepository.java
│   │   │       ├── service/                      # Business Logic
│   │   │       │   ├── BusService.java
│   │   │       │   └── RouteService.java
│   │   │       ├── controller/                   # REST APIs
│   │   │       │   ├── BusController.java
│   │   │       │   └── RouteController.java
│   │   │       └── scheduler/                    # Scheduled Tasks
│   │   │           └── BusLocationSimulator.java
│   │   └── resources/
│   │       ├── static/                          # Frontend Resources
│   │       │   ├── index.html
│   │       │   └── js/
│   │       │       └── app.js
│   │       └── application.properties           # Configuration
└── pom.xml                                       # Maven Configuration
```

## 🚀 설치 및 실행

### 필수 요구사항

- Java 17 이상
- Maven 3.6 이상
- 웹 브라우저 (Chrome, Firefox, Safari 등)

### 설치 방법

1. **프로젝트 클론**
```bash
git clone https://github.com/yourusername/ethiopia-bus-monitoring.git
cd ethiopia-bus-monitoring
```

2. **의존성 설치**
```bash
mvn clean install
```

3. **애플리케이션 실행**
```bash
mvn spring-boot:run
```

4. **브라우저에서 접속**
```
http://localhost:8080
```

### H2 Database Console 접속
```
http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:busdb
- Username: sa
- Password: (비워둠)
```

## 📡 API 엔드포인트

### Bus APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/buses` | 모든 버스 조회 |
| GET | `/api/buses/{id}` | 특정 버스 조회 |
| GET | `/api/buses/active` | 운행중인 버스 조회 |
| GET | `/api/buses/status/{status}` | 상태별 버스 조회 |
| GET | `/api/buses/route/{routeId}` | 노선별 버스 조회 |
| POST | `/api/buses` | 새 버스 등록 |
| PUT | `/api/buses/{id}` | 버스 정보 수정 |
| PATCH | `/api/buses/{id}/location` | 버스 위치 업데이트 |
| PATCH | `/api/buses/{id}/status` | 버스 상태 변경 |
| DELETE | `/api/buses/{id}` | 버스 삭제 |

### Route APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/routes` | 모든 노선 조회 |
| GET | `/api/routes/{id}` | 특정 노선 조회 |
| GET | `/api/routes/active` | 활성 노선 조회 |
| POST | `/api/routes` | 새 노선 등록 |
| PUT | `/api/routes/{id}` | 노선 정보 수정 |
| DELETE | `/api/routes/{id}` | 노선 삭제 |

## 🎯 주요 기능

### 1. 실시간 버스 위치 추적
- GPS 좌표 기반 실시간 위치 업데이트
- 10초마다 자동 갱신
- 지도상에 버스 아이콘으로 표시

### 2. 버스 상태 관리
- **ACTIVE**: 운행중
- **INACTIVE**: 운행종료
- **WAITING**: 대기중
- **MAINTENANCE**: 정비중
- **EMERGENCY**: 긴급상황
- **DELAYED**: 지연

### 3. 노선 관리
- 노선별 버스 배치
- 정류장 정보 관리
- 예상 소요 시간 및 요금 정보

### 4. 통계 및 모니터링
- 전체 버스 현황
- 운행중/대기중/정비중 버스 수
- 실시간 업데이트

## 🗺️ 지원 지역

현재 아디스아바바(Addis Ababa) 지역을 중심으로 서비스가 제공되며, 향후 다른 도시로 확대 예정입니다.

- **중심 좌표**: 9.03°N, 38.74°E
- **서비스 반경**: 약 11km

## 📝 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 👥 기여하기

프로젝트 개선에 기여하고 싶으시다면:

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 연락처

- **프로젝트 관리자**: Ethiopia Bus Authority
- **이메일**: busmonitoring@ethiopia.gov.et
- **긴급 연락처**: +251-11-515-8000

## 🙏 감사의 말

- 대한민국 행정안전부 - eGovFrame 제공
- OpenStreetMap - 지도 데이터 제공
- Spring Boot 커뮤니티

---

<div align="center">
Made with ❤️ for Ethiopia 🇪🇹
</div>