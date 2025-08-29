package com.example.egovbus.repository;

import com.example.egovbus.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUserAndTypeAndUsedFalse(User user, TokenType type);
    void deleteByUserAndType(User user, TokenType type);
}