package com.sa.restaurant.app.RestaurantsActivity.presenter

import android.app.Activity
import android.content.Context
import android.location.Location
import android.view.ViewGroup
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.sa.restaurant.adapters.restaurantadapter
import com.sa.restaurant.app.RestaurantsActivity.IGoogleApiServices


interface RestaurantPresenter{

    //fun nearbyplaces(context: Context,typeplace: String,location: Location,iGoogleApiServices:IGoogleApiServices):ArrayList<String>

    fun BuildLocationreq(): LocationRequest

   fun Buildlocationcallback(iGoogleApiServices:IGoogleApiServices,context: ViewGroup,activity: Context,adapter: restaurantadapter): LocationCallback

    fun checklocationpermission(context: Activity): Boolean

    fun createClient( context: Context):GoogleApiClient
}