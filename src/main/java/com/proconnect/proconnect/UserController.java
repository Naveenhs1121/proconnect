package com.proconnect.proconnect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserRepository repo;

    @Autowired
    private ProfessionalRepository professionalRepo;

    // REGISTER (simple example; hash password in real app)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            if (user.getEmail() == null || user.getPassword() == null) {
                return ResponseEntity.badRequest().body("Email and password required");
            }
            if (repo.findByEmail(user.getEmail()).isPresent()) {
                return ResponseEntity.status(409).body("Email already registered");
            }
            // hash password in production
            User saved = repo.save(user);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("ERROR: " + e.getMessage());
        }
    }

    // Send OTP (DB-backed)
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam String email) {
        try {
            otpService.createAndSendOtp(email, "USER", "REGISTER");
            return ResponseEntity.ok("OTP sent to " + email);
        } catch (IllegalStateException ise) {
            return ResponseEntity.status(429).body(ise.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to send OTP: " + e.getMessage());
        }
    }

    // Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email,
                                       @RequestParam String otp) {
        boolean ok = otpService.verifyOtp(email, "REGISTER", otp);
        if (!ok) return ResponseEntity.badRequest().body("Invalid or expired OTP");
        return ResponseEntity.ok("OTP verified");
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User userLogin){
        try {
            Optional<User> userOpt = repo.findByEmail(userLogin.getEmail());
            if (userOpt.isEmpty()) return ResponseEntity.status(404).body("User not found");
            User user = userOpt.get();
            
            if (user.getPassword() == null || !user.getPassword().equals(userLogin.getPassword())) {
                return ResponseEntity.status(401).body("Wrong password");
            }
    
            if ("PROFESSIONAL".equalsIgnoreCase(user.getRole())) {
                Professional pro = professionalRepo.findByEmail(user.getEmail());
                if (pro != null) {
                    return ResponseEntity.ok(pro);
                }
            }
    
            return ResponseEntity.ok(user);
        } catch (org.springframework.dao.IncorrectResultSizeDataAccessException | jakarta.persistence.NonUniqueResultException e) {
            return ResponseEntity.status(409).body("Duplicate account found. Please contact support.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Login failed: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getProfile(@PathVariable Long id) {
        return repo.findById(id)
            .map(u -> {
                u.setPassword(null);
                return ResponseEntity.ok(u);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateProfile(@PathVariable Long id, @RequestBody User updated) {
        return repo.findById(id).map(user -> {
            if (updated.getName() != null) user.setName(updated.getName());
            if (updated.getPhone() != null) user.setPhone(updated.getPhone());
            if (updated.getCity() != null) user.setCity(updated.getCity());
            repo.save(user);
            user.setPassword(null);
            return ResponseEntity.ok(user);
        }).orElse(ResponseEntity.notFound().build());
    }
}
