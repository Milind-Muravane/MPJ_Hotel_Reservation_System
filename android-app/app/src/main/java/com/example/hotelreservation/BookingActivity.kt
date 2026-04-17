package com.example.hotelreservation

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.hotelreservation.api.RetrofitClient
import com.example.hotelreservation.model.Booking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingActivity : AppCompatActivity() {

    private var checkInDate = ""
    private var checkOutDate = ""
    private var pricePerNight = 0.0
    private var isCouponApplied = false
    private var numRooms = 1
    private val guestNameFields = mutableListOf<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        // UI References
        val tvRoomType = findViewById<TextView>(R.id.tvRoomType)
        val tvPrice = findViewById<TextView>(R.id.tvPrice)
        val tvSelectedDates = findViewById<TextView>(R.id.tvSelectedDates)
        val guestContainer = findViewById<LinearLayout>(R.id.guestContainer)
        val btnConfirm = findViewById<TextView>(R.id.btnConfirm)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val imgRoom = findViewById<ImageView>(R.id.imgRoom)
        val etCoupon = findViewById<EditText>(R.id.etCoupon)
        val btnApply = findViewById<TextView>(R.id.btnApply)

        // ✅ Get Search Data from Intent
        val type = intent.getStringExtra("type") ?: ""
        pricePerNight = intent.getDoubleExtra("price", 0.0)
        checkInDate = intent.getStringExtra("checkIn") ?: ""
        checkOutDate = intent.getStringExtra("checkOut") ?: ""
        val adults = intent.getIntExtra("adults", 2)
        val children = intent.getIntExtra("children", 0)

        // ✅ Occupancy Logic: Max 2 Adults and 1 Child per room
        val roomsByAdults = (adults + 1) / 2
        val roomsByChildren = children
        numRooms = Math.max(roomsByAdults, roomsByChildren)

        val session = SessionManager(this)
        val customerId = session.getUserId()

        tvRoomType.text = "$type Room ($numRooms Rooms)"
        tvPrice.text = "₹${(pricePerNight * numRooms).toInt()}"
        tvSelectedDates.text = "$checkInDate to $checkOutDate"

        when (type) {
            "Deluxe" -> imgRoom.setImageResource(R.drawable.deluxe_room)
            "Suite" -> imgRoom.setImageResource(R.drawable.suite_room)
            "Standard" -> imgRoom.setImageResource(R.drawable.standard_room)
        }

        btnBack.setOnClickListener { finish() }

        // ✅ DYNAMICALLY ADD GUEST NAME PEBBLES (Improved Heights & Padding)
        val density = resources.displayMetrics.density
        for (i in 1..adults) { 
            addGuestNameInput("Adult $i Name", guestContainer, density) 
        }
        for (i in 1..children) { 
            addGuestNameInput("Child $i Name", guestContainer, density) 
        }

        // ✅ Coupon Logic
        btnApply.setOnClickListener {
            val code = etCoupon.text.toString().trim()
            if (code == "MATS" && !isCouponApplied) {
                val currentTotal = pricePerNight * numRooms
                val discountedPrice = currentTotal * 0.95
                tvPrice.text = "₹${discountedPrice.toInt()}"
                isCouponApplied = true
                btnApply.text = "Applied"
                btnApply.isEnabled = false
                btnApply.alpha = 0.5f
                Toast.makeText(this, "5% Discount Applied! 🎟️", Toast.LENGTH_SHORT).show()
            } else if (code != "MATS") {
                Toast.makeText(this, "Invalid Coupon Code", Toast.LENGTH_SHORT).show()
            }
        }

        btnConfirm.setOnClickListener {
            val names = guestNameFields.joinToString(", ") { it.text.toString() }
            if (guestNameFields.any { it.text.isNullOrBlank() }) {
                Toast.makeText(this, "Please enter all guest names", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            createBooking(type, customerId, names)
        }
    }

    private fun addGuestNameInput(hint: String, container: LinearLayout, density: Float) {
        val editText = EditText(this)
        val heightInPx = (50 * density).toInt() 
        
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, heightInPx)
        params.setMargins(0, 0, 0, (12 * density).toInt())
        
        editText.layoutParams = params
        editText.hint = hint
        editText.setHintTextColor(resources.getColor(android.R.color.darker_gray))
        editText.setTextColor(resources.getColor(android.R.color.black))
        editText.textSize = 14f
        editText.setPadding((20 * density).toInt(), 0, (20 * density).toInt(), 0)
        editText.setBackgroundResource(R.drawable.glass_input) 
        editText.isSingleLine = true

        container.addView(editText)
        guestNameFields.add(editText)
    }

    private fun createBooking(type: String, customerId: Long, names: String) {
        val bookingData = mutableMapOf(
            "checkInDate" to checkInDate,
            "checkOutDate" to checkOutDate,
            "guestNames" to names,
            "numRooms" to numRooms.toString()
        )
        if (isCouponApplied) bookingData["coupon"] = "MATS"

        RetrofitClient.instance.getRoomByType(type).enqueue(object : Callback<com.example.hotelreservation.model.Room> {
            override fun onResponse(call: Call<com.example.hotelreservation.model.Room>, response: Response<com.example.hotelreservation.model.Room>) {
                val roomId = response.body()?.id ?: return
                RetrofitClient.instance.createBooking(roomId, customerId, bookingData).enqueue(object : Callback<Booking> {
                    override fun onResponse(call: Call<Booking>, response: Response<Booking>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@BookingActivity, "Booking Confirmed! ✅", Toast.LENGTH_LONG).show()
                            finish()
                        }
                    }
                    override fun onFailure(call: Call<Booking>, t: Throwable) {}
                })
            }
            override fun onFailure(call: Call<com.example.hotelreservation.model.Room>, t: Throwable) {}
        })
    }
}