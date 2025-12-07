package com.proconnect.proconnect;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Random;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(ProfessionalRepository repo) {
        return args -> {
            // Clear old "foreign" data to apply new Indian context
            if (repo.count() > 0) {
                System.out.println("Cleaning up old data...");
                repo.deleteAll();
            }

            System.out.println("Seeding database with 50+ South Indian professionals...");

            String[] services = {"Plumber", "Electrician", "Carpenter", "Mechanic", "Tutor", "Cleaner", "Painter", "Gardener", "Chef", "Photographer"};
            String[] cities = {"Chennai", "Bangalore", "Hyderabad", "Kochi", "Mysore", "Coimbatore", "Trivandrum", "Madurai", "Visakhapatnam", "Vijayawada"};
            String[] names = {"Ravi", "Suresh", "Ramesh", "Priya", "Lakshmi", "Anita", "Vijay", "Karthik", "Arun", "Meena", "Divya", "Sanjay", "Vikram", "Deepa", "Swati"};
            String[] lastNames = {"Kumar", "Reddy", "Nair", "Iyer", "Rao", "Menon", "Pillai", "Chettiar", "Gowda", "Balaji", "Krishnan", "Subramaniam", "Venkatesh"};

            Random rand = new Random();

            // Create 2 professionals for EACH City + Service combination to ensure even distribution
            // 10 Cities * 10 Services * 2 Pros = 200 Professionals
            for (String city : cities) {
                for (String service : services) {
                    for (int k = 0; k < 2; k++) { // 2 pros per category
                        Professional p = new Professional();
                        String firstName = names[rand.nextInt(names.length)];
                        String lastName = lastNames[rand.nextInt(lastNames.length)];

                        p.setName(firstName + " " + lastName);
                        // Make email unique
                        p.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "." + city.substring(0,3).toLowerCase() + service.substring(0,3).toLowerCase() + k + "@gmail.com");
                        p.setPhone("98" + String.format("%08d", rand.nextInt(100000000)));
                        p.setServiceType(service);
                        p.setCity(city);
                        
                        // New Profile Data
                        p.setRating(3.5 + (rand.nextDouble() * 1.5)); // 3.5 to 5.0
                        p.setExperienceYears(rand.nextInt(15) + 1);
                        p.setReviewsCount(rand.nextInt(100) + 5);
                        p.setDescription("Experienced " + service + " with over " + p.getExperienceYears() + " years of fieldwork. Specializing in residential and commercial projects in " + city + ".");

                        // Set hourly rate (varies by service type)
                        double baseRate = 50.0;
                        if (service.equals("Plumber") || service.equals("Electrician")) {
                            baseRate = 80.0;
                        } else if (service.equals("Tutor") || service.equals("Photographer")) {
                            baseRate = 100.0;
                        } else if (service.equals("Chef")) {
                            baseRate = 120.0;
                        }
                        p.setHourlyRate(baseRate + (rand.nextDouble() * 20)); // Add some variation

                        // Add sample photos
                        p.setPhotos(List.of(
                            "https://placehold.co/600x400?text=Work+Sample+1",
                            "https://placehold.co/600x400?text=Work+Sample+2",
                            "https://placehold.co/600x400?text=Work+Sample+3"
                        ));

                        repo.save(p);
                    }
                }
            }

            System.out.println("Database seeded successfully!");
        };
    }
}
