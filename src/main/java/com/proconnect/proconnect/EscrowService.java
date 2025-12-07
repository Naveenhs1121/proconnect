package com.proconnect.proconnect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class EscrowService {

    @Autowired
    private EscrowTransactionRepository escrowRepository;

    public void holdFunds(Long bookingId, Long professionalId, Double amount) {
        EscrowTransaction escrow = new EscrowTransaction();
        escrow.setBookingId(bookingId);
        escrow.setProfessionalId(professionalId);
        escrow.setAmount(amount);
        escrow.setStatus("HELD");
        escrow.setCreatedAt(LocalDateTime.now());
        
        escrowRepository.save(escrow);
    }

    public void releaseFunds(Long bookingId) {
        EscrowTransaction escrow = escrowRepository.findByBookingId(bookingId)
                .stream()
                .filter(e -> "HELD".equals(e.getStatus()))
                .findFirst()
                .orElse(null);

        if (escrow != null) {
            escrow.setStatus("RELEASED");
            escrow.setReleasedAt(LocalDateTime.now());
            escrowRepository.save(escrow);
            System.out.println("Funds released for booking: " + bookingId);
        }
    }
}
