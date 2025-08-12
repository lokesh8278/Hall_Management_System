package com.hallbooking.config;


import com.hallbooking.hall_service.entity.Amenity;
import com.hallbooking.enums.AmenityType;
import com.hallbooking.hall_service.repository.AmenityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedAmenities(AmenityRepository amenityRepository) {
        return args -> {
            for (AmenityType type : AmenityType.values()) {
                boolean exists = amenityRepository.findByType(type).isPresent();
                if (!exists) {
                    Amenity amenity = new Amenity();
                    amenity.setType(type);
                    amenityRepository.save(amenity);
                    System.out.println("Inserted Amenity: " + type);
                }
            }
        };
    }
}
