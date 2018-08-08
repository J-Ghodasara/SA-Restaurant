package com.sa.restaurant.app.restaurantsActivity.presenter

import android.app.Activity
import android.content.Context
import android.location.Location
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.GoogleMap
import com.sa.restaurant.app.restaurantsActivity.IGoogleApiServices

/**
 * MapsPresenter class
 * Created On :- 23 july 2018
 * Created by :- jay.ghodasara
 */

interface MapsPresenter {

    fun nearbyplaces2(context: Context, typeplace: String, location: Location, iGoogleApiServices: IGoogleApiServices, mMap: GoogleMap)

    fun BuildLocationreq(): LocationRequest

    fun Buildlocationcallback(iGoogleApiServices: IGoogleApiServices, activity: Context): LocationCallback

    fun checklocationpermission(context: Activity): Boolean

    fun createClient(context: Context): GoogleApiClient

    fun favRestaurants(context: Context, typeplace: String, location: Location, iGoogleApiServices: IGoogleApiServices, mMap: GoogleMap)
}