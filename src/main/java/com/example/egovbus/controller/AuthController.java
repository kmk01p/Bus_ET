package com.example.egovbus.controller;

import com.example.egovbus.dto.LoginRequest;
import com.example.egovbus.dto.LoginResponse;
import com.example.egovbus.model.User;
import com.example.egovbus.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * Driver login endpoint
     */
    @PostMapping("/driver/login")
    public ResponseEntity<LoginResponse> driverLogin(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.driverLogin(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    /**
     * Passenger login endpoint
     */
    @PostMapping("/passenger/login")
    public ResponseEntity<LoginResponse> passengerLogin(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.passengerLogin(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    /**
     * Driver registration
     */
    @PostMapping("/driver/register")
    public ResponseEntity<User> registerDriver(@RequestBody User driver) {
        try {
            User registered = authService.registerDriver(driver);
            return ResponseEntity.status(HttpStatus.CREATED).body(registered);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Passenger registration
     */
    @PostMapping("/passenger/register")
    public ResponseEntity<User> registerPassenger(@RequestBody User passenger) {
        try {
            User registered = authService.registerPassenger(passenger);
            return ResponseEntity.status(HttpStatus.CREATED).body(registered);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}