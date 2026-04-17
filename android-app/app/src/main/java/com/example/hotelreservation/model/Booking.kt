package com.example.hotelreservation.model

data class Booking(
    val id: Long?,
    val checkInDate: String,
    val checkOutDate: String,

    val status: String?,          // 🔥 ADD
    val totalAmount: Double?,     // 🔥 ADD
    val refundAmount: Double?,    // 🔥 ADD

    val customer: Customer,
    val room: Room,
    val dnd: Boolean?
)