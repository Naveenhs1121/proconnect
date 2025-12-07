package com.proconnect.proconnect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Backwards-compatible: previous method that generated its own OTP and returned it.
    // You may remove this if you don't need it.
    public String sendOtp(String toEmail) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        sendOtp(toEmail, otp);
        return otp;
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        String e = email.trim();
        // simple regex, good for basic validation
        return e.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    // Preferred: send the provided OTP (single source of truth)


    public void sendOtp(String toEmail, String otp) {
        if (toEmail == null) throw new IllegalArgumentException("Recipient email is null");
        String email = toEmail.trim();
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid recipient email: " + toEmail);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP - ProConnect Verification");
        message.setText("Your OTP is: " + otp + "\nValid for 5 minutes.");
        
        System.out.println(">>> DEBUG OTP for " + email + ": " + otp);
        
        mailSender.send(message);
    }
}