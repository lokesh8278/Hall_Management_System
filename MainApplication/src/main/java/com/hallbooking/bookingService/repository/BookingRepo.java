package com.hallbooking.bookingService.repository;

import com.hallbooking.bookingService.entity.Bookings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Repository
public interface BookingRepo extends JpaRepository<Bookings,Integer> {

    @Query("SELECT COUNT(b) FROM Bookings b WHERE b.hall.id = :hallId AND " +
            "b.status IN ('PENDING', 'CONFIRMED') AND " +
            "(:checkin < b.checkout AND :checkout > b.checkin)")
    long countConflictingBookings(@Param("hallId") long hallId,
                                  @Param("checkin") Date checkin,
                                  @Param("checkout") Date checkout);

    @Query("SELECT b FROM Bookings b WHERE b.status = :status AND b.createdat < :time")
    List<Bookings> findByStatusAndCreatedatBefore(@Param("status") String status, @Param("time") LocalDateTime time);

    @Query("SELECT b FROM Bookings b JOIN FETCH b.user JOIN FETCH b.hall WHERE b.hall.id = :hallId")
    List<Bookings> findByHallIdWithDetails(@Param("hallId") Long hallId);
    
    @Query("SELECT b FROM Bookings b JOIN FETCH b.user JOIN FETCH b.hall LEFT JOIN FETCH b.room WHERE b.user.id = :userId ORDER BY b.createdat DESC")
    List<Bookings> findByUserIdWithDetails(@Param("userId") Long userId);
    
    @Query("SELECT b FROM Bookings b " +
            "JOIN FETCH b.user " +
            "JOIN FETCH b.hall h " +
            "LEFT JOIN FETCH b.room " +
            "WHERE h.ownerId = :vendorId " +
            "ORDER BY b.createdat DESC")
    List<Bookings> findBookingsByVendorId(@Param("vendorId") Long vendorId);
    
    @Query("SELECT b FROM Bookings b WHERE b.hall.id = :hallId AND b.status IN ('CONFIRMED', 'PENDING') AND " +
           "DATE(b.checkin) = DATE(:date)")
    List<Bookings> findActiveBookingsForHallOnDate(@Param("hallId") Long hallId, @Param("date") Date date);
    
    @Modifying
    @Query("UPDATE Bookings b SET b.status = 'CANCELLED' WHERE b.id = :bookingId")
    void cancelBookingById(@Param("bookingId") Integer bookingId);
}
