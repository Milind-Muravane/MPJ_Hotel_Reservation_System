package com.example.hotelreservation

import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.hotelreservation.api.RetrofitClient
import com.example.hotelreservation.model.Customer

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val cbTerms = findViewById<CheckBox>(R.id.cbTerms)
        val btnSignup = findViewById<TextView>(R.id.btnSignup)

        // ✅ Terms & Conditions Logic
        cbTerms.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                btnSignup.alpha = 1.0f
                btnSignup.isClickable = true
            } else {
                btnSignup.alpha = 0.5f
                btnSignup.isClickable = false
            }
        }

        btnSignup.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val customer = Customer(
                name = name,
                email = email,
                password = password,
                phone = phone
            )

            RetrofitClient.instance.createCustomer(customer)
                .enqueue(object : Callback<Customer> {
                    override fun onResponse(call: Call<Customer>, response: Response<Customer>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@SignupActivity, "Signup Successful! ✅", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@SignupActivity, "Signup Failed. Try again.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Customer>, t: Throwable) {
                        Toast.makeText(this@SignupActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}