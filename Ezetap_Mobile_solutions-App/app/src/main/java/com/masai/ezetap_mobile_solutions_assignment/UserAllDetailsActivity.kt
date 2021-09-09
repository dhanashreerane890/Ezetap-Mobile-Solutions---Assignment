package com.masai.ezetap_mobile_solutions_assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_user_all_details.*

class UserAllDetailsActivity : AppCompatActivity() {

    private val dbUser = FirebaseDatabase.getInstance().getReference("User")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_all_details)
        getDataFromIntent()
        btnhome.setOnClickListener {
            val intent = Intent(this, UserDetailsActivity::class.java)
            startActivity(intent)
        }

    }

    // save and get data
    private fun getDataFromIntent() {
        if (intent != null && intent.extras != null) {
            tvUsername.text = "Username : ${intent.getStringExtra("userName")}"
            tvEmail.text = "Email: ${intent.getStringExtra("email")}"
            tvPassword.text = "Passwoed : ${intent.getStringExtra("password")}"
            tvAge.text = "Age : ${intent.getStringExtra("age")+" Years"}"
            tvUsrBirthDate.text = "Birth Date : ${intent.getStringExtra("birthDate")}"
            tvUserLocation.text = "Address: ${intent.getStringExtra("location")}"
            tvAddressLongitude.text = "Longitude: ${intent.getStringExtra("longitude")}"
            tvAddressLatitude.text = "Latitude: ${intent.getStringExtra("latitude")}"

        }
    }


}