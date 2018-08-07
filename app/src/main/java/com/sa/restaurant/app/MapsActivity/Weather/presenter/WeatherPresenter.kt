package com.sa.restaurant.app.MapsActivity.Weather.presenter

import android.content.Context
import android.location.Location
import android.view.View
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.sa.restaurant.app.RestaurantsActivity.IGoogleApiServices

/**
 * WeatherPresenter class
 * Created On :- 27 july 2018
 * Created by :- jay.ghodasara
 */

interface WeatherPresenter {

    fun BuildLocationreq(): LocationRequest

    fun Buildlocationcallback(iGoogleApiServices: IGoogleApiServices, activity: Context, view: View?, flag: Int): LocationCallback


    fun createClient(context: Context): GoogleApiClient


    fun getNameFromLatLng(location: Location, context: Context, iGoogleApiServices: IGoogleApiServices, view: View, flag: Int)

    fun getNameFromLatLngForAlarm(location: Location, context: Context, iGoogleApiServices: IGoogleApiServices)


}