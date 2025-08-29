package com.example.egovbus.controller;

import com.example.egovbus.dto.*;
import com.example.egovbus.model.User;
import com.example.egovbus.service.UserService;
import com.example.egovbus.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Map;

/**
 * User Controller - Complete user management endpoints
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    private final AuthService authService;
    
    /**
     * User signup (Passenger/Driver)
     */
    @PostMapping("/auth/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            User user = userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Registration successful. Please verify your phone number.",
                "userId", user.getId(),
                "phoneNumber", user.getPhoneNumber()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Verify phone number with OTP
     */
    @PostMapping("/auth/verify-phone")
    public ResponseEntity<?> verifyPhone(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            String otp = request.get("otp");
            
            boolean verified = userService.verifyPhoneNumber(phoneNumber, otp);
            
            if (verified) {
                return ResponseEntity.ok(Map.of(
                    "message", "Phone number verified successfully"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Verification failed"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Resend verification OTP
     */
    @PostMapping("/auth/resend-otp")
    public ResponseEntity<?> resendOTP(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            User user = userService.getUserByPhone(phoneNumber);
            userService.sendVerificationOTP(user);
            
            return ResponseEntity.ok(Map.of(
                "message", "Verification code sent successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Request password reset
     */
    @PostMapping("/auth/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            userService.requestPasswordReset(phoneNumber);
            
            return ResponseEntity.ok(Map.of(
                "message", "Reset code sent to your phone number"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Reset password with OTP
     */
    @PostMapping("/auth/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        try {
            userService.resetPassword(request);
            
            return ResponseEntity.ok(Map.of(
                "message", "Password reset successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get user profile
     */
    @GetMapping("/user/profile/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update user profile
     */
    @PutMapping("/user/profile/{userId}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileRequest request) {
        try {
            User user = userService.updateProfile(userId, request);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Change password
     */
    @PostMapping("/user/change-password/{userId}")
    public ResponseEntity<?> changePassword(
            @PathVariable Long userId,
            @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(userId, request);
            return ResponseEntity.ok(Map.of(
                "message", "Password changed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete account (soft delete)
     */
    @DeleteMapping("/user/account/{userId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long userId) {
        try {
            userService.deleteAccount(userId);
            return ResponseEntity.ok(Map.of(
                "message", "Account deactivated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}