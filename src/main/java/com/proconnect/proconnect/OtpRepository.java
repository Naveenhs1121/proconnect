package com.proconnect.proconnect;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpCode, Long> {

    Optional<OtpCode> findTopByEmailAndPurposeAndUsedIsFalseOrderByExpiresAtDesc(
            String email,
            String purpose
    );

    long countByEmailAndPurposeAndExpiresAtAfter(String email, String purpose, LocalDateTime after);
}
