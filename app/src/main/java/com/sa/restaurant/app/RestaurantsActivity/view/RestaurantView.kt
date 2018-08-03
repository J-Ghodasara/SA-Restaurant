package com.sa.restaurant.app.RestaurantsActivity.view

import android.content.Context
import android.location.Location
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.sa.restaurant.adapters.RestaurantAdapter
import com.sa.restaurant.app.RestaurantsActivity.IGoogleApiServices
import com.sa.restaurant.app.RestaurantsActivity.model.RestaurantData

interface RestaurantView{
    fun getcurrentlatlng(location: Location,iGoogleApiServices: IGoogleApiServices,context: ViewGroup,activity: Context,adapter: RestaurantAdapter)

    fun restaurantslist(list:ArrayList<RestaurantData>,activity: Context,adapter: RestaurantAdapter,recyclerView: RecyclerView)
}