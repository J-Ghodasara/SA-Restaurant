package com.sa.restaurant.app.RestaurantsActivity


import com.sa.restaurant.app.MapsActivity.Weather.Model.Response
import com.sa.restaurant.app.RestaurantsActivity.model.POJO
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
    fun getrestaurantInfo(@Url url: String): Call<com.sa.restaurant.app.RestaurantsActivity.model.PlaceInfo.Response>



}