package com.proconnect.proconnect;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByProfessionalId(Long professionalId);

    List<Booking> findByProfessionalIdAndDateAndStatusNot(Long professionalId, String date, String status);
}
