package com.example.demo.controller;

import com.example.demo.entity.Booking;
import com.example.demo.service.BookingService;
import org.springframework.web.bind.annotation.*;import java.util.List;

@RestController
@RequestMapping("/bookings")
@CrossOrigin
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @PostMapping("/create")
    public Booking createBooking(
            @RequestParam Long roomId,
            @RequestParam Long customerId,
            @RequestBody Booking booking
    ) {
        return bookingService.createBooking(roomId, customerId, booking);
    }

    @GetMapping("/customer/{customerId}")
    public List<Booking> getBookingsByCustomer(@PathVariable Long customerId) {
        return bookingService.getBookingsByCustomer(customerId);
    }

    @PutMapping("/cancel/{id}")
    public Booking cancelBooking(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }

    @PutMapping("/{id}/dnd")
    public Booking updateBookingDnd(@PathVariable Long id, @RequestParam boolean dnd) {
        return bookingService.updateBookingDnd(id, dnd);
    }
}