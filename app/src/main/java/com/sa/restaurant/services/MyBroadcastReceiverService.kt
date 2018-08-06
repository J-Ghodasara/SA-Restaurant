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
import com.sa.restaurant.app.MapsActivity.Weather.presenter.WeatherPresenter
import com.sa.restaurant.app.MapsActivity.Weather.presenter.WeatherPresenterImpl
import com.sa.restaurant.app.MapsActivity.Weather.weatherFragment
import com.sa.restaurant.app.RestaurantsActivity.IGoogleApiServices
import com.sa.restaurant.app.RestaurantsActivity.RetrofitnearbyClient

class MyBroadcastReceiverService : BroadcastReceiver() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationreq: LocationRequest
    lateinit var locationcallback: LocationCallback
    lateinit var iGoogleApiServices: IGoogleApiServices

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("Alarm","Triggered")
        weatherFragment.iGoogleApiServices = RetrofitnearbyClient.getClient("https://query.yahooapis.com/").create(IGoogleApiServices::class.java)
        var weatherPresenter: WeatherPresenter = WeatherPresenterImpl()
        weatherPresenter.createClient(context!!)
        locationreq = weatherPresenter.BuildLocationreq()
        locationcallback = weatherPresenter.Buildlocationcallback(weatherFragment.iGoogleApiServices, context, null,3)
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationProviderClient.requestLocationUpdates(locationreq, locationcallback, Looper.myLooper())
        }
    }


}