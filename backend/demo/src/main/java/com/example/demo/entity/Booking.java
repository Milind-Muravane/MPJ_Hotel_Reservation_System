package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "bookings")
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    
    private String status;          // BOOKED / CANCELLED
    private Double totalAmount;     
    private Double refundAmount;    
    private Boolean dnd;
    
    private String coupon;          // For "MATS" discount
    private String guestNames;      // To store "Adult 1, Adult 2..."
    private Integer numRooms;       // Number of rooms booked

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
    
    public Booking() {}

    public Booking(LocalDate checkInDate, LocalDate checkOutDate, Customer customer, Room room) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.customer = customer;
        this.room = room;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(Double refundAmount) { this.refundAmount = refundAmount; }

    public Boolean getDnd() { return dnd; }
    public void setDnd(Boolean dnd) { this.dnd = dnd; }

    public String getCoupon() { return coupon; }
    public void setCoupon(String coupon) { this.coupon = coupon; }

    public String getGuestNames() { return guestNames; }
    public void setGuestNames(String guestNames) { this.guestNames = guestNames; }

    public Integer getNumRooms() { return numRooms; }
    public void setNumRooms(Integer numRooms) { this.numRooms = numRooms; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
}