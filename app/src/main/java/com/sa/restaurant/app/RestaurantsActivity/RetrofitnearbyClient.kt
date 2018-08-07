package com.sa.restaurant.app.RestaurantsActivity

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Provides Retrofit object
 * Created On :- 23 july 2018
 * Created by :- jay.ghodasara
 */

object RetrofitnearbyClient {
    var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit {
        if (retrofit == null) {

            retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return retrofit!!

    }
}