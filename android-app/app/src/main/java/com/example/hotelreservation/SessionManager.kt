package com.example.hotelreservation

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUser(id: Long) {
        prefs.edit().putLong("user_id", id).apply()
    }

    fun getUserId(): Long {
        return prefs.getLong("user_id", -1)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}