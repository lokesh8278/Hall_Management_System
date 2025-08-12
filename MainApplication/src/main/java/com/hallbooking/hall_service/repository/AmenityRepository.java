package com.hallbooking.hall_service.repository;

import com.hallbooking.hall_service.entity.Amenity;
import com.hallbooking.enums.AmenityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long>  {
    Optional<Amenity> findByType(AmenityType type);
}
