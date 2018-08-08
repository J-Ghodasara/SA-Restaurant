package com.sa.restaurant.app.restaurantsActivity


import com.sa.restaurant.app.mapsActivity.weather.model.Response
import com.sa.restaurant.app.restaurantsActivity.model.POJO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * IGoogleApiServices interface
 * Created On :- 23 july 2018
 * Created by :- jay.ghodasara
 */

interface IGoogleApiServices {

    @GET
    fun getnearbyplaces(@Url url: String): Call<POJO>

    @GET
    fun getWeatherInfo(@Url url: String): Call<Response>

    @GET
    fun getrestaurantInfo(@Url url: String): Call<com.sa.restaurant.app.restaurantsActivity.model.PlaceInfo.Response>



}