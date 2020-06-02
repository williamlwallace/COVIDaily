package com.example.covid_19

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.covid_19.ui.home.HomeFragment
import com.example.covid_19.ui.news.NewsFragment
import com.example.covid_19.ui.saved.SavedFragment
import com.example.covid_19.ui.saved.SettingsFragment
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val homeFragment: Fragment = HomeFragment()
    private val newsFragment: Fragment = NewsFragment()
    private val settingsFragment: Fragment = SettingsFragment()
    private val savedFragment: Fragment = SavedFragment()
    private val fm: FragmentManager = supportFragmentManager
    var active: Fragment = homeFragment

    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var sharedPreference: SharedPreference
    private var CHANNEL_ID: String = "gang_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.setElevation(0F);

        fm.beginTransaction().add(R.id.myFragment, settingsFragment, "4").hide(settingsFragment)
            .commit()
        fm.beginTransaction().add(R.id.myFragment, savedFragment, "3").hide(savedFragment)
            .commit()
        fm.beginTransaction().add(R.id.myFragment, newsFragment, "2").hide(newsFragment)
            .commit()
        fm.beginTransaction().add(R.id.myFragment, homeFragment, "1").commit()

        nav_view.setOnNavigationItemSelectedListener {item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    fm.beginTransaction().hide(active).show(homeFragment).commit();
                    active = homeFragment
                }
                R.id.navigation_news -> {
                    fm.beginTransaction().hide(active).show(newsFragment).commit();
                    active = newsFragment
                }
                R.id.navigation_statistics -> {
                    fm.beginTransaction().hide(active).show(savedFragment).commit();
                    active = savedFragment
                }
                R.id.navigation_settings -> {
                    fm.beginTransaction().hide(active).show(settingsFragment).commit();
                    active = settingsFragment
                }
            }
            true
        }

        sharedPreference = SharedPreference(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        createNotificationChannel()

        // Daily notifications
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = 9
        calendar[Calendar.MINUTE] = 30
        calendar[Calendar.SECOND] = 0
        val intent1 = Intent(applicationContext, AlarmReceiver::class.java)
        intent1.putExtra("DAILY_NOTIFICATION", sharedPreference.getValueString("DAILY_NOTIFICATION"))
        val alarmUp =
            (PendingIntent.getBroadcast(applicationContext, 0, intent1, PendingIntent.FLAG_NO_CREATE)) != null
        if (!alarmUp) {
            val pendingIntent =
                PendingIntent.getBroadcast(applicationContext, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager: AlarmManager =
                (getSystemService(Context.ALARM_SERVICE) as AlarmManager)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        }

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    //Unique value
    private val PERMISSION_ID = 42

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Granted. Start getting the location information
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        getCountry(location)
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            getCountry(mLastLocation)
        }
    }

    private fun getCountry(location: Location) {
        val gcd = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> =
            gcd.getFromLocation(location.latitude, location.longitude, 1)

        if (addresses.isNotEmpty()) {
            val countryFlag: String = addresses[0].countryCode.toFlagEmoji()
            val country: String = addresses[0].getCountryName()
            val countryISO: String = addresses[0].countryCode // Country ISO code for newsapi.org
            sharedPreference.save("COUNTRY", country)
            sharedPreference.save("COUNTRY_ISO", countryISO)
            sharedPreference.save("COUNTRY_KEBAB_CASE", country.toKebabCase()) // Kebab case version of country name needed for Covid API
            sharedPreference.save("COUNTRY_FLAG", countryFlag)
        }

    }

    //Function to get flag emoji from country code
    private fun String.toFlagEmoji(): String {
        // 1. It first checks if the string consists of only 2 characters: ISO 3166-1 alpha-2 two-letter country codes (https://en.wikipedia.org/wiki/Regional_Indicator_Symbol).
        if (this.length != 2) {
            return this
        }

        val countryCodeCaps = this.toUpperCase() // upper case is important because we are calculating offset
        val firstLetter = Character.codePointAt(countryCodeCaps, 0) - 0x41 + 0x1F1E6
        val secondLetter = Character.codePointAt(countryCodeCaps, 1) - 0x41 + 0x1F1E6

        // 2. It then checks if both characters are alphabet
        if (!countryCodeCaps[0].isLetter() || !countryCodeCaps[1].isLetter()) {
            return this
        }

        return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    }

    private fun String.toKebabCase(): String {
        return this.toLowerCase().replace(" ", "-")
    }

}
