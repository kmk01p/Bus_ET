package com.example.egovbus.service;

import com.example.egovbus.model.*;
import com.example.egovbus.repository.*;
import com.example.egovbus.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Random;

/**
 * User Service - Complete user management
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    
    /**
     * Register new user (passenger or driver)
     */
    public User registerUser(SignupRequest request) {
        // Check if user already exists
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered");
        }
        
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername() != null ? request.getUsername() : request.getPhoneNumber());
        user.setPhoneNumber(formatEthiopianPhone(request.getPhoneNumber()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        user.setPreferredLanguage(request.getPreferredLanguage() != null ? request.getPreferredLanguage() : "am");
        
        // Driver specific fields
        if (user.getRole() == UserRole.DRIVER) {
            user.setDriverLicenseNumber(request.getDriverLicenseNumber());
            user.setIsActive(false); // Drivers need admin approval
        } else {
            user.setIsActive(true); // Passengers are active immediately
        }
        
        User savedUser = userRepository.save(user);
        
        // Send verification OTP
        sendVerificationOTP(savedUser);
        
        log.info("New user registered: {} - {}", savedUser.getPhoneNumber(), savedUser.getRole());
        
        return savedUser;
    }
    
    /**
     * Send verification OTP via SMS
     */
    public void sendVerificationOTP(User user) {
        // Generate 6-digit OTP
        String otp = generateOTP();
        
        // Create verification token
        VerificationToken token = new VerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setOtp(otp);
        token.setUser(user);
        token.setType(TokenType.SMS_VERIFICATION);
        
        tokenRepository.save(token);
        
        // Send SMS
        String message = String.format(
            "Your Ethiopia Bus verification code is: %s. Valid for 10 minutes.",
            otp
        );
        
        if (user.getPreferredLanguage().equals("am")) {
            message = String.format(
                "የኢትዮጵያ አውቶብስ ማረጋገጫ ኮድዎ: %s. ለ10 ደቂቃ ብቻ ይሰራል።",
                otp
            );
        }
        
        notificationService.sendSMS(user.getPhoneNumber(), message);
        
        log.info("Verification OTP sent to: {}", user.getPhoneNumber());
    }
    
    /**
     * Verify phone number with OTP
     */
    public boolean verifyPhoneNumber(String phoneNumber, String otp) {
        User user = userRepository.findByPhoneNumber(formatEthiopianPhone(phoneNumber))
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        VerificationToken token = tokenRepository.findByUserAndTypeAndUsedFalse(
            user, TokenType.SMS_VERIFICATION)
            .orElseThrow(() -> new RuntimeException("No verification token found"));
        
        if (token.isExpired()) {
            throw new RuntimeException("OTP has expired");
        }
        
        if (!token.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }
        
        // Mark token as used
        token.setUsed(true);
        tokenRepository.save(token);
        
        // Activate user account
        user.setIsActive(true);
        userRepository.save(user);
        
        log.info("Phone verified for user: {}", phoneNumber);
        
        return true;
    }
    
    /**
     * Request password reset
     */
    public void requestPasswordReset(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(formatEthiopianPhone(phoneNumber))
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Generate OTP
        String otp = generateOTP();
        
        // Create reset token
        VerificationToken token = new VerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setOtp(otp);
        token.setUser(user);
        token.setType(TokenType.PASSWORD_RESET);
        
        tokenRepository.save(token);
        
        // Send SMS
        String message = String.format(
            "Your password reset code is: %s. Valid for 10 minutes.",
            otp
        );
        
        if (user.getPreferredLanguage().equals("am")) {
            message = String.format(
                "የይለፍ ቃል ዳግም ማስጀመሪያ ኮድዎ: %s. ለ10 ደቂቃ ብቻ ይሰራል።",
                otp
            );
        }
        
        notificationService.sendSMS(user.getPhoneNumber(), message);
        
        log.info("Password reset OTP sent to: {}", user.getPhoneNumber());
    }
    
    /**
     * Reset password with OTP
     */
    public void resetPassword(PasswordResetRequest request) {
        User user = userRepository.findByPhoneNumber(formatEthiopianPhone(request.getPhoneNumber()))
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        VerificationToken token = tokenRepository.findByUserAndTypeAndUsedFalse(
            user, TokenType.PASSWORD_RESET)
            .orElseThrow(() -> new RuntimeException("No reset token found"));
        
        if (token.isExpired()) {
            throw new RuntimeException("Reset code has expired");
        }
        
        if (!token.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid reset code");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // Mark token as used
        token.setUsed(true);
        tokenRepository.save(token);
        
        // Send confirmation SMS
        String message = "Your password has been successfully reset.";
        if (user.getPreferredLanguage().equals("am")) {
            message = "የይለፍ ቃልዎ በተሳካ ሁኔታ ተቀይሯል።";
        }
        
        notificationService.sendSMS(user.getPhoneNumber(), message);
        
        log.info("Password reset for user: {}", user.getPhoneNumber());
    }
    
    /**
     * Update user profile
     */
    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        
        if (request.getEmail() != null) {
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }
        
        if (request.getPreferredLanguage() != null) {
            user.setPreferredLanguage(request.getPreferredLanguage());
        }
        
        if (request.getSmsNotifications() != null) {
            user.setSmsNotifications(request.getSmsNotifications());
        }
        
        if (request.getPushNotifications() != null) {
            user.setPushNotifications(request.getPushNotifications());
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Change password
     */
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        log.info("Password changed for user: {}", user.getPhoneNumber());
    }
    
    /**
     * Generate 6-digit OTP
     */
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    
    /**
     * Format Ethiopian phone number
     */
    private String formatEthiopianPhone(String phone) {
        if (!phone.startsWith("+251")) {
            if (phone.startsWith("0")) {
                phone = "+251" + phone.substring(1);
            } else if (phone.startsWith("251")) {
                phone = "+" + phone;
            } else {
                phone = "+251" + phone;
            }
        }
        return phone;
    }
    
    /**
     * Get user by ID
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    /**
     * Delete user account
     */
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Soft delete - just deactivate
        user.setIsActive(false);
        userRepository.save(user);
        
        log.info("Account deactivated for user: {}", user.getPhoneNumber());
    }
    
    /**
     * Get user by phone number
     */
    public User getUserByPhone(String phoneNumber) {
        return userRepository.findByPhoneNumber(formatEthiopianPhone(phoneNumber))
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}