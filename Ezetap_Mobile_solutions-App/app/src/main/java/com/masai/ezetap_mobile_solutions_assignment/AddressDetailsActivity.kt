package com.masai.ezetap_mobile_solutions_assignment


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.firebase.database.FirebaseDatabase
import com.masai.ezetap_mobile_solutions_assignment.dialog.LoadingDialog
import kotlinx.android.synthetic.main.activity_address_details.*
import kotlinx.android.synthetic.main.activity_address_details.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.*

class AddressDetailsActivity : AppCompatActivity() {
    var birthDate: String? = null
    var age: String? = null
    var email: String? = null
    var password: String? = null
    var userName: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var location: String? = null


    lateinit var loadingDialog: LoadingDialog

    //Firebase initialization
    private val dbUser = FirebaseDatabase.getInstance().getReference("User")

    //Declaring the needed Variables
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    val PERMISSION_ID = 1010

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_details)
        getDataFromIntent()

        loadingDialog = LoadingDialog(this)

        btnProceed_Address.setOnClickListener {
            location = textInputLayoutAddress.etAddress.text.toString()
            if (location?.isEmpty() == true) {
                textInputLayoutAddress.etAddress.error = "required"
                return@setOnClickListener
            }
            loadingDialog.startLoadingDialog()

            val user = UserModel(
                "",
                userName,
                email,
                password,
                age,
                birthDate,
                location,
                latitude,
                longitude
            )

            addAuthor(user)
        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        etAddress.setOnClickListener {
            Log.d("Debug:", CheckPermission().toString())
            Log.d("Debug:", isLocationEnabled().toString())
            RequestPermission()
            /* fusedLocationProviderClient.lastLocation.addOnSuccessListener{location: Location? ->
                 textView.text = location?.latitude.toString() + "," + location?.longitude.toString()
             }*/
            getLastLocation()
        }

    }

    private fun getDataFromIntent() {
        if (intent != null && intent.extras != null) {
            userName = intent.getStringExtra("userName")
            email = intent.getStringExtra("email")
            password = intent.getStringExtra("password")
            age = intent.getStringExtra("age")
            birthDate = intent.getStringExtra("birthDate")
        }
    }

    // user added to realtime database
    fun addAuthor(user: UserModel) {
        user.id = dbUser.push().key
        dbUser.child(user.id!!).setValue(user)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    loadingDialog.dismissDialog()
                    Toast.makeText(
                        applicationContext,
                        "User added Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    // passing data to UserAllDetailsActivity
                    val intent = Intent(this, UserAllDetailsActivity::class.java)
                    intent.putExtra("birthDate", birthDate)
                    intent.putExtra("userName", userName)
                    intent.putExtra("password", password)
                    intent.putExtra("email", email)
                    intent.putExtra("age", age)
                    intent.putExtra("location", location)
                    intent.putExtra("latitude", latitude)
                    intent.putExtra("longitude", longitude)
                    startActivity(intent)
                } else {
                    loadingDialog.dismissDialog()
                    Toast.makeText(applicationContext, "User not added", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // fetching location(mapping code)
    fun getLastLocation() {
        if (CheckPermission()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        NewLocationData()
                    } else {
                        Log.d("Debug:", "Your Location:" + location.longitude)
                        longitude = location.longitude.toString()
                        latitude = location.latitude.toString()

                        textInputLayoutAddress.etAddress.setText(
                            "You Address is : Long: " + location.longitude + " , Lat: " + location.latitude + "\n" + getCityName(
                                location.latitude,
                                location.longitude
                            )
                        )
                    }
                }
            } else {
                Toast.makeText(this, "Please Turn on Your device Location", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            RequestPermission()
        }
    }


    fun NewLocationData() {
        var locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            Log.d("Debug:", "your last last location: " + lastLocation.longitude.toString())
            textInputLayoutAddress.etAddress.setText("You Last Location is : Long: " + lastLocation.longitude.toString() + " , Lat: " + lastLocation.latitude.toString())
        }
    }


    // Location permission for fetch location
    private fun CheckPermission(): Boolean {
        //this function will return a boolean
        //true: if we have permission
        //false if not
        if (
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }


    fun RequestPermission() {
        //this function will allows us to tell the user to requesut the necessary permsiion if they are not garented
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    fun isLocationEnabled(): Boolean {
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Debug:", "You have the Permission")
            }
        }
    }


    // fetch city name
    private fun getCityName(lat: Double, long: Double): String {
        var cityName: String = ""
        var countryName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress = geoCoder.getFromLocation(lat, long, 3)

        cityName = Adress.get(0).locality
        countryName = Adress.get(0).countryName
        Log.d("Debug:", "Your City: " + cityName + " ; your Country " + countryName)
        return cityName
    }
}




