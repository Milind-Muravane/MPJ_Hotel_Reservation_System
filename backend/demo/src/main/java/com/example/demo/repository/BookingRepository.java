package com.example.demo.repository;import com.example.demo.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // ✅ Find all bookings made by a specific customer
    List<Booking> findByCustomerId(Long customerId);

    /**
     * ✅ UPDATED: Calculates total rooms occupied for a specific type and date range.
     * It sums the 'numRooms' field to account for multiple rooms per booking.
     * If numRooms is NULL (for old records), it treats it as 1.
     */
    @Query("""
        SELECT COALESCE(SUM(CASE WHEN b.numRooms IS NULL THEN 1 ELSE b.numRooms END), 0) 
        FROM Booking b
        WHERE b.room.type = :type
        AND b.status <> 'CANCELLED'
        AND (:checkIn < b.checkOutDate AND :checkOut > b.checkInDate)
    """)
    int countConflictingBookings(
            @Param("type") String type,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );
}