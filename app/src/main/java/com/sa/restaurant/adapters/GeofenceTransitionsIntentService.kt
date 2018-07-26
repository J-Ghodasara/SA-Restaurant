package com.sa.restaurant.adapters

import android.app.*
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.RemoteViews
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT
import com.google.android.gms.location.GeofencingEvent
import com.sa.restaurant.MainActivity
import com.sa.restaurant.R
import android.content.ContentResolver
import android.net.Uri


class GeofenceTransitionsIntentService : IntentService(null) {
    // ...

    lateinit var notificationManager: NotificationManager
    lateinit var notificationchannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelid = "com.sa.restaurant"
    private val desc = "test"
    lateinit var notification: Notification
    override fun onHandleIntent(intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {

            Log.e("GeoIntentService", "Error")
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == GEOFENCE_TRANSITION_ENTER || geofenceTransition == GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // Get the transition details as a String.
            val geofenceTransitionDetails = getGeofenceTransitionDetails()
            geofenceTransitionDetails.context=this
            geofenceTransitionDetails.geofenceTransition=geofenceTransition
            geofenceTransitionDetails.list= triggeringGeofences

            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails)
            Log.i("GeoIntentService", geofenceTransitionDetails.toString())
        } else {
            // Log the error.
            Log.e("GeoIntentService", "Transition Invalid Type")
        }
    }

    fun sendNotification(geofenceTransitionDetails: getGeofenceTransitionDetails) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent = Intent(this, MainActivity::class.java)
       var geofenceName= geofenceTransitionDetails.list!![0].requestId

        val pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
      //  var remoteView: RemoteViews = RemoteViews(this.packageName, R.layout.custom_notification)

        val alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + packageName + "/raw/plucky")
        notification = NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Yay!! $geofenceName. is near, wanna visit it?")
                .setOngoing(false)
                .setContentText(desc)
                .setOnlyAlertOnce(true)
                .setSound(alarmSound)
                .setContentIntent(pendingIntent).build()



        // notificationManager.notify(123, notification)
        startForeground(123, notification)


    }

    class getGeofenceTransitionDetails(){
        var context: Context?= null
        var geofenceTransition:Int?=null
        var list:List<Geofence>?=null
    }
}