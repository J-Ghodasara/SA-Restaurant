package com.sa.restaurant.app.RestaurantsActivity.presenter

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.sa.restaurant.adapters.RestaurantAdapter
import com.sa.restaurant.app.RestaurantsActivity.IGoogleApiServices


interface RestaurantPresenter{



    fun BuildLocationreq(): LocationRequest

   fun Buildlocationcallback(iGoogleApiServices:IGoogleApiServices,context: ViewGroup,activity: Context,adapter: RestaurantAdapter,recyclerView: RecyclerView): LocationCallback

    fun checklocationpermission(context: Activity): Boolean

    fun createClient( context: Context):GoogleApiClient
}