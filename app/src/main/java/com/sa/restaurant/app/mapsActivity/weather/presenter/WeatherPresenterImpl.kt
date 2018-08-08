package com.sa.restaurant.app.mapsActivity.weather.presenter

import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Location
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.sa.restaurant.app.mapsActivity.weather.view.WeatherView
import com.sa.restaurant.app.mapsActivity.weather.WeatherFragment
import com.sa.restaurant.app.restaurantsActivity.IGoogleApiServices
import com.sa.restaurant.app.restaurantsActivity.presenter.MapsPresenterImpl
import com.sa.restaurant.utils.Toastutils
import retrofit2.Call
import retrofit2.Response
import android.location.Geocoder
import android.view.View
import com.sa.restaurant.app.restaurantsActivity.RestaurantActivity
import com.sa.restaurant.app.roomDatabase.Mydatabase
import com.sa.restaurant.app.roomDatabase.WeatherInfoTable
import java.util.*

/**
 * WeatherPresenterImpl class
 * Created On :- 27 july 2018
 * Created by :- jay.ghodasara
 */

class WeatherPresenterImpl : WeatherPresenter, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    lateinit var locationReq: LocationRequest
    lateinit var locationCallback: LocationCallback
    var latlng: LatLng? = null
    var GEOFENCE_ID_STAN_UNI = "My_Location"
    var loc: Location? = null
    var result: com.sa.restaurant.app.mapsActivity.weather.model.Response = com.sa.restaurant.app.mapsActivity.weather.model.Response()


    override fun buildLocationreq(): LocationRequest {
        locationReq = LocationRequest()
        locationReq.interval = 60000 * 60

        locationReq.fastestInterval = 5000
        locationReq.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        return locationReq
    }

    override fun buildlocationcallback(iGoogleApiServices: IGoogleApiServices, activity: Context, view: View?, flag: Int): LocationCallback {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                loc = p0!!.locations[p0.locations.size - 1]

                latlng = LatLng(loc!!.latitude, loc!!.longitude)

                MapsPresenterImpl.AREA_LANDMARKS[GEOFENCE_ID_STAN_UNI] = latlng!!
                if (flag == 2) {
                    val weatherView: WeatherView = WeatherFragment()
                    weatherView.sendlocation(loc!!, activity, iGoogleApiServices, view!!)

                }
                if (flag == 1) {
                    getNameFromLatLng(loc!!, activity, iGoogleApiServices, view!!, 1)
                }
                if (flag == 3) {
                    getNameFromLatLngForAlarm(loc!!, activity, iGoogleApiServices)
                }

            }
        }

        return locationCallback
    }


    override fun createClient(context: Context): GoogleApiClient {
        var gClient: GoogleApiClient
        synchronized(this) {

            gClient = GoogleApiClient.Builder(context).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build()
            gClient.connect()
            return gClient
        }
    }

    fun getWeatherInfo(context: Context, typeplace: Bundle, iGoogleApiServices: IGoogleApiServices, view: View?, flag: Int) {
        val url = getWeatherUrl(typeplace)
        iGoogleApiServices.getWeatherInfo(url).enqueue(object : retrofit2.Callback<com.sa.restaurant.app.mapsActivity.weather.model.Response> {
            override fun onFailure(call: Call<com.sa.restaurant.app.mapsActivity.weather.model.Response>?, t: Throwable?) {
                Toastutils.showToast(context, "Failed")
            }

            override fun onResponse(call: Call<com.sa.restaurant.app.mapsActivity.weather.model.Response>?, response: Response<com.sa.restaurant.app.mapsActivity.weather.model.Response>?) {


                result = response!!.body()!!

                //  var latlng2: LatLng? = null
                if (response!!.body()!! != null) {
                    val city = result.query!!.results!!.channel!!.location!!.city
                    val region = result.query!!.results!!.channel!!.location!!.region
                    val humidity = result.query!!.results!!.channel!!.atmosphere!!.humidity
                    val unit = result.query!!.results!!.channel!!.units!!.temperature
                    val code = result.query!!.results!!.channel!!.item!!.condition!!.code
                    val temperature = result.query!!.results!!.channel!!.item!!.condition!!.temp
                    val text = result.query!!.results!!.channel!!.item!!.condition!!.text

                    val bundle: Bundle = Bundle()
                    bundle.putString("city", city)
                    bundle.putString("humidity", humidity)
                    bundle.putString("unit", unit)
                    bundle.putString("code", code)
                    bundle.putString("temperature", temperature)
                    bundle.putString("text", text)
                    bundle.putString("region", region)


                    if (flag == 2) {
                        val weatherView: WeatherView = WeatherFragment()
                        weatherView.sendweatherInfo(bundle, context, view!!)

                    }
                    if (flag == 1) {
                        val restaurantActivity: RestaurantActivity = RestaurantActivity()
                        restaurantActivity.setWeatherInfo(view!!, bundle, context)
                    }
                } else {

                }
            }


        })
    }

    fun getWeatherUrl(bundle: Bundle): String {
        var city = bundle["city"]
        var placename = bundle["place"]
        var weatherUrl: StringBuilder = StringBuilder("https://query.yahooapis.com/v1/public/yql?q=select * from weather.forecast where woeid in (select woeid from geo.places(1) ")
        weatherUrl.append("where text=\"$city,$placename\")")
        weatherUrl.append("&format=json")

        return weatherUrl.toString()
    }

    override fun getNameFromLatLng(location: Location, context: Context, iGoogleApiServices: IGoogleApiServices, view: View, flag: Int) {
        val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>

        addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        val city = addresses[0].locality
        val state = addresses[0].adminArea
        val country = addresses[0].countryName
        val postalCode = addresses[0].postalCode
        val knownName = addresses[0].featureName

        var bundle: Bundle = Bundle()
        bundle.putString("city", city)
        bundle.putString("place", knownName)

        if (flag == 2) {
            getWeatherInfo(context, bundle, iGoogleApiServices, view, 2)
        }
        if (flag == 1) {
            getWeatherInfo(context, bundle, iGoogleApiServices, view, 1)
        }
        if (flag == 3) {
            getWeatherInfo(context, bundle, iGoogleApiServices, view, 3)
        }


    }


    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onConnected(p0: Bundle?) {


    }

    override fun onConnectionSuspended(p0: Int) {

    }


    override fun getNameFromLatLngForAlarm(location: Location, context: Context, iGoogleApiServices: IGoogleApiServices) {
        val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>

        addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        val city = addresses[0].locality
        val state = addresses[0].adminArea
        val country = addresses[0].countryName
        val postalCode = addresses[0].postalCode
        val knownName = addresses[0].featureName

        val bundle: Bundle = Bundle()
        bundle.putString("city", city)
        bundle.putString("place", knownName)


        getWeatherInfoForAlarm(context, bundle, iGoogleApiServices)
    }

    fun getWeatherInfoForAlarm(context: Context, typeplace: Bundle, iGoogleApiServices: IGoogleApiServices) {
        val url = getWeatherUrl(typeplace)
        iGoogleApiServices.getWeatherInfo(url).enqueue(object : retrofit2.Callback<com.sa.restaurant.app.mapsActivity.weather.model.Response> {
            override fun onFailure(call: Call<com.sa.restaurant.app.mapsActivity.weather.model.Response>?, t: Throwable?) {
                Toastutils.showToast(context, "Failed")
            }

            override fun onResponse(call: Call<com.sa.restaurant.app.mapsActivity.weather.model.Response>?, response: Response<com.sa.restaurant.app.mapsActivity.weather.model.Response>?) {


                result = response!!.body()!!


                if (response!!.body()!! != null) {
                    val city = result.query!!.results!!.channel!!.location!!.city
                    val region = result.query!!.results!!.channel!!.location!!.region
                    val humidity = result.query!!.results!!.channel!!.atmosphere!!.humidity
                    var unit = result.query!!.results!!.channel!!.units!!.temperature
                    val code = result.query!!.results!!.channel!!.item!!.condition!!.code
                    val temperature = result.query!!.results!!.channel!!.item!!.condition!!.temp
                    val text = result.query!!.results!!.channel!!.item!!.condition!!.text


                    val mydb: Mydatabase = Room.databaseBuilder(context, Mydatabase::class.java, "Database").allowMainThreadQueries().build()
                    val sharedpref: SharedPreferences = context.getSharedPreferences("UserInfo", 0)
                    val username = sharedpref.getString("username", null)

                    val uid = mydb.myDao().getUserId(username!!)

                    val result: List<WeatherInfoTable> = mydb.myDao().checkUserId(uid)

                    if (result.isNotEmpty()) {
                        val weatherInfoTable: WeatherInfoTable = WeatherInfoTable()
                        weatherInfoTable.ID = result[0].ID
                        weatherInfoTable.uid = uid
                        weatherInfoTable.place = region.toString()
                        weatherInfoTable.city = city.toString()
                        weatherInfoTable.temperature = temperature.toString()
                        weatherInfoTable.humidity = humidity.toString()
                        weatherInfoTable.WeatherType = text.toString()
                        weatherInfoTable.Code = code.toString()
                        mydb.myDao().updateWeatherInfo(weatherInfoTable)
                    } else {
                        val weatherInfoTable: WeatherInfoTable = WeatherInfoTable()
                        weatherInfoTable.uid = uid
                        weatherInfoTable.place = region.toString()
                        weatherInfoTable.city = city.toString()
                        weatherInfoTable.temperature = temperature.toString()
                        weatherInfoTable.humidity = humidity.toString()
                        weatherInfoTable.WeatherType = text.toString()
                        weatherInfoTable.Code = code.toString()
                        mydb.myDao().addWeatherInfo(weatherInfoTable)
                    }


                } else {

                }
            }


        })
    }


}