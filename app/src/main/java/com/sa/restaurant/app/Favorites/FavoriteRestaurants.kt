package com.sa.restaurant.app.Favorites


import android.arch.persistence.room.Room
import android.content.SharedPreferences
import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import com.sa.restaurant.R
import com.sa.restaurant.adapters.restaurantadapter
import com.sa.restaurant.app.RestaurantsActivity.model.RestaurantData
import com.sa.restaurant.app.roomDatabase.FavoritesTable
import com.sa.restaurant.app.roomDatabase.Mydatabase
import kotlinx.android.synthetic.main.fragment_favorite_restaurants.*


/**
 * Fragment for favorite restaurants
 *
 */
class FavoriteRestaurants : Fragment() {

    lateinit var mydb: Mydatabase
    lateinit var adapter: restaurantadapter
    lateinit var myView: View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v: View = inflater.inflate(R.layout.fragment_favorite_restaurants, container, false)
        myView = v
        mydb = Room.databaseBuilder(activity, Mydatabase::class.java, "Database").allowMainThreadQueries().build()

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var sharedpref: SharedPreferences = activity.getSharedPreferences("UserInfo", 0)
        var Username = sharedpref.getString("username", null)
        var uid = mydb.myDao().getUserId(Username!!)
        var list: List<FavoritesTable> = mydb.myDao().getFavorites(uid)

        var favorite_list: ArrayList<RestaurantData> = ArrayList()
        for (l in list.indices) {

            var restaurantData: RestaurantData = RestaurantData()
            restaurantData.Name = list[l].restaurantName
            restaurantData.Address = list[l].restaurantAddress
            restaurantData.image = list[l].restaurantPhoto
            favorite_list.add(restaurantData)
        }

        adapter = restaurantadapter(activity, favorite_list, myView)
        var dividerItemDecoration: com.sa.restaurant.adapters.DividerItemDecoration = com.sa.restaurant.adapters.DividerItemDecoration(activity)
        fav_restaurants_recycler.addItemDecoration(dividerItemDecoration)

        fav_restaurants_recycler.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        fav_restaurants_recycler.adapter = adapter

    }

    fun reload(context: Context, myView: View) {
        var sharedpref: SharedPreferences = context.getSharedPreferences("UserInfo", 0)
        var Username = sharedpref.getString("username", null)
        mydb = Room.databaseBuilder(context, Mydatabase::class.java, "Database").allowMainThreadQueries().build()
        var uid = mydb.myDao().getUserId(Username!!)
        var list: List<FavoritesTable> = mydb.myDao().getFavorites(uid)
        var favorite_list: ArrayList<RestaurantData> = ArrayList()
        for (l in list.indices) {

            var restaurantData: RestaurantData = RestaurantData()
            restaurantData.Name = list[l].restaurantName
            restaurantData.Address = list[l].restaurantAddress
            restaurantData.image = list[l].restaurantPhoto
            favorite_list.add(restaurantData)
        }
        var fav_restaurants_recycler: RecyclerView = myView.findViewById(R.id.fav_restaurants_recycler)
        adapter = restaurantadapter(context, favorite_list, myView)
        var dividerItemDecoration: com.sa.restaurant.adapters.DividerItemDecoration = com.sa.restaurant.adapters.DividerItemDecoration(context)
        fav_restaurants_recycler.addItemDecoration(dividerItemDecoration)

        fav_restaurants_recycler.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        fav_restaurants_recycler.adapter = adapter
    }


}
