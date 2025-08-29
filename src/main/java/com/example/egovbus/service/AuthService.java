package com.example.egovbus.service;

import com.example.egovbus.model.User;
import com.example.egovbus.model.UserRole;
import com.example.egovbus.repository.UserRepository;
import com.example.egovbus.dto.LoginRequest;
import com.example.egovbus.dto.LoginResponse;
import com.example.egovbus.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * Authentication Service
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    
    /**
     * Driver login
     */
    public LoginResponse driverLogin(LoginRequest request) {
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
            .orElseThrow(() -> new RuntimeException("Invalid phone number or password"));
        
        if (user.getRole() != UserRole.DRIVER) {
            throw new RuntimeException("Access denied. Driver account required.");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid phone number or password");
        }
        
        if (!user.getIsActive()) {
            throw new RuntimeException("Account is deactivated");
        }
        
        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        // Generate JWT token
        String token = tokenProvider.generateToken(user);
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole().name());
        response.setPhoneNumber(user.getPhoneNumber());
        
        if (user.getAssignedBus() != null) {
            response.setBusId(user.getAssignedBus().getId());
            response.setBusNumber(user.getAssignedBus().getBusNumber());
        }
        
        log.info("Driver login successful: {}", user.getPhoneNumber());
        return response;
    }
    
    /**
     * Passenger login
     */
    public LoginResponse passengerLogin(LoginRequest request) {
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
            .orElseThrow(() -> new RuntimeException("Invalid phone number or password"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid phone number or password");
        }
        
        if (!user.getIsActive()) {
            throw new RuntimeException("Account is deactivated");
        }
        
        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        // Generate JWT token
        String token = tokenProvider.generateToken(user);
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole().name());
        response.setPhoneNumber(user.getPhoneNumber());
        
        log.info("Passenger login successful: {}", user.getPhoneNumber());
        return response;
    }
    
    /**
     * Register new driver
     */
    public User registerDriver(User driver) {
        if (userRepository.existsByPhoneNumber(driver.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered");
        }
        
        driver.setPassword(passwordEncoder.encode(driver.getPassword()));
        driver.setRole(UserRole.DRIVER);
        driver.setIsActive(false); // Requires admin approval
        
        User savedDriver = userRepository.save(driver);
        log.info("New driver registered: {}", driver.getPhoneNumber());
        
        return savedDriver;
    }
    
    /**
     * Register new passenger
     */
    public User registerPassenger(User passenger) {
        if (userRepository.existsByPhoneNumber(passenger.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered");
        }
        
        passenger.setPassword(passwordEncoder.encode(passenger.getPassword()));
        passenger.setRole(UserRole.PASSENGER);
        passenger.setIsActive(true);
        
        User savedPassenger = userRepository.save(passenger);
        log.info("New passenger registered: {}", passenger.getPhoneNumber());
        
        return savedPassenger;
    }
}