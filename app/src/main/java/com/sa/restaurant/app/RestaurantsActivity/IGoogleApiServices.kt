package com.sa.restaurant.app.RestaurantsActivity


import com.sa.restaurant.app.MapsActivity.Weather.Model.Response
import com.sa.restaurant.app.RestaurantsActivity.model.POJO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface IGoogleApiServices {

    @GET
    fun getnearbyplaces(@Url url: String): Call<POJO>

    @GET
    fun getWeatherInfo(@Url url: String): Call<Response>



}