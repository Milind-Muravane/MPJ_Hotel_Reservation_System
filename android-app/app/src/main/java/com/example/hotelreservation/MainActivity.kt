package com.example.hotelreservation

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.hotelreservation.api.RetrofitClient
import com.example.hotelreservation.model.Customer
import retrofit2.*

class MainActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        val btnSignup = findViewById<Button>(R.id.btnSignup)

        btnSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email & password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val customer = Customer(
                name = "",
                email = email,
                password = password,
                phone = ""
            )

            RetrofitClient.instance.loginCustomer(customer)
                .enqueue(object : Callback<Customer> {

                    override fun onResponse(call: Call<Customer>, response: Response<Customer>) {

                        if (response.isSuccessful && response.body() != null) {

                            val user = response.body()!!

                            // ✅ SAVE USER SESSION
                            val session = SessionManager(this@MainActivity)
                            session.saveUser(response.body()!!.id!!)

                            Toast.makeText(this@MainActivity, "Login Successful", Toast.LENGTH_SHORT).show()

                            // 🔥 NEW: GO TO SEARCH SCREEN INSTEAD OF ROOM LIST
                            startActivity(Intent(this@MainActivity, SearchActivity::class.java))
                            finish()

                        } else {
                            Toast.makeText(this@MainActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Customer>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}