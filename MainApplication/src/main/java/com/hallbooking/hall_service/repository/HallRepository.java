package com.hallbooking.hall_service.repository;

import com.hallbooking.hall_service.entity.Hall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HallRepository extends JpaRepository<Hall, Long> {

    @Query(value = "SELECT * FROM halls WHERE ST_DWithin(location, ST_MakePoint(:lng, :lat)::geography, :radius) AND is_active = true", nativeQuery = true)
    List<Hall> findHallsNearby(@Param("lat") double lat, @Param("lng") double lng, @Param("radius") double radius);

    // ---- ONE collection per method (no multiple join fetches) ----

    @Query("""
        SELECT h FROM Hall h
        LEFT JOIN FETCH h.documents d
        WHERE h.id = :id AND h.isActive = true
    """)
    Optional<Hall> findByIdWithDocumentsOnly(@Param("id") Long id);

    @Query("""
        SELECT h FROM Hall h
        LEFT JOIN FETCH h.rooms r
        WHERE h.id = :id AND h.isActive = true
    """)
    Optional<Hall> findByIdWithRooms(@Param("id") Long id);

    @Query("""
        SELECT h FROM Hall h
        LEFT JOIN FETCH h.amenities a
        WHERE h.id = :id AND h.isActive = true
    """)
    Optional<Hall> findByIdWithAmenities(@Param("id") Long id);

    @Query("""
        SELECT h FROM Hall h
        LEFT JOIN FETCH h.images i
        WHERE h.id = :id AND h.isActive = true
    """)
    Optional<Hall> findByIdWithImages(@Param("id") Long id);

    // Basic list (no collections)
    @Query("SELECT h FROM Hall h WHERE h.isActive = true ORDER BY h.id")
    Page<Hall> findAllActiveHalls(Pageable pageable);

    @Query("SELECT h FROM Hall h WHERE h.ownerId = :ownerId AND h.isActive = true")
    List<Hall> findByOwnerIdAndActive(@Param("ownerId") Long ownerId);

    // If you prefer EntityGraphs, keep ONE collection per method
    @EntityGraph(attributePaths = {"rooms"})
    @Query("SELECT h FROM Hall h WHERE h.id = :id AND h.isActive = true")
    Optional<Hall> findByIdWithRoomsGraph(@Param("id") Long id);

    @EntityGraph(attributePaths = {"amenities"})
    @Query("SELECT h FROM Hall h WHERE h.id = :id AND h.isActive = true")
    Optional<Hall> findByIdWithAmenitiesGraph(@Param("id") Long id);
}
