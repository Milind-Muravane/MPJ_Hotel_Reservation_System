package com.example.demo.repository;

import com.example.demo.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    
    List<Room> findByType(String type);

    @Query("SELECT SUM(r.available) FROM Room r WHERE r.type = :type")
    Integer getTotalRoomsByType(@Param("type") String type);
}