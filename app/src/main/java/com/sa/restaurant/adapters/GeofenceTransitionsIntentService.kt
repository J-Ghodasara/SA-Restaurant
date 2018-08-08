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

/*
* created by:- jay.ghodasara
* created on:- 23 july 18
* This class is a service that is used to trigger notification on entering Geofence
*/


class GeofenceTransitionsIntentService : IntentService(null) {


    lateinit var notificationManager: NotificationManager
    lateinit var notificationchannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelid = "com.sa.restaurant"
    private val desc = "test"
    lateinit var notification: Notification
    override fun onHandleIntent(intent: Intent?) {


        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {


            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition


        if (geofenceTransition == GEOFENCE_TRANSITION_ENTER) {

            // Get the geofences that were triggered. A single event can trigger

            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // Get the transition details as a String.
            val geofenceTransitionDetails = GetGeofenceTransitionDetails()
            geofenceTransitionDetails.context = this
            geofenceTransitionDetails.geofenceTransition = geofenceTransition
            geofenceTransitionDetails.list = triggeringGeofences

            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails)

        } else {


        }
    }

    fun sendNotification(geofenceTransitionDetails: GetGeofenceTransitionDetails) {

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

    class GetGeofenceTransitionDetails() {
        var context: Context? = null
        var geofenceTransition: Int? = null
        var list: List<Geofence>? = null
    }
}