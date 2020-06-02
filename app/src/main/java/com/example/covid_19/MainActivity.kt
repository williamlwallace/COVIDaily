package com.example.covid_19

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.covid_19.ui.home.HomeFragment
import com.example.covid_19.ui.news.NewsFragment
import com.example.covid_19.ui.saved.SavedFragment
import com.example.covid_19.ui.saved.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private val homeFragment: Fragment = HomeFragment()
    private val newsFragment: Fragment = NewsFragment()
    private val settingsFragment: Fragment = SettingsFragment()
    private val savedFragment: Fragment = SavedFragment()
    private val fm: FragmentManager = supportFragmentManager
    var active: Fragment = homeFragment

    lateinit var sharedPreference: SharedPreference
    private var CHANNEL_ID: String = "gang_channel"
    private lateinit var sensorManager: SensorManager
    private var temperature: Sensor? = null

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
        if (!alarmUp && sharedPreference.getValueBoolean("notifications", defaultValue = true)) {
            val pendingIntent =
                PendingIntent.getBroadcast(applicationContext, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager: AlarmManager =
                (getSystemService(Context.ALARM_SERVICE) as AlarmManager)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        }

        //Temperature
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            temperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
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

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, temperature, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val ambient_temp = event?.values?.get(0);
        val tempText: TextView = findViewById(R.id.tempText)
        tempText.text = ambient_temp.toString() + " °C"
    }


}
