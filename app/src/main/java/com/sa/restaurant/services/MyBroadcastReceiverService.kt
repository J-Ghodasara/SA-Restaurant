package com.sa.restaurant.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.sa.restaurant.app.mapsActivity.weather.presenter.WeatherPresenter
import com.sa.restaurant.app.mapsActivity.weather.presenter.WeatherPresenterImpl
import com.sa.restaurant.app.mapsActivity.weather.WeatherFragment
import com.sa.restaurant.app.restaurantsActivity.IGoogleApiServices
import com.sa.restaurant.app.restaurantsActivity.RetrofitnearbyClient

/**
 * MyBroadcastreceiver class for updating the weather info inside the Room database every 1 hour
 * Created On :- 23 july 2018
 * Created by :- jay.ghodasara
 */


class MyBroadcastReceiverService : BroadcastReceiver() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationreq: LocationRequest
    lateinit var locationcallback: LocationCallback
    lateinit var iGoogleApiServices: IGoogleApiServices

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("Alarm", "Triggered")
        WeatherFragment.iGoogleApiServices = RetrofitnearbyClient.getClient("https://query.yahooapis.com/").create(IGoogleApiServices::class.java)
        val weatherPresenter: WeatherPresenter = WeatherPresenterImpl()
        weatherPresenter.createClient(context!!)
        locationreq = weatherPresenter.buildLocationreq()
        locationcallback = weatherPresenter.buildlocationcallback(WeatherFragment.iGoogleApiServices, context, null, 3)
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationProviderClient.requestLocationUpdates(locationreq, locationcallback, Looper.myLooper())
        }
    }


}