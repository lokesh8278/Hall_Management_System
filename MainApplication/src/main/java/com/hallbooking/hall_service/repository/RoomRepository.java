package com.hallbooking.hall_service.repository;

import com.hallbooking.hall_service.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHall_Id(Long hallId);

}


