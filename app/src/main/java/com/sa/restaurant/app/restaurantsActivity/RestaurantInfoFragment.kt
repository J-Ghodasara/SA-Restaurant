package com.sa.restaurant.app.restaurantsActivity

import android.os.Bundle
import android.app.Fragment
import android.app.ProgressDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sa.restaurant.R
import com.sa.restaurant.app.restaurantsActivity.model.PlaceInfo.Response
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
        RestaurantActivity.homeIsVisible=false
       isInfoVisible=true
        val view: View = inflater.inflate(R.layout.fragment_restaurant_info, container, false)
        RestaurantActivity.homeIsVisible = false


        restroName = arguments.getString("restroName", null).toString()
        restroAddress = arguments.getString("restroAddress", null).toString()
        restroImg = arguments.getString("restroImg", null).toString()
        ratings=arguments.getDouble("rating")

        open=arguments.getString("open")
        placeId=arguments.getString("Clicked_placeId")


        var dialog:ProgressDialog= ProgressDialog.show(activity,null,"Please Wait")
        var iGoogleApiServices: IGoogleApiServices = RetrofitnearbyClient.getClient("https://maps.googleapis.com/").create(IGoogleApiServices::class.java)
        iGoogleApiServices.getrestaurantInfo(getrestaurantInfoUrl(placeId!!)).enqueue(object : retrofit2.Callback<com.sa.restaurant.app.restaurantsActivity.model.PlaceInfo.Response> {


            override fun onFailure(call: Call<Response>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<Response>?, response: retrofit2.Response<com.sa.restaurant.app.restaurantsActivity.model.PlaceInfo.Response>?) {

                val result: com.sa.restaurant.app.restaurantsActivity.model.PlaceInfo.Response = response!!.body()!!


                val calendar: Calendar = Calendar.getInstance()
                val date: Date = calendar.time
                val day = SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.time)

                if (result.result == null || result.result.opening_hours == null) {
                    todays_timings = "Timings Information Not Available"

                    timings_info.text = todays_timings
                    dialog.dismiss()
                } else {
                    val days = result.result.opening_hours.weekday_text

                    when (day) {
                        "Monday" -> {
                            todays_timings = days[0].toString()

                        }
                        "Tuesday" -> {
                            todays_timings = days[1].toString()

                        }
                        "Wednesday" -> {
                            todays_timings = days[2].toString()

                        }
                        "Thursday" -> {
                            todays_timings = days[3]

                        }
                        "Friday" -> {
                            todays_timings = days[4].toString()

                        }
                        "Saturday" -> {
                            todays_timings = days[5].toString()

                        }
                        "Sunday" -> {
                            todays_timings = days[6].toString()

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
        val googleplaceurl: StringBuilder = StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?")
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

       activity.runOnUiThread(Runnable {
           ratingbar.rating=ratings!!.toFloat()
       })


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
