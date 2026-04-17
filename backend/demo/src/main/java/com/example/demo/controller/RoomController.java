package com.example.demo.controller;

import com.example.demo.entity.Room;
import com.example.demo.service.RoomService;
import com.example.demo.dto.RoomTypeDTO;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/rooms")
@CrossOrigin
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<Room> getRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/types")
    public List<RoomTypeDTO> getRoomTypes() {
        return roomService.getRoomTypes();
    }

    @GetMapping("/by-type/{type}")
    public Room getRoomByType(@PathVariable String type) {
        return roomService.getRoomByType(type);
    }

    @GetMapping("/availability")
    public List<RoomTypeDTO> getAvailability(
            @RequestParam String checkIn,
            @RequestParam String checkOut
    ) {
        return roomService.getRoomTypesWithAvailability(
                LocalDate.parse(checkIn),
                LocalDate.parse(checkOut)
        );
    }
}