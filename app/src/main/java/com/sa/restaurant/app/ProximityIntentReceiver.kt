package com.sa.restaurant.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.sa.restaurant.MainActivity
import com.sa.restaurant.R
import com.sa.restaurant.adapters.GeofenceTransitionsIntentService


class ProximityIntentReceiver : BroadcastReceiver() {

    lateinit var notificationManager: NotificationManager
    lateinit var notificationchannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelid = "com.sa.restaurant"
    private val desc = "test"
    lateinit var notification: Notification

    override fun onReceive(context: Context?, intent: Intent?) {
        val key = LocationManager.KEY_PROXIMITY_ENTERING
        val entering = intent!!.getBooleanExtra(key, false)
        if (entering) {
            sendNotification(context)
        }
    }

    fun sendNotification(context: Context?) {
        Log.i("Notification triggered", "success")
        notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent = Intent(context, MainActivity::class.java)


        val pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + context.packageName + "/raw/plucky")
        notification = NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Yay!! fav restaurant is near")
                .setOngoing(false)
                .setContentText("wanna visit it?")
                .setSound(alarmSound)
                .setContentIntent(pendingIntent).build()



        notificationManager.notify(System.currentTimeMillis().toInt(), notification)



    }

}