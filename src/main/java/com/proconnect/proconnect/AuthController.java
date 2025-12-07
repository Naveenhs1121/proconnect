package com.proconnect.proconnect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private OtpRepository otpRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProfessionalRepository professionalRepo;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequest req) {
        if (req.getEmail() == null || req.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Email required");
        }
        if (!req.getEmail().endsWith("@gmail.com")) {
            return ResponseEntity.badRequest().body("Email must be a @gmail.com address");
        }

        try {
            otpService.createAndSendOtp(req.getEmail(), req.getRole(), "REGISTER");
            return ResponseEntity.ok("OTP sent to email");
        } catch (IllegalStateException ise) {
            return ResponseEntity.status(429).body(ise.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to send OTP: " + e.getMessage());
        }
    }

    @PostMapping("/register-with-otp")
    public ResponseEntity<?> registerWithOtp(@RequestBody RegisterWithOtpRequest req) {
        boolean ok = otpService.verifyOtp(req.getEmail(), "REGISTER", req.getOtp());
        if (!ok) return ResponseEntity.badRequest().body("Invalid or expired OTP");

        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        // Validate password complexity
        // At least 8 chars, 1 upper, 1 lower, 1 number, 1 special
        String passRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        if (req.getPassword() == null || !req.getPassword().matches(passRegex)) {
            return ResponseEntity.badRequest().body("Password must be 8+ chars, incl. Upper, Lower, Number, Special");
        }

        // create user (remember to hash password in production)
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(req.getPassword());
        user.setRole(req.getRole()); // USER / PROFESSIONAL
        User savedUser = userRepo.save(user);

        if ("PROFESSIONAL".equalsIgnoreCase(req.getRole())) {
            Professional p = new Professional();
            p.setName(req.getName());
            p.setEmail(req.getEmail());
            p.setPhone(req.getPhone());
            p.setServiceType(req.getServiceType());
            p.setCity(req.getCity());
            professionalRepo.save(p);
        }

        return ResponseEntity.ok(savedUser);
    }
}
