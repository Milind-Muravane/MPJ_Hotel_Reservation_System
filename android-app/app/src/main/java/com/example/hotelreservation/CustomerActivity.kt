package com.example.hotelreservation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotelreservation.adapter.RoomAdapter
import com.example.hotelreservation.adapter.BookingAdapter
import com.example.hotelreservation.api.RetrofitClient
import com.example.hotelreservation.model.RoomType
import com.example.hotelreservation.model.Booking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CustomerActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var rvBookings: RecyclerView
    private lateinit var bookingsOverlay: RelativeLayout
    private lateinit var tvGreeting: TextView
    private lateinit var tvSearchDetails: TextView
    
    private var checkIn = ""
    private var checkOut = ""
    private var adults = 2
    private var children = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        recyclerView = findViewById(R.id.recyclerView)
        rvBookings = findViewById(R.id.rvBookings)
        bookingsOverlay = findViewById(R.id.bookingsOverlay)
        tvGreeting = findViewById(R.id.tvGreeting)
        tvSearchDetails = findViewById(R.id.tvSearchDetails)
        
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnMyBookingsTop = findViewById<TextView>(R.id.btnMyBookingsTop)
        val btnBackBookings = findViewById<ImageView>(R.id.btnBackBookings)

        recyclerView.layoutManager = LinearLayoutManager(this)
        rvBookings.layoutManager = LinearLayoutManager(this)

        // ✅ Get Search Data
        checkIn = intent.getStringExtra("checkIn") ?: ""
        checkOut = intent.getStringExtra("checkOut") ?: ""
        adults = intent.getIntExtra("adults", 2)
        children = intent.getIntExtra("children", 0)
        
        if (checkIn.isNotEmpty()) {
            tvSearchDetails.text = "$checkIn to $checkOut | $adults Adults, $children Kids"
        }

        // ✅ Check if we should show bookings immediately
        if (intent.getBooleanExtra("showBookings", false)) {
            showBookings()
        }

        updateGreeting()

        btnBack.setOnClickListener {
            finish() 
        }

        btnMyBookingsTop.setOnClickListener {
            showBookings()
        }

        btnBackBookings.setOnClickListener {
            bookingsOverlay.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh room list whenever we return to this screen to ensure availability is updated
        if (checkIn.isNotEmpty()) {
            fetchRoomTypes()
        }
        // If bookings overlay is visible, refresh it too
        if (bookingsOverlay.visibility == View.VISIBLE) {
            fetchBookings()
        }
    }

    private fun showBookings() {
        bookingsOverlay.visibility = View.VISIBLE
        fetchBookings()
    }

    private fun updateGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        tvGreeting.text = when (hour) {
            in 0..11 -> "Good morning 👋"
            in 12..16 -> "Good afternoon ☀️"
            in 17..20 -> "Good evening 🌆"
            else -> "Good night 🌙"
        }
    }

    private fun fetchRoomTypes() {
        RetrofitClient.instance.getAvailability(checkIn, checkOut)
            .enqueue(object : Callback<List<RoomType>> {
                override fun onResponse(call: Call<List<RoomType>>, response: Response<List<RoomType>>) {
                    if (response.isSuccessful && response.body() != null) {
                        recyclerView.adapter = RoomAdapter(
                            response.body()!!,
                            checkIn,
                            checkOut,
                            adults,
                            children
                        )
                    }
                }
                override fun onFailure(call: Call<List<RoomType>>, t: Throwable) {
                    Toast.makeText(this@CustomerActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun fetchBookings() {
        val customerId = SessionManager(this).getUserId()
        RetrofitClient.instance.getBookingsByCustomer(customerId)
            .enqueue(object : Callback<List<Booking>> {
                override fun onResponse(call: Call<List<Booking>>, response: Response<List<Booking>>) {
                    if (response.isSuccessful && response.body() != null) {
                        rvBookings.adapter = BookingAdapter(response.body()!!.toMutableList())
                    }
                }
                override fun onFailure(call: Call<List<Booking>>, t: Throwable) {
                    Toast.makeText(this@CustomerActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onBackPressed() {
        if (bookingsOverlay.visibility == View.VISIBLE) {
            bookingsOverlay.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }
}