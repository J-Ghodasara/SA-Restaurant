package com.sa.restaurant.app.restaurantsActivity.presenter

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.sa.restaurant.adapters.RestaurantAdapter
import com.sa.restaurant.app.restaurantsActivity.IGoogleApiServices

/**
 * RestaurantPresenter class
 * Created On :- 23 july 2018
 * Created by :- jay.ghodasara
 */

interface RestaurantPresenter{



    fun buildLocationreq(): LocationRequest

   fun buildlocationcallback(iGoogleApiServices:IGoogleApiServices, context: ViewGroup, activity: Context, adapter: RestaurantAdapter, recyclerView: RecyclerView): LocationCallback

    fun checklocationpermission(context: Activity): Boolean

    fun createClient( context: Context):GoogleApiClient
}