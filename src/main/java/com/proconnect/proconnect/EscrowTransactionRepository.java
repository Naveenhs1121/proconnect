package com.proconnect.proconnect;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EscrowTransactionRepository extends JpaRepository<EscrowTransaction, Long> {
    List<EscrowTransaction> findByBookingId(Long bookingId);
    List<EscrowTransaction> findByProfessionalId(Long professionalId);
}
