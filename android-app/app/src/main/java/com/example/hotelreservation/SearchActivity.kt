package com.example.hotelreservation

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class SearchActivity : AppCompatActivity() {

    private var checkInDate = ""
    private var checkOutDate = ""
    private var isSelectingCheckIn = true
    private var adults = 2
    private var children = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val pebbleCalendar = findViewById<LinearLayout>(R.id.pebbleCalendar)
        val tvSelectedDates = findViewById<TextView>(R.id.tvSelectedDates)
        val calendarSection = findViewById<LinearLayout>(R.id.calendarSection)
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val tvDatePrompt = findViewById<TextView>(R.id.tvDatePrompt)
        val btnSearch = findViewById<Button>(R.id.btnSearch)
        val btnMyBookingsPebble = findViewById<TextView>(R.id.btnMyBookingsPebble)
        
        val pebbleAdults = findViewById<LinearLayout>(R.id.pebbleAdults)
        val pebbleChildren = findViewById<LinearLayout>(R.id.pebbleChildren)
        val tvAdults = findViewById<TextView>(R.id.tvAdults)
        val tvChildren = findViewById<TextView>(R.id.tvChildren)

        // ✅ Toggle Calendar
        pebbleCalendar.setOnClickListener {
            calendarSection.visibility = if (calendarSection.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        // ✅ Calendar Logic
        calendarView.minDate = System.currentTimeMillis()
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = sdf.format(calendar.time)

            if (isSelectingCheckIn) {
                checkInDate = formattedDate
                isSelectingCheckIn = false
                tvDatePrompt.text = "Please select check-out date"
                tvSelectedDates.text = "In: $checkInDate"
            } else {
                if (formattedDate <= checkInDate) {
                    Toast.makeText(this, "Check-out must be after check-in", Toast.LENGTH_SHORT).show()
                } else {
                    checkOutDate = formattedDate
                    tvDatePrompt.text = "Dates selected ✅"
                    tvSelectedDates.text = "$checkInDate - $checkOutDate"
                    calendarSection.visibility = View.GONE
                    isSelectingCheckIn = true 
                }
            }
        }

        // ✅ Adult Selection
        pebbleAdults.setOnClickListener {
            showGuestPicker("Select Adults", 1, 10, adults) { selected ->
                if (selected > 2) {
                    Toast.makeText(this, "Note: Only 2 adults are allowed in 1 room. More rooms will be booked automatically.", Toast.LENGTH_LONG).show()
                }
                adults = selected
                tvAdults.text = "Adults: $adults"
            }
        }

        // ✅ Children Selection
        pebbleChildren.setOnClickListener {
            showGuestPicker("Select Children", 0, 10, children) { selected ->
                children = selected
                tvChildren.text = "Children: $children"
            }
        }

        // ✅ Search Action
        btnSearch.setOnClickListener {
            if (checkInDate.isEmpty() || checkOutDate.isEmpty()) {
                Toast.makeText(this, "Please select dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, CustomerActivity::class.java)
            intent.putExtra("checkIn", checkInDate)
            intent.putExtra("checkOut", checkOutDate)
            intent.putExtra("adults", adults)
            intent.putExtra("children", children)
            startActivity(intent)
        }

        // ✅ My Bookings Navigation
        btnMyBookingsPebble.setOnClickListener {
            val intent = Intent(this, CustomerActivity::class.java)
            intent.putExtra("showBookings", true)
            startActivity(intent)
        }
    }

    private fun showGuestPicker(title: String, min: Int, max: Int, current: Int, onSelected: (Int) -> Unit) {
        val picker = NumberPicker(this).apply {
            minValue = min
            maxValue = max
            value = current
        }
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(picker)
            .setPositiveButton("OK") { _, _ -> onSelected(picker.value) }
            .setNegativeButton("Cancel", null)
            .show()
    }
}