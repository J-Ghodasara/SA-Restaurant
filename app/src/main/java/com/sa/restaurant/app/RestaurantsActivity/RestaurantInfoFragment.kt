package com.sa.restaurant.app.RestaurantsActivity

import android.os.Bundle
import android.app.Fragment
import android.app.ProgressDialog
import android.os.ResultReceiver
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sa.restaurant.R
import com.sa.restaurant.app.RestaurantsActivity.model.PlaceInfo.Response
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_restaurant_info.*
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.*


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
    var placeId:String?=null
    var todays_timings: String? = null
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
        placeId=arguments.getString("Clicked_placeId")


        var dialog:ProgressDialog= ProgressDialog.show(activity,null,"Please Wait")
        var iGoogleApiServices: IGoogleApiServices = RetrofitnearbyClient.getClient("https://maps.googleapis.com/").create(IGoogleApiServices::class.java)
        iGoogleApiServices.getrestaurantInfo(getrestaurantInfoUrl(placeId!!)).enqueue(object : retrofit2.Callback<com.sa.restaurant.app.RestaurantsActivity.model.PlaceInfo.Response> {


            override fun onFailure(call: Call<Response>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<Response>?, response: retrofit2.Response<com.sa.restaurant.app.RestaurantsActivity.model.PlaceInfo.Response>?) {

                var result: com.sa.restaurant.app.RestaurantsActivity.model.PlaceInfo.Response = response!!.body()!!

                Log.i("result", result.toString())
                var calendar: Calendar = Calendar.getInstance()
                var date: Date = calendar.time
                var day = SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.time)
                Log.i("Day", day)
                if (result.result == null || result.result.opening_hours == null) {
                    todays_timings = "Timings Information Not Available"
                    Log.i("Thursday", todays_timings)
                    timings_info.text = todays_timings
                    dialog.dismiss()
                } else {
                    var days = result.result.opening_hours.weekday_text
                    Log.i("Days", days.toString())
                    when (day) {
                        "Monday" -> {
                            todays_timings = days[0].toString()
                            Log.i("Monday", todays_timings)
                        }
                        "Tuesday" -> {
                            todays_timings = days[1].toString()
                            Log.i("Tuesday", todays_timings)
                        }
                        "Wednesday" -> {
                            todays_timings = days[2].toString()
                            Log.i("Wednesday", todays_timings)
                        }
                        "Thursday" -> {
                            todays_timings = days[3]
                            Log.i("Thursday", todays_timings)
                        }
                        "Friday" -> {
                            todays_timings = days[4].toString()
                            Log.i("Friday", todays_timings)
                        }
                        "Saturday" -> {
                            todays_timings = days[5].toString()
                            Log.i("Saturday", todays_timings)
                        }
                        "Sunday" -> {
                            todays_timings = days[6].toString()
                            Log.i("Sunday", todays_timings)
                        }


                    }
                    timings_info.text = todays_timings
                    dialog.dismiss()
                }

            }

        })

        return view
    }

    fun getrestaurantInfoUrl(placeId: String): String {
        var googleplaceurl: StringBuilder = StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?")
        googleplaceurl.append("placeid=" + placeId)
        googleplaceurl.append("&key=" + "AIzaSyB0_n9UBObCELuk4pLP8XL1kIKghrPNdks")
        return googleplaceurl.toString()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        name.text = restroName
        address.text = restroAddress
        Picasso.get().load(restroImg).into(img)
        ratingbar.isClickable=false
        ratingbar.rating=ratings!!.toFloat()

//        Log.i("timingsOnactivity",todays_timing)
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
