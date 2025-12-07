package com.proconnect.proconnect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public Payment processPayment(Long bookingId, Double amount) {
        // Mock payment processing logic
        // In reality, this would talk to Stripe/PayPal

        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(amount);
        payment.setStatus("SUCCESS"); // Assume success for mock
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setTimestamp(LocalDateTime.now());
        
        paymentRepository.save(payment);

        // Update booking status
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        booking.setPaymentStatus("PAID");
        // We might want to auto-confirm if paid, or keep pending professional approval?
        // Let's keep status as is (PENDING or CONFIRMED depending on flow)
        // Usually, payment confirms the booking slot.
        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);

        return payment;
    }
}
