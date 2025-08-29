package com.example.egovbus.repository;

import com.example.egovbus.model.User;
import com.example.egovbus.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
}