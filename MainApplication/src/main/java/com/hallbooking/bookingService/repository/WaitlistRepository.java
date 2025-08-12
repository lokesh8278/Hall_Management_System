package com.hallbooking.bookingService.repository;


import com.hallbooking.bookingService.entity.WaitlistEntity;
import com.hallbooking.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaitlistRepository extends JpaRepository<WaitlistEntity, Long> {
    List<WaitlistEntity> findByHallIdAndRoomIdAndStatus(Long hallId, Long roomId, BookingStatus status);
}

