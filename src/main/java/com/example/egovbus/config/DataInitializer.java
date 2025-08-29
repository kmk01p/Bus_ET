package com.example.egovbus.config;

import com.example.egovbus.model.*;
import com.example.egovbus.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Data Initializer - Creates initial admin and sample data
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final BusRepository busRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Bean
    CommandLineRunner init() {
        return args -> {
            // Create Super Admin if not exists
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPhoneNumber("+251911000000");
                admin.setPassword(passwordEncoder.encode("Admin@2024#Ethiopia"));
                admin.setFullName("System Administrator");
                admin.setEmail("admin@ethiopiabus.gov.et");
                admin.setRole(UserRole.ADMIN);
                admin.setIsActive(true);
                admin.setPreferredLanguage("en");
                
                userRepository.save(admin);
                log.info("Super Admin created - Username: admin, Password: Admin@2024#Ethiopia");
            }
            
            // Create sample routes if none exist
            if (routeRepository.count() == 0) {
                createSampleRoutes();
            }
            
            // Create sample buses if none exist
            if (busRepository.count() == 0) {
                createSampleBuses();
            }
            
            // Create sample drivers if none exist
            if (userRepository.findByRole(UserRole.DRIVER).isEmpty()) {
                createSampleDrivers();
            }
            
            log.info("Database initialization completed");
        };
    }
    
    private void createSampleRoutes() {
        Route route1 = new Route();
        route1.setRouteNumber("R001");
        route1.setRouteName("Bole - Merkato");
        route1.setStartPoint("Bole International Airport");
        route1.setEndPoint("Merkato Bus Station");
        route1.setStops(Arrays.asList(
            "Bole Airport", "Bole Medhanialem", "Mexico Square", 
            "Meskel Square", "Leghar", "Piassa", "Merkato"
        ));
        route1.setTotalDistance(15.5);
        route1.setEstimatedTime(45);
        route1.setFare(25.0);
        route1.setOperatingHours("05:00 AM - 11:00 PM");
        route1.setIsActive(true);
        
        Route route2 = new Route();
        route2.setRouteNumber("R002");
        route2.setRouteName("Kality - CMC");
        route2.setStartPoint("Kality Total");
        route2.setEndPoint("CMC Michael");
        route2.setStops(Arrays.asList(
            "Kality", "Saris Abo", "Saris", "Gotera", 
            "Mexico", "Bambis", "CMC"
        ));
        route2.setTotalDistance(18.0);
        route2.setEstimatedTime(55);
        route2.setFare(30.0);
        route2.setOperatingHours("05:30 AM - 10:30 PM");
        route2.setIsActive(true);
        
        Route route3 = new Route();
        route3.setRouteNumber("R003");
        route3.setRouteName("Megenagna - Tor Hailoch");
        route3.setStartPoint("Megenagna");
        route3.setEndPoint("Tor Hailoch");
        route3.setStops(Arrays.asList(
            "Megenagna", "Yeka", "Kotebe", "Ayat", 
            "Kara", "Lebu", "Tor Hailoch"
        ));
        route3.setTotalDistance(12.0);
        route3.setEstimatedTime(35);
        route3.setFare(20.0);
        route3.setOperatingHours("06:00 AM - 10:00 PM");
        route3.setIsActive(true);
        
        routeRepository.saveAll(Arrays.asList(route1, route2, route3));
        log.info("Sample routes created");
    }
    
    private void createSampleBuses() {
        Route route1 = routeRepository.findByRouteNumber("R001").orElse(null);
        Route route2 = routeRepository.findByRouteNumber("R002").orElse(null);
        Route route3 = routeRepository.findByRouteNumber("R003").orElse(null);
        
        if (route1 != null) {
            for (int i = 1; i <= 3; i++) {
                Bus bus = new Bus();
                bus.setBusNumber("AA-10" + i);
                bus.setLicensePlate("3-A-1234" + i);
                bus.setCapacity(50);
                bus.setCurrentPassengers(0);
                bus.setStatus(BusStatus.INACTIVE);
                bus.setRoute(route1);
                bus.setCurrentLatitude(9.03 + (Math.random() - 0.5) * 0.1);
                bus.setCurrentLongitude(38.74 + (Math.random() - 0.5) * 0.1);
                busRepository.save(bus);
            }
        }
        
        if (route2 != null) {
            for (int i = 4; i <= 6; i++) {
                Bus bus = new Bus();
                bus.setBusNumber("AA-10" + i);
                bus.setLicensePlate("3-A-1234" + i);
                bus.setCapacity(45);
                bus.setCurrentPassengers(0);
                bus.setStatus(BusStatus.INACTIVE);
                bus.setRoute(route2);
                bus.setCurrentLatitude(9.03 + (Math.random() - 0.5) * 0.1);
                bus.setCurrentLongitude(38.74 + (Math.random() - 0.5) * 0.1);
                busRepository.save(bus);
            }
        }
        
        if (route3 != null) {
            for (int i = 7; i <= 9; i++) {
                Bus bus = new Bus();
                bus.setBusNumber("AA-10" + i);
                bus.setLicensePlate("3-A-1234" + i);
                bus.setCapacity(40);
                bus.setCurrentPassengers(0);
                bus.setStatus(BusStatus.INACTIVE);
                bus.setRoute(route3);
                bus.setCurrentLatitude(9.03 + (Math.random() - 0.5) * 0.1);
                bus.setCurrentLongitude(38.74 + (Math.random() - 0.5) * 0.1);
                busRepository.save(bus);
            }
        }
        
        log.info("Sample buses created");
    }
    
    private void createSampleDrivers() {
        String[] driverNames = {
            "Abebe Kebede", "Tadesse Haile", "Getachew Molla",
            "Solomon Bekele", "Yohannes Tesfaye", "Daniel Girma",
            "Mulugeta Assefa", "Tewodros Alemu", "Hailu Mekonnen"
        };
        
        String[] phoneNumbers = {
            "911111111", "911111112", "911111113",
            "911111114", "911111115", "911111116",
            "911111117", "911111118", "911111119"
        };
        
        for (int i = 0; i < driverNames.length; i++) {
            User driver = new User();
            driver.setUsername("driver" + (i + 1));
            driver.setPhoneNumber("+251" + phoneNumbers[i]);
            driver.setPassword(passwordEncoder.encode("Driver123!"));
            driver.setFullName(driverNames[i]);
            driver.setEmail("driver" + (i + 1) + "@ethiopiabus.com");
            driver.setRole(UserRole.DRIVER);
            driver.setIsActive(true);
            driver.setDriverLicenseNumber("DL-2024-" + String.format("%04d", i + 1));
            driver.setLicenseExpiryDate(LocalDateTime.now().plusYears(2));
            driver.setPreferredLanguage("am");
            
            User savedDriver = userRepository.save(driver);
            
            // Assign driver to bus
            Bus bus = busRepository.findByBusNumber("AA-10" + (i + 1)).orElse(null);
            if (bus != null) {
                bus.setDriver(savedDriver);
                bus.setDriverName(savedDriver.getFullName());
                bus.setDriverPhone(savedDriver.getPhoneNumber());
                busRepository.save(bus);
                
                savedDriver.setAssignedBus(bus);
                userRepository.save(savedDriver);
            }
        }
        
        log.info("Sample drivers created - Default password: Driver123!");
    }
}