package com.example.hotelreservation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotelreservation.adapter.BookingAdapter
import com.example.hotelreservation.api.RetrofitClient
import com.example.hotelreservation.model.Booking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyBookingsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var customerId: Long = 1 // 🔥 TEMP (later dynamic login)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_bookings)

        recyclerView = findViewById(R.id.recyclerBookings)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadBookings()
    }

    private fun loadBookings() {

        RetrofitClient.instance.getBookingsByCustomer(customerId)
            .enqueue(object : Callback<List<Booking>> {

                override fun onResponse(
                    call: Call<List<Booking>>,
                    response: Response<List<Booking>>
                ) {

                    if (response.isSuccessful) {

                        val bookings = response.body() ?: emptyList()

                        recyclerView.adapter =
                            BookingAdapter(bookings.toMutableList())

                    } else {
                        Toast.makeText(
                            this@MyBookingsActivity,
                            "Failed to load bookings",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<Booking>>, t: Throwable) {
                    Toast.makeText(
                        this@MyBookingsActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}