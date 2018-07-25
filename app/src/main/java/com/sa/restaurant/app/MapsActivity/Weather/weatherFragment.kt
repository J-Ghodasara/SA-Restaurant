package com.sa.restaurant.app.MapsActivity.Weather


import android.os.Bundle
import android.app.Fragment
import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest

import com.sa.restaurant.R
import com.sa.restaurant.R.drawable
import com.sa.restaurant.app.MapsActivity.MapsFragment
import com.sa.restaurant.app.MapsActivity.Weather.presenter.WeatherPresenter
import com.sa.restaurant.app.MapsActivity.Weather.presenter.WeatherPresenterImpl
import com.sa.restaurant.app.MapsActivity.Weather.view.WeatherView
import com.sa.restaurant.app.RestaurantsActivity.IGoogleApiServices
import com.sa.restaurant.app.RestaurantsActivity.RetrofitnearbyClient
import com.sa.restaurant.app.RestaurantsActivity.presenter.MapsPresenter
import com.sa.restaurant.app.RestaurantsActivity.presenter.MapsPresenterImpl
import kotlinx.android.synthetic.main.fragment_weather.*
import android.graphics.drawable.Drawable
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.location.LocationServices
import com.sa.restaurant.app.roomDatabase.FavoritesTable
import com.sa.restaurant.app.roomDatabase.Mydatabase
import com.sa.restaurant.app.roomDatabase.Table
import com.sa.restaurant.app.roomDatabase.WeatherInfoTable


/**
 * A Simple Weather Fragment
 * created on-> 25 july 2018
 * created by-> jay.ghodasara
 */

class weatherFragment : Fragment() ,WeatherView{



    lateinit var iGoogleApiServices: IGoogleApiServices
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationreq: LocationRequest
    lateinit var locationcallback: LocationCallback
    var images:ArrayList<Int> = ArrayList()
    lateinit var mydb: Mydatabase
    lateinit var passed_view:View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view:View= inflater.inflate(R.layout.fragment_weather, container, false)
        passed_view=view
        var weatherPresenter: WeatherPresenter = WeatherPresenterImpl()
        weatherPresenter.createClient(activity)
        //mMap!!.isMyLocationEnabled = true

        iGoogleApiServices = RetrofitnearbyClient.getClient("https://query.yahooapis.com/").create(IGoogleApiServices::class.java)

        locationreq = weatherPresenter.BuildLocationreq()
        locationcallback = weatherPresenter.Buildlocationcallback(iGoogleApiServices,activity,passed_view)
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
            fusedLocationProviderClient.requestLocationUpdates(locationreq, locationcallback, Looper.myLooper())
        }

    return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun sendlocation(location: Location, context: Context,iGoogleApiServices: IGoogleApiServices,view: View) {
        var weatherPresenter: WeatherPresenter = WeatherPresenterImpl()
         weatherPresenter.getNameFromLatLng(location,context,iGoogleApiServices,view)


    }

    override fun sendweatherInfo(bundle: Bundle, context: Context,view: View) {

        var cityValue= bundle["city"]
        var humidityValue=bundle["humidity"]
        var unit=bundle["unit"]
        var code=bundle["code"]
        var temperatureValue=bundle["temperature"]
        var text=bundle["text"]
        var region=bundle["region"]

        var weather_type:TextView= view.findViewById(R.id.weather_type)
        var humidity:TextView=view.findViewById(R.id.humidity)
        var temperature_unit:TextView=view.findViewById(R.id.temperature_unit)
        var temperature:TextView=view.findViewById(R.id.temperature)
        var city:TextView=view.findViewById(R.id.city)
        var weathericon:ImageView=view.findViewById(R.id.weathericon)

         weather_type.text= text.toString()
        humidity.text=humidityValue.toString()
        temperature_unit.text=unit.toString()
        temperature.text=temperatureValue.toString()
        city.text=cityValue.toString()+" "+region.toString()
Log.i("image","drawable/icon$code")
        var resources:Int=context.resources.getIdentifier("drawable/icon$code",null,"com.sa.restaurant")
        var icon:Drawable=context.resources.getDrawable(resources)
        weathericon.setImageDrawable(icon)

        mydb = Room.databaseBuilder(context, Mydatabase::class.java, "Database").allowMainThreadQueries().build()
        var sharedpref: SharedPreferences = context.getSharedPreferences("UserInfo", 0)
        var  Username = sharedpref.getString("username", null)

        var  uid=  mydb.myDao().getUserId(Username!!)
        var result:List<WeatherInfoTable> = mydb.myDao().checkUserId(uid)


        var weatherInfoTable:WeatherInfoTable= WeatherInfoTable()
        weatherInfoTable.uid=uid
        weatherInfoTable.place=region.toString()
        weatherInfoTable.city=cityValue.toString()
        weatherInfoTable.temperature=temperatureValue.toString()
        weatherInfoTable.humidity=humidityValue.toString()

        if(result.isNotEmpty()) {
            mydb.myDao().updateWeatherInfo(weatherInfoTable)

        }else{

            mydb.myDao().addWeatherInfo(weatherInfoTable)

        }



    }
}
