package com.example.hotelreservation.api

import com.example.hotelreservation.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // 🔹 Rooms
    @GET("rooms")
    fun getRooms(): Call<List<Room>>

    @GET("rooms/types")
    fun getRoomTypes(): Call<List<RoomType>>

    @GET("rooms/by-type/{type}")
    fun getRoomByType(
        @Path("type") type: String
    ): Call<Room>

    // ✅ REALTIME AVAILABILITY
    @GET("rooms/availability")
    fun getAvailability(
        @Query("checkIn") checkIn: String,
        @Query("checkOut") checkOut: String
    ): Call<List<RoomType>>

    // 🔹 Booking
    @POST("bookings/create")
    fun createBooking(
        @Query("roomId") roomId: Long,
        @Query("customerId") customerId: Long,
        @Body booking: Map<String, String>
    ): Call<Booking>

    @GET("bookings/customer/{customerId}")
    fun getBookingsByCustomer(
        @Path("customerId") customerId: Long
    ): Call<List<Booking>>

    @PUT("bookings/cancel/{id}")
    fun cancelBooking(
        @Path("id") bookingId: Long
    ): Call<Booking>

    // ✅ ROOMWISE DND (Matches 'bookings' table structure)
    @PUT("bookings/{id}/dnd")
    fun updateBookingDnd(
        @Path("id") id: Long,
        @Query("dnd") dnd: Boolean
    ): Call<Booking>

    // 🔹 Customer
    @POST("customers")
    fun createCustomer(@Body customer: Customer): Call<Customer>

    @POST("customers/login")
    fun loginCustomer(@Body customer: Customer): Call<Customer>

    @GET("customers")
    fun getCustomers(): Call<List<Customer>>

    @PUT("customers/{id}/dnd")
    fun updateCustomerDnd(
        @Path("id") id: Long,
        @Query("dnd") dnd: Boolean
    ): Call<Customer>
}