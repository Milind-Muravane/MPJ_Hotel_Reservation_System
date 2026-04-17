package com.example.hotelreservation.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.hotelreservation.R
import com.example.hotelreservation.BookingActivity
import com.example.hotelreservation.model.RoomType

class RoomAdapter(
    private val roomTypes: List<RoomType>,
    private val checkIn: String = "",
    private val checkOut: String = "",
    private val adults: Int = 2,
    private val children: Int = 0
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    class RoomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgRoom: ImageView = view.findViewById(R.id.imgRoom)
        val tvRoomType: TextView = view.findViewById(R.id.tvRoomType)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvAvailable: TextView = view.findViewById(R.id.tvAvailable)
        val btnBook: TextView = view.findViewById(R.id.btnBook)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        return RoomViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.room_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = roomTypes[position]
        holder.tvRoomType.text = "${room.type} Room"
        holder.tvPrice.text = "₹${room.price.toInt()}"
        holder.tvAvailable.text = "Available: ${room.availableRooms}"

        when (room.type) {
            "Deluxe" -> holder.imgRoom.setImageResource(R.drawable.deluxe_room)
            "Suite" -> holder.imgRoom.setImageResource(R.drawable.suite_room)
            "Standard" -> holder.imgRoom.setImageResource(R.drawable.standard_room)
        }

        holder.btnBook.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, BookingActivity::class.java).apply {
                putExtra("type", room.type)
                putExtra("price", room.price)
                putExtra("checkIn", checkIn)
                putExtra("checkOut", checkOut)
                putExtra("adults", adults)
                putExtra("children", children)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = roomTypes.size
}