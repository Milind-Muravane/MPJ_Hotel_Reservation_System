package com.example.hotelreservation.model

import com.google.gson.annotations.SerializedName

data class Room(

    val id: Long,

    @SerializedName("room_number")
    val roomNumber: Int,

    val type: String,

    val price: Double,

    val available: Int
)