package com.sa.restaurant.app.mapsActivity.weather


import android.app.Fragment
import android.app.ProgressDialog
import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.sa.restaurant.R
import com.sa.restaurant.app.mapsActivity.weather.presenter.WeatherPresenter
import com.sa.restaurant.app.mapsActivity.weather.presenter.WeatherPresenterImpl
import com.sa.restaurant.app.mapsActivity.weather.view.WeatherView
import com.sa.restaurant.app.restaurantsActivity.IGoogleApiServices
import com.sa.restaurant.app.restaurantsActivity.RetrofitnearbyClient
import com.sa.restaurant.app.roomDatabase.Mydatabase
import com.sa.restaurant.app.roomDatabase.WeatherInfoTable


/**
 * A Simple Weather Fragment
 * created on-> 25 july 2018
 * created by-> jay.ghodasara
 */

class WeatherFragment : Fragment(), WeatherView {


    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationreq: LocationRequest
    lateinit var locationcallback: LocationCallback
    var images: ArrayList<Int> = ArrayList()
    lateinit var mydb: Mydatabase
    lateinit var passed_view: View

    companion object {
        lateinit var dialog: ProgressDialog
        lateinit var iGoogleApiServices: IGoogleApiServices
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_weather, container, false)
        passed_view = view
        dialog = ProgressDialog(activity)
        dialog.setMessage("Please wait")
        dialog.setCancelable(false)
        dialog.isIndeterminate = true
        dialog.show()
        val weatherPresenter: WeatherPresenter = WeatherPresenterImpl()
        weatherPresenter.createClient(activity)


        iGoogleApiServices = RetrofitnearbyClient.getClient("https://query.yahooapis.com/").create(IGoogleApiServices::class.java)

        locationreq = weatherPresenter.buildLocationreq()
        locationcallback = weatherPresenter.buildlocationcallback(iGoogleApiServices, activity, passed_view, 2)
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
            fusedLocationProviderClient.requestLocationUpdates(locationreq, locationcallback, Looper.myLooper())
        }

        return view
    }


    override fun sendlocation(location: Location, context: Context, iGoogleApiServices: IGoogleApiServices, view: View) {
        val weatherPresenter: WeatherPresenter = WeatherPresenterImpl()
        weatherPresenter.getNameFromLatLng(location, context, iGoogleApiServices, view, 2)


    }

    override fun sendweatherInfo(bundle: Bundle, context: Context, view: View) {

        val cityValue = bundle["city"]
        val humidityValue: Any = bundle["humidity"]
        val unit = bundle["unit"]
        val code = bundle["code"]
        val temperatureValue = bundle["temperature"]
        val text: Any = bundle["text"]
        val region = bundle["region"]

        val weather_type: TextView = view.findViewById(R.id.weather_type)
        val humidity: TextView = view.findViewById(R.id.humidity)
        val temperature_unit: TextView = view.findViewById(R.id.temperature_unit)
        val temperature: TextView = view.findViewById(R.id.temperature)
        val city: TextView = view.findViewById(R.id.city)
        val weathericon: ImageView = view.findViewById(R.id.weathericon)

        weather_type.text = text.toString()
        humidity.text = humidityValue.toString()
        temperature_unit.text = unit.toString()
        temperature.text = temperatureValue.toString()
        city.text = cityValue.toString() + " " + region.toString()

        dialog.dismiss()

        val resources: Int = context.resources.getIdentifier("drawable/icon$code", null, "com.sa.restaurant")
        val icon: Drawable = context.resources.getDrawable(resources)
        weathericon.setImageDrawable(icon)

        mydb = Room.databaseBuilder(context, Mydatabase::class.java, "Database").allowMainThreadQueries().build()
        val sharedpref: SharedPreferences = context.getSharedPreferences("UserInfo", 0)
        val username = sharedpref.getString("username", null)

        val uid = mydb.myDao().getUserId(username!!)

        val result: List<WeatherInfoTable> = mydb.myDao().checkUserId(uid)

        if (result.isNotEmpty()) {
            val weatherInfoTable: WeatherInfoTable = WeatherInfoTable()
            weatherInfoTable.ID = result[0].ID
            weatherInfoTable.uid = uid
            weatherInfoTable.place = region.toString()
            weatherInfoTable.city = cityValue.toString()
            weatherInfoTable.temperature = temperatureValue.toString()
            weatherInfoTable.humidity = humidityValue.toString()
            weatherInfoTable.WeatherType = text.toString()
            weatherInfoTable.Code = code.toString()
            mydb.myDao().updateWeatherInfo(weatherInfoTable)
        } else {
            val weatherInfoTable: WeatherInfoTable = WeatherInfoTable()
            weatherInfoTable.uid = uid
            weatherInfoTable.place = region.toString()
            weatherInfoTable.city = cityValue.toString()
            weatherInfoTable.temperature = temperatureValue.toString()
            weatherInfoTable.humidity = humidityValue.toString()
            weatherInfoTable.WeatherType = text.toString()
            weatherInfoTable.Code = code.toString()
            mydb.myDao().addWeatherInfo(weatherInfoTable)
        }


    }
}
