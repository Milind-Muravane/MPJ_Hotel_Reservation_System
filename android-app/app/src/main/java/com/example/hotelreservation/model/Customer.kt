package com.example.hotelreservation.model

data class Customer(
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val id: Long? = null,
    val dnd: Boolean? = false
)