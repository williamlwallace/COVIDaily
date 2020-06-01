package com.example.covid_19

import android.R
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class AlarmReceiver : BroadcastReceiver() {
    private var CHANNEL_ID: String = "gang_channel"
    private var MID: Int = 69

    override fun onReceive(context: Context, intent: Intent?) {
        val `when` = System.currentTimeMillis()
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(
            context, 0,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.sym_def_app_icon)
            .setContentTitle("Daily cases update")
            .setContentText("In the past 24 hours there are 0 new cases").setSound(alarmSound)
            .setAutoCancel(true).setWhen(`when`)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(MID, builder.build())
    }
}