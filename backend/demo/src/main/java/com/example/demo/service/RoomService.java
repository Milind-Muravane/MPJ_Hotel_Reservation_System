package com.example.demo.service;

import com.example.demo.dto.RoomTypeDTO;
import com.example.demo.entity.Room;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.BookingRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public RoomService(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // ✅ FIXED: This method now groups rooms by type manually to avoid repository errors
    public List<RoomTypeDTO> getRoomTypes() {
        List<Room> allRooms = roomRepository.findAll();
        Map<String, RoomTypeDTO> typeMap = new HashMap<>();

        for (Room r : allRooms) {
            if (!typeMap.containsKey(r.getType())) {
                typeMap.put(r.getType(), new RoomTypeDTO(r.getType(), r.getPrice(), r.getAvailable()));
            }
        }
        return new ArrayList<>(typeMap.values());
    }

    // ✅ REALTIME DATE-BASED AVAILABILITY
    public List<RoomTypeDTO> getRoomTypesWithAvailability(LocalDate checkIn, LocalDate checkOut) {
        List<Room> allRooms = roomRepository.findAll();
        Map<String, RoomTypeDTO> availabilityMap = new HashMap<>();
        Set<String> processedTypes = new HashSet<>();

        for (Room room : allRooms) {
            String type = room.getType();
            if (processedTypes.contains(type)) continue;
            processedTypes.add(type);

            Integer totalCapacity = roomRepository.getTotalRoomsByType(type);
            if (totalCapacity == null) totalCapacity = 0;

            int occupiedCount = bookingRepository.countConflictingBookings(type, checkIn, checkOut);
            int realAvailability = totalCapacity - occupiedCount;

            availabilityMap.put(type, new RoomTypeDTO(
                    type,
                    room.getPrice(),
                    Math.max(realAvailability, 0)
            ));
        }
        return new ArrayList<>(availabilityMap.values());
    }

    public Room getRoomByType(String type) {
        return roomRepository.findByType(type)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No room found for type: " + type));
    }
}