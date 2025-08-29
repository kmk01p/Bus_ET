package com.example.egovbus.controller;

import com.example.egovbus.model.*;
import com.example.egovbus.service.*;
import com.example.egovbus.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

/**
 * Admin Controller - Administrator functions
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
// @PreAuthorize("hasRole('ADMIN')")  // Enable in production
public class AdminController {
    
    private final UserRepository userRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    
    /**
     * Get dashboard statistics
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // User statistics
        stats.put("totalUsers", userRepository.count());
        stats.put("totalDrivers", userRepository.findByRole(UserRole.DRIVER).size());
        stats.put("totalPassengers", userRepository.findByRole(UserRole.PASSENGER).size());
        stats.put("pendingDrivers", userRepository.findByRole(UserRole.DRIVER).stream()
            .filter(d -> !d.getIsActive()).count());
        
        // Bus statistics
        stats.put("totalBuses", busRepository.count());
        stats.put("activeBuses", busRepository.findByStatus(BusStatus.ACTIVE).size());
        stats.put("maintenanceBuses", busRepository.findByStatus(BusStatus.MAINTENANCE).size());
        
        // Route statistics
        stats.put("totalRoutes", routeRepository.count());
        stats.put("activeRoutes", routeRepository.findByIsActive(true).size());
        
        // Reservation statistics
        stats.put("totalReservations", reservationRepository.count());
        stats.put("todayReservations", reservationRepository.findAll().stream()
            .filter(r -> r.getReservationTime().toLocalDate().equals(
                java.time.LocalDate.now())).count());
        
        // Payment statistics
        stats.put("totalPayments", paymentRepository.count());
        stats.put("todayRevenue", calculateTodayRevenue());
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get all users
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(
            @RequestParam(required = false) String role) {
        List<User> users;
        if (role != null) {
            users = userRepository.findByRole(UserRole.valueOf(role.toUpperCase()));
        } else {
            users = userRepository.findAll();
        }
        return ResponseEntity.ok(users);
    }
    
    /**
     * Approve/Activate driver account
     */
    @PutMapping("/users/{userId}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setIsActive(true);
        userRepository.save(user);
        
        return ResponseEntity.ok(Map.of(
            "message", "User activated successfully",
            "user", user
        ));
    }
    
    /**
     * Deactivate user account
     */
    @PutMapping("/users/{userId}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setIsActive(false);
        userRepository.save(user);
        
        return ResponseEntity.ok(Map.of(
            "message", "User deactivated successfully"
        ));
    }
    
    /**
     * Assign driver to bus
     */
    @PutMapping("/buses/{busId}/assign-driver/{driverId}")
    public ResponseEntity<?> assignDriverToBus(
            @PathVariable Long busId,
            @PathVariable Long driverId) {
        
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> new RuntimeException("Bus not found"));
        
        User driver = userRepository.findById(driverId)
            .orElseThrow(() -> new RuntimeException("Driver not found"));
        
        if (driver.getRole() != UserRole.DRIVER) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "User is not a driver"
            ));
        }
        
        // Remove driver from previous bus if any
        if (driver.getAssignedBus() != null) {
            Bus previousBus = driver.getAssignedBus();
            previousBus.setDriver(null);
            busRepository.save(previousBus);
        }
        
        // Assign new bus
        bus.setDriver(driver);
        bus.setDriverName(driver.getFullName());
        bus.setDriverPhone(driver.getPhoneNumber());
        busRepository.save(bus);
        
        driver.setAssignedBus(bus);
        userRepository.save(driver);
        
        return ResponseEntity.ok(Map.of(
            "message", "Driver assigned successfully",
            "bus", bus
        ));
    }
    
    /**
     * Create new bus
     */
    @PostMapping("/buses")
    public ResponseEntity<?> createBus(@RequestBody Bus bus) {
        Bus savedBus = busRepository.save(bus);
        return ResponseEntity.ok(savedBus);
    }
    
    /**
     * Update bus
     */
    @PutMapping("/buses/{busId}")
    public ResponseEntity<?> updateBus(
            @PathVariable Long busId,
            @RequestBody Bus busDetails) {
        
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> new RuntimeException("Bus not found"));
        
        bus.setBusNumber(busDetails.getBusNumber());
        bus.setLicensePlate(busDetails.getLicensePlate());
        bus.setCapacity(busDetails.getCapacity());
        bus.setStatus(busDetails.getStatus());
        bus.setRoute(busDetails.getRoute());
        
        Bus updatedBus = busRepository.save(bus);
        return ResponseEntity.ok(updatedBus);
    }
    
    /**
     * Delete bus
     */
    @DeleteMapping("/buses/{busId}")
    public ResponseEntity<?> deleteBus(@PathVariable Long busId) {
        busRepository.deleteById(busId);
        return ResponseEntity.ok(Map.of(
            "message", "Bus deleted successfully"
        ));
    }
    
    /**
     * Get all routes
     */
    @GetMapping("/routes")
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(routeRepository.findAll());
    }
    
    /**
     * Create new route
     */
    @PostMapping("/routes")
    public ResponseEntity<?> createRoute(@RequestBody Route route) {
        Route savedRoute = routeRepository.save(route);
        return ResponseEntity.ok(savedRoute);
    }
    
    /**
     * Update route
     */
    @PutMapping("/routes/{routeId}")
    public ResponseEntity<?> updateRoute(
            @PathVariable Long routeId,
            @RequestBody Route routeDetails) {
        
        Route route = routeRepository.findById(routeId)
            .orElseThrow(() -> new RuntimeException("Route not found"));
        
        route.setRouteNumber(routeDetails.getRouteNumber());
        route.setRouteName(routeDetails.getRouteName());
        route.setStartPoint(routeDetails.getStartPoint());
        route.setEndPoint(routeDetails.getEndPoint());
        route.setStops(routeDetails.getStops());
        route.setFare(routeDetails.getFare());
        route.setIsActive(routeDetails.getIsActive());
        
        Route updatedRoute = routeRepository.save(route);
        return ResponseEntity.ok(updatedRoute);
    }
    
    /**
     * Delete route
     */
    @DeleteMapping("/routes/{routeId}")
    public ResponseEntity<?> deleteRoute(@PathVariable Long routeId) {
        routeRepository.deleteById(routeId);
        return ResponseEntity.ok(Map.of(
            "message", "Route deleted successfully"
        ));
    }
    
    /**
     * Get payment reports
     */
    @GetMapping("/payments/report")
    public ResponseEntity<?> getPaymentReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        List<Payment> payments = paymentRepository.findAll();
        
        Map<String, Object> report = new HashMap<>();
        report.put("totalPayments", payments.size());
        report.put("totalRevenue", payments.stream()
            .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
            .mapToDouble(p -> p.getAmount().doubleValue())
            .sum());
        report.put("payments", payments);
        
        return ResponseEntity.ok(report);
    }
    
    private double calculateTodayRevenue() {
        return paymentRepository.findAll().stream()
            .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
            .filter(p -> p.getPaymentTime().toLocalDate().equals(
                java.time.LocalDate.now()))
            .mapToDouble(p -> p.getAmount().doubleValue())
            .sum();
    }
}