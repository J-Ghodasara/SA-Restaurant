package com.sa.restaurant.adapters

import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.Geofence.*
import com.google.android.gms.location.GeofencingEvent
import com.sa.restaurant.MainActivity
import com.sa.restaurant.R


class GeofenceTransitionsIntentService : IntentService(null) {


    lateinit var notificationManager: NotificationManager
    lateinit var notificationchannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelid = "com.sa.restaurant"
    private val desc = "test"
    lateinit var notification: Notification
    override fun onHandleIntent(intent: Intent?) {

        Log.i("Geofence Service", "Triggered")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {

            Log.e("GeoIntentService", "Error")
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == GEOFENCE_TRANSITION_ENTER || geofenceTransition == GEOFENCE_TRANSITION_EXIT || geofenceTransition == GEOFENCE_TRANSITION_DWELL) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // Get the transition details as a String.
            val geofenceTransitionDetails = getGeofenceTransitionDetails()
            geofenceTransitionDetails.context = this
            geofenceTransitionDetails.geofenceTransition = geofenceTransition
            geofenceTransitionDetails.list = triggeringGeofences
            Log.i("trigger detected", "success")
            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails)
            Log.i("GeoIntentService", geofenceTransitionDetails.toString())
        } else {
            // Log the error.
            Log.e("GeoIntentService", "Transition Invalid Type")
        }
    }

    fun sendNotification(geofenceTransitionDetails: getGeofenceTransitionDetails) {
        Log.i("Notification triggered", "success")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent = Intent(this, MainActivity::class.java)
        var geofenceName = geofenceTransitionDetails.list!![0].requestId

        val pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + packageName + "/raw/plucky")
        notification = NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Yay!! $geofenceName. is near")
                .setOngoing(false)
                .setContentText("wanna visit it?")
                .setSound(alarmSound)
                .setContentIntent(pendingIntent).build()



        notificationManager.notify(System.currentTimeMillis().toInt(), notification)



    }

    class getGeofenceTransitionDetails() {
        var context: Context? = null
        var geofenceTransition: Int? = null
        var list: List<Geofence>? = null
    }
}