package com.sa.restaurant.app

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationServices
import com.sa.restaurant.adapters.GeofenceTransitionsIntentService
import com.sa.restaurant.app.MapsActivity.MapsFragment
import com.sa.restaurant.app.RestaurantsActivity.presenter.RestaurantPresenterImpl
import com.sa.restaurant.utils.Toastutils

class GeofenceService(context: Context) : Service() {

    var context: Context = context
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        return START_STICKY
    }


}