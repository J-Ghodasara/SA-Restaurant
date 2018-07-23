package com.sa.restaurant.app.RestaurantsActivity.view

import android.content.Context
import android.location.Location
import android.view.ViewGroup
import com.sa.restaurant.adapters.restaurantadapter
import com.sa.restaurant.app.RestaurantsActivity.IGoogleApiServices
import com.sa.restaurant.app.RestaurantsActivity.model.RestaurantData

interface RestaurantView{
    fun getcurrentlatlng(location: Location,iGoogleApiServices: IGoogleApiServices,context: ViewGroup,activity: Context,adapter: restaurantadapter)

    fun restaurantslist(list:ArrayList<RestaurantData>,activity: Context,adapter: restaurantadapter)
}