package com.proconnect.proconnect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professionals")
public class ProfessionalController {

    @Autowired
    private ProfessionalRepository repo;

    // add new professional
    @PostMapping("/register")
    public Professional register(@RequestBody Professional professional) {
        return repo.save(professional);
    }

    // search professionals by city + service
    @GetMapping("/search")
    public List<Professional> search(
            @RequestParam String city,
            @RequestParam String serviceType) {

        return repo.findByServiceTypeContainingIgnoreCaseAndCityContainingIgnoreCase(serviceType, city);
    }

    @GetMapping("/cities")
    public List<String> getCities() {
        return repo.findDistinctCities();
    }

    @GetMapping("/services")
    public List<String> getServices() {
        return repo.findDistinctServiceTypes();
    }

    @GetMapping("/{id}")
    public org.springframework.http.ResponseEntity<Professional> getById(@PathVariable Long id) {
        return repo.findById(id).map(org.springframework.http.ResponseEntity::ok).orElse(org.springframework.http.ResponseEntity.notFound().build());
    }

    @GetMapping("/top-rated")
    public List<Professional> getTopRated() {
        return repo.findTop6ByOrderByRatingDesc();
    }

    // Update profile description
    @PutMapping("/{id}")
    public Professional updateProfile(@PathVariable Long id, @RequestBody Professional updated) {
        return repo.findById(id).map(p -> {
            if (updated.getDescription() != null) p.setDescription(updated.getDescription());
            return repo.save(p);
        }).orElseThrow(() -> new RuntimeException("Professional not found"));
    }

    // Add photo
    @PostMapping("/{id}/photos")
    public Professional addPhoto(@PathVariable Long id, @RequestBody String photoUrl) {
         // Remove quotes if present (raw string body often comes with quotes)
        String cleanUrl = photoUrl.replace("\"", "");
        return repo.findById(id).map(p -> {
            p.getPhotos().add(cleanUrl);
            return repo.save(p);
        }).orElseThrow(() -> new RuntimeException("Professional not found"));
    }

    // Remove photo
    @DeleteMapping("/{id}/photos")
    public Professional removePhoto(@PathVariable Long id, @RequestParam String photoUrl) {
        return repo.findById(id).map(p -> {
            p.getPhotos().remove(photoUrl);
            return repo.save(p);
        }).orElseThrow(() -> new RuntimeException("Professional not found"));
    }
}
