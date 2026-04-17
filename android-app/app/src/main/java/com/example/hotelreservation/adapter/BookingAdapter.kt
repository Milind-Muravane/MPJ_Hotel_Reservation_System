package com.example.hotelreservation.adapter

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.hotelreservation.R
import com.example.hotelreservation.api.RetrofitClient
import com.example.hotelreservation.model.Booking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingAdapter(private val bookings: MutableList<Booking>) :
    RecyclerView.Adapter<BookingAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRoomType: TextView = view.findViewById(R.id.tvRoomType)
        val tvDates: TextView = view.findViewById(R.id.tvDates)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val btnCancel: TextView = view.findViewById(R.id.btnCancel)
        val imgRoom: ImageView = view.findViewById(R.id.imgRoom)
        val imgDnd: ImageView = view.findViewById(R.id.imgDnd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val booking = bookings[position]

        holder.tvRoomType.text = "${booking.room.type} Room"
        holder.tvDates.text = "${booking.checkInDate} - ${booking.checkOutDate}"
        holder.tvStatus.text = booking.status
        holder.tvPrice.text = "₹${booking.totalAmount}"

        // 🔥 Status Color & DND Visibility
        if (booking.status == "CANCELLED") {
            holder.tvStatus.setTextColor(Color.RED)
            holder.btnCancel.text = "CANCELLED"
            holder.btnCancel.isEnabled = false
            holder.btnCancel.alpha = 0.5f
            
            // Hide DND for cancelled bookings
            holder.imgDnd.visibility = View.GONE
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"))
            holder.btnCancel.text = "CANCEL"
            holder.btnCancel.isEnabled = true
            holder.btnCancel.alpha = 1.0f
            
            holder.imgDnd.visibility = View.VISIBLE
        }

        // ✅ Image mapping
        when (booking.room.type) {
            "Deluxe" -> holder.imgRoom.setImageResource(R.drawable.deluxe_room)
            "Suite" -> holder.imgRoom.setImageResource(R.drawable.suite_room)
            "Standard" -> holder.imgRoom.setImageResource(R.drawable.standard_room)
        }

        // 🔥 DND STATE UI (Now Room-wise / Booking-specific)
        val isDnd = booking.dnd == true
        if (isDnd) {
            holder.imgDnd.setImageResource(android.R.drawable.ic_lock_silent_mode)
            holder.imgDnd.setColorFilter(Color.RED)
        } else {
            holder.imgDnd.setImageResource(android.R.drawable.ic_lock_silent_mode_off)
            holder.imgDnd.setColorFilter(Color.BLACK)
        }

        // 🔥 DND TOGGLE (Calls BookingController endpoint for this specific booking ID)
        holder.imgDnd.setOnClickListener {
            val newState = !isDnd
            
            RetrofitClient.instance.updateBookingDnd(booking.id!!, newState)
                .enqueue(object : Callback<Booking> {
                    override fun onResponse(call: Call<Booking>, response: Response<Booking>) {
                        if (response.isSuccessful && response.body() != null) {
                            // Update only this specific booking
                            bookings[position] = response.body()!!
                            notifyItemChanged(position)
                            
                            val statusMsg = if (newState) "DND Activated for this room" else "DND Deactivated"
                            Toast.makeText(holder.itemView.context, statusMsg, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(holder.itemView.context, "Error: ${response.code()}. Ensure DND endpoint exists in BookingController!", Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onFailure(call: Call<Booking>, t: Throwable) {
                        Toast.makeText(holder.itemView.context, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        // 🔥 CANCEL BOOKING
        holder.btnCancel.setOnClickListener {

            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Cancel Booking")
                .setMessage("17% deduction will be applied. Continue?")
                .setPositiveButton("Yes") { _, _ ->

                    RetrofitClient.instance.cancelBooking(booking.id!!)
                        .enqueue(object : Callback<Booking> {

                            override fun onResponse(call: Call<Booking>, response: Response<Booking>) {
                                if (response.isSuccessful && response.body() != null) {
                                    bookings[position] = response.body()!!
                                    notifyItemChanged(position)
                                }
                            }

                            override fun onFailure(call: Call<Booking>, t: Throwable) {
                                Toast.makeText(
                                    holder.itemView.context,
                                    "Cancel failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun getItemCount() = bookings.size
}