package com.example.demo.dto;

public class RoomTypeDTO {

    private String type;
    private double price;
    private long availableRooms;

    public RoomTypeDTO(String type, double price, long availableRooms) {
        this.type = type;
        this.price = price;
        this.availableRooms = availableRooms;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public long getAvailableRooms() {
        return availableRooms;
    }
}