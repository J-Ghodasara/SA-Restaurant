package com.sa.restaurant.app.RestaurantsActivity

import android.os.Bundle
import android.app.Fragment
import android.os.ResultReceiver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sa.restaurant.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_restaurant_info.*


/**
 * Restaurant Info fragment that shows the information of particular restaurant.
 * created on:- 29 july 18
 * created by:- jay.ghodasara
 */
class RestaurantInfoFragment : Fragment() {
    lateinit var restroName: String
    lateinit var restroAddress: String
    lateinit var restroImg: String
     var ratings:Double?=null
    lateinit var open:String
    companion object {
        var isInfoVisible: Boolean = false
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        var view: View = inflater.inflate(R.layout.fragment_restaurant_info, container, false)
        RestaurantActivity.homeIsVisible = false
        //RestaurantActivity.favIsVisibletouser=RestaurantActivity.favIsVisibletouser
        isInfoVisible = true
        restroName = arguments.getString("restroName", null).toString()
        restroAddress = arguments.getString("restroAddress", null).toString()
        restroImg = arguments.getString("restroImg", null).toString()
        ratings=arguments.getDouble("rating")
        open=arguments.getString("open")


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        name.text = restroName
        address.text = restroAddress
        Picasso.get().load(restroImg).into(img)
        ratingbar.isClickable=false
        ratingbar.rating=ratings!!.toFloat()
        if(open=="NotAvailable"){
            open_status.visibility=View.GONE
        }else{
            if(open=="true"){
                open_status.text="Currently Open"
            }else{
                open_status.text="Currently Closed"
            }

        }

    }


}
