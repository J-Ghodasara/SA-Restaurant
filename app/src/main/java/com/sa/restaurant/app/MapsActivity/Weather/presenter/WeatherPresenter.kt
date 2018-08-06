package com.sa.restaurant.app.MapsActivity.Weather.presenter

import android.content.Context
import android.location.Location
import android.view.View
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.sa.restaurant.app.RestaurantsActivity.IGoogleApiServices

interface WeatherPresenter {

    fun BuildLocationreq(): LocationRequest

    fun Buildlocationcallback(iGoogleApiServices: IGoogleApiServices, activity: Context, view: View?, flag:Int): LocationCallback



    fun createClient(context: Context): GoogleApiClient

    // fun getWeatherInfo(context: Context, typeplace: Bundle, iGoogleApiServices: IGoogleApiServices)

    fun getNameFromLatLng(location: Location, context: Context, iGoogleApiServices: IGoogleApiServices, view: View,flag:Int)

   fun getNameFromLatLngForAlarm(location: Location, context: Context, iGoogleApiServices: IGoogleApiServices)


}