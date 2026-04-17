package com.example.demo.service;

import com.example.demo.entity.*;import com.example.demo.repository.*;
import com.example.demo.exception.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final CustomerRepository customerRepository;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          CustomerRepository customerRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.customerRepository = customerRepository;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // ✅ UPDATED: Handles Multiple Rooms & Guest Names
    @Transactional
    public synchronized Booking createBooking(Long roomId, Long customerId, Booking booking) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        String type = room.getType();
        int totalRoomsInHotel = roomRepository.getTotalRoomsByType(type);
        
        // Check how many rooms of this type are already booked for these dates
        int bookedRooms = bookingRepository.countConflictingBookings(
                type,
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );

        // Get the number of rooms requested from the app (defaults to 1 if null)
        int requestedRooms = (booking.getNumRooms() != null) ? booking.getNumRooms() : 1;

        // 🔥 VALIDATION: Check if enough rooms are available
        if (bookedRooms + requestedRooms > totalRoomsInHotel) {
            throw new BadRequestException("Not enough rooms available for these dates");
        }

        // ✅ UPDATE ROOM INVENTORY (Subtract the requested number of rooms)
        room.setAvailable(room.getAvailable() - requestedRooms);
        roomRepository.save(room);

        // Set mandatory fields
        booking.setRoom(room);
        booking.setCustomer(customer);
        booking.setStatus("BOOKED");

        // ✅ CALCULATE STAY DURATION
        long days = ChronoUnit.DAYS.between(
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );
        if (days <= 0) days = 1;

        // 🔥 CALCULATE TOTAL AMOUNT (Price * Days * Number of Rooms)
        double total = days * room.getPrice() * requestedRooms;
        
        // Apply 5% Discount if coupon is MATS
        if ("MATS".equals(booking.getCoupon())) {
            total = total * 0.95; 
            System.out.println("MATS Coupon Applied! Final Price: " + total);
        }

        booking.setTotalAmount(total);

        // Save to database
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByCustomer(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    // ✅ UPDATED: Restores correct number of rooms on cancellation
    @Transactional
    public Booking cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new BadRequestException("Already cancelled");
        }

        // 🔥 RESTORE ROOMS based on numRooms field
        Room room = booking.getRoom();
        if (room != null) {
            int roomsToRestore = (booking.getNumRooms() != null) ? booking.getNumRooms() : 1;
            room.setAvailable(room.getAvailable() + roomsToRestore);
            roomRepository.save(room);
        }

        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }
    
    public Booking updateBookingDnd(Long id, boolean dnd) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        booking.setDnd(dnd);
        return bookingRepository.save(booking);
    }
}