package com.proconnect.proconnect;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProfessionalRepository extends JpaRepository<Professional, Long> {

    // find by city and service type
    // find by city and service type (case-insensitive partial match)
    List<Professional> findByServiceTypeContainingIgnoreCaseAndCityContainingIgnoreCase(String serviceType, String city);

    // find by email
    Professional findByEmail(String email);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT p.city FROM Professional p")
    List<String> findDistinctCities();

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT p.serviceType FROM Professional p")
    List<String> findDistinctServiceTypes();

    List<Professional> findTop6ByOrderByRatingDesc();
}
