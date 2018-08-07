package com.sa.restaurant.app.MapsActivity.Weather.view

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.View
import com.sa.restaurant.app.RestaurantsActivity.IGoogleApiServices

/**
 * WeatherView class
 * Created On :- 23 july 2018
 * Created by :- jay.ghodasara
 */

interface WeatherView{

    fun sendlocation(location: Location,context: Context,iGoogleApiServices: IGoogleApiServices,view: View)

    fun sendweatherInfo(bundle: Bundle,context: Context,view: View)
}