package com.masai.ezetap_mobile_solutions_assignment


import android.content.Intent

import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.DatePickerDialog
import android.view.View
import android.widget.Toast
import java.util.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*


class UserDetailsActivity : AppCompatActivity() {

    var birthDate: String? = null
    var age: String? = null

    lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnProceed.setOnClickListener {
            val userName = textInputLayoutUsername.etUsername.text.toString()
            val password = textInputLayoutPassword.etPassword.text.toString()
            val email = textInputLayoutEmail.etEmail.text.toString()
           // validation
            if (userName.isEmpty()) {
                textInputLayoutUsername.etUsername.error = "required"
                return@setOnClickListener

            }
            if (email.isEmpty()) {
                textInputLayoutEmail.etEmail.error = "required"
                return@setOnClickListener

            }
            if (password.isEmpty()) {

                textInputLayoutPassword.etPassword.error = "required"

                if (password.length < 6) {
                    textInputLayoutPassword.etPassword.error = "minimum length is 6"
                    return@setOnClickListener
                }
                return@setOnClickListener

            }
            if (birthDate?.isEmpty() == true) {
                textInputLayoutChooseDate.etChooseDate.error = "required"
                return@setOnClickListener
            }
            val intent = Intent(this, AddressDetailsActivity::class.java)
            intent.putExtra("birthDate", birthDate)
            intent.putExtra("userName", userName)
            intent.putExtra("password", password)
            intent.putExtra("email", email)
            intent.putExtra("age", age)
            startActivity(intent)
        }
        etChooseDate.setOnClickListener {
            selectDate(it)
        }
    }

    // date picker code from edit text
    fun selectDate(view: View) {
        var calendar = Calendar.getInstance()
        var cDay = calendar.get(Calendar.DAY_OF_MONTH)
        var cMonth = calendar.get(Calendar.MONTH)
        var cYear = calendar.get(Calendar.YEAR)
        //set CalendarDialog
        val calendarDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                cDay = dayOfMonth
                cMonth = month
                cYear = year

                val currentYear = Calendar.getInstance()
                    .get(Calendar.YEAR)
                if (cYear > currentYear) {
                    textMassge("Date is not valid")
                } else {
                    val age = currentYear - cYear
                    this.age = age.toString()
                }

                birthDate = "$cDay/${cMonth + 1}/$cYear"
                textInputLayoutChooseDate.etChooseDate.setText("$cDay/${cMonth + 1}/$cYear")
            }, cYear, cMonth, cDay
        )
        calendarDialog.show()

    }

    private fun textMassge(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }
}






