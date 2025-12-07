package com.proconnect.proconnect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepo;

    @Autowired
    private EmailService emailService;

    private String generateOtpCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    @Transactional
    public String createAndSendOtp(String email, String role, String purpose) {

        String otpCode = generateOtpCode();

        // Save OTP in DB
        OtpCode otp = new OtpCode();
        otp.setEmail(email);
        otp.setCode(otpCode);
        otp.setRole(role);
        otp.setPurpose(purpose);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);
        otpRepo.save(otp);

        // Send OTP to email
        emailService.sendOtp(email, otpCode);

        return otpCode;
    }

    @Transactional
    public boolean verifyOtp(String email, String purpose, String otpToCheck) {

        var otpOpt = otpRepo.findTopByEmailAndPurposeAndUsedIsFalseOrderByExpiresAtDesc(email, purpose);
        if (otpOpt.isEmpty()) return false;

        OtpCode otp = otpOpt.get();

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) return false;
        if (!otp.getCode().equals(otpToCheck)) return false;

        otp.setUsed(true);
        otpRepo.save(otp);
        return true;
    }
}
