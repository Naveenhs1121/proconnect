package com.proconnect.proconnect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingRepository repo;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private EscrowService escrowService;

    // 1) Create a booking with conflict check
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Booking booking) {
        // default status if not set
        if (booking.getStatus() == null || booking.getStatus().isEmpty()) {
            booking.setStatus("PENDING");
        }

        // Set payment status to UNPAID by default
        if (booking.getPaymentStatus() == null || booking.getPaymentStatus().isEmpty()) {
            booking.setPaymentStatus("UNPAID");
        }

        // Calculate total amount based on professional's hourly rate
        Professional professional = professionalRepository.findById(booking.getProfessionalId())
                .orElseThrow(() -> new RuntimeException("Professional not found"));
        
        // Assume 1 hour booking for simplicity (can be enhanced later)
        Double hourlyRate = professional.getHourlyRate() != null ? professional.getHourlyRate() : 50.0;
        booking.setTotalAmount(hourlyRate);

        // Check for conflicts
        List<Booking> existing = repo.findByProfessionalIdAndDateAndStatusNot(
                booking.getProfessionalId(), 
                booking.getDate(), 
                "CANCELLED"
        );
        
        for (Booking b : existing) {
            if (b.getTimeSlot().equals(booking.getTimeSlot())) {
                return ResponseEntity.status(409).body("Time slot already booked");
            }
        }

        return ResponseEntity.ok(repo.save(booking));
    }

    // 2) Get all bookings for a user
    @GetMapping("/user/{userId}")
    public List<Booking> getByUser(@PathVariable Long userId) {
        return repo.findByUserId(userId);
    }

    // 3) Get all bookings for a professional
    @GetMapping("/professional/{profId}")
    public List<Booking> getByProfessional(@PathVariable("profId") Long professionalId) {
        return repo.findByProfessionalId(professionalId);
    }

    // 3.5) Get booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4) Update booking status (CONFIRMED, COMPLETED, CANCELLED)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        Optional<Booking> bookingOpt = repo.findById(id);

        if (bookingOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Booking not found");
        }

        Booking booking = bookingOpt.get();
        booking.setStatus(status);
        repo.save(booking);

        // If booking is completed, release escrow funds
        if ("COMPLETED".equals(status)) {
            escrowService.releaseFunds(id);
        }

        return ResponseEntity.ok(booking);
    }
}
