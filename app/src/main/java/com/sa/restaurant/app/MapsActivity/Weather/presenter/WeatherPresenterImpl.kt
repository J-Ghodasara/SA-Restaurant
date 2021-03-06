package com.sa.restaurant.app.MapsActivity.Weather.presenter

import android.content.Context
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sa.restaurant.app.MapsActivity.MapsFragment
import com.sa.restaurant.app.MapsActivity.Weather.view.WeatherView
import com.sa.restaurant.app.MapsActivity.Weather.weatherFragment
import com.sa.restaurant.app.RestaurantsActivity.IGoogleApiServices
import com.sa.restaurant.app.RestaurantsActivity.model.POJO
import com.sa.restaurant.app.RestaurantsActivity.model.RestaurantData
import com.sa.restaurant.app.RestaurantsActivity.presenter.MapsPresenterImpl
import com.sa.restaurant.utils.Toastutils
import retrofit2.Call
import retrofit2.Response
import android.location.Geocoder
import android.view.View
import java.util.*


class WeatherPresenterImpl : WeatherPresenter, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    lateinit var locationReq: LocationRequest
    lateinit var locationCallback: LocationCallback
    var latlng: LatLng? = null
    var GEOFENCE_ID_STAN_UNI = "My_Location"
    var loc: Location? = null
    var result: com.sa.restaurant.app.MapsActivity.Weather.Model.Response = com.sa.restaurant.app.MapsActivity.Weather.Model.Response()


    override fun BuildLocationreq(): LocationRequest {
        locationReq = LocationRequest()
        locationReq.interval = 60000 * 60
        Log.i("Called", "in onconnected")
        locationReq.fastestInterval = 5000
        locationReq.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        return locationReq
    }

    override fun Buildlocationcallback(iGoogleApiServices: IGoogleApiServices, activity: Context, view: View): LocationCallback {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                loc = p0!!.locations[p0.locations.size - 1]
                Log.i("Location Weather", "${MapsPresenterImpl.loc}")
                latlng = LatLng(loc!!.latitude, loc!!.longitude)

                MapsPresenterImpl.AREA_LANDMARKS[GEOFENCE_ID_STAN_UNI] = latlng!!

                var weatherView: WeatherView = weatherFragment()
                weatherView.sendlocation(loc!!, activity, iGoogleApiServices, view)





            }
        }

        return locationCallback
    }


    override fun createClient(context: Context): GoogleApiClient {
        var gClient: GoogleApiClient
        synchronized(this) {
            Log.i("Client", "created")
            gClient = GoogleApiClient.Builder(context).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build()
            gClient.connect()
            return gClient
        }
    }

    fun getWeatherInfo(context: Context, typeplace: Bundle, iGoogleApiServices: IGoogleApiServices, view: View) {
        val url = getWeatherUrl(typeplace)
        iGoogleApiServices.getWeatherInfo(url).enqueue(object : retrofit2.Callback<com.sa.restaurant.app.MapsActivity.Weather.Model.Response> {
            override fun onFailure(call: Call<com.sa.restaurant.app.MapsActivity.Weather.Model.Response>?, t: Throwable?) {
                Toastutils.showToast(context, "Failed")
            }

            override fun onResponse(call: Call<com.sa.restaurant.app.MapsActivity.Weather.Model.Response>?, response: Response<com.sa.restaurant.app.MapsActivity.Weather.Model.Response>?) {


                result = response!!.body()!!
                Log.i("Response", result.toString())
                //  var latlng2: LatLng? = null
                if (response!!.body()!! != null) {
                    var city = result.query!!.results!!.channel!!.location!!.city
                    var region = result.query!!.results!!.channel!!.location!!.region
                    var humidity = result.query!!.results!!.channel!!.atmosphere!!.humidity
                    var unit = result.query!!.results!!.channel!!.units!!.temperature
                    var code = result.query!!.results!!.channel!!.item!!.condition!!.code
                    var temperature = result.query!!.results!!.channel!!.item!!.condition!!.temp
                    var text = result.query!!.results!!.channel!!.item!!.condition!!.text

                    var bundle: Bundle = Bundle()
                    bundle.putString("city", city)
                    bundle.putString("humidity", humidity)
                    bundle.putString("unit", unit)
                    bundle.putString("code", code)
                    bundle.putString("temperature", temperature)
                    bundle.putString("text", text)
                    bundle.putString("region", region)

                    Log.i("bundle", bundle.toString())
                    var weatherView: WeatherView = weatherFragment()
                    weatherView.sendweatherInfo(bundle, context, view)

                    //   Log.i("Total places", list.size.toString())

                } else {
                    Log.i("List not found", "Trying again")
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

    override fun getNameFromLatLng(location: Location, context: Context, iGoogleApiServices: IGoogleApiServices, view: View) {
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

        getWeatherInfo(context, bundle, iGoogleApiServices, view)

    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.i("OnConnectionFailed", "failed")
    }

    override fun onConnected(p0: Bundle?) {

        Log.i("OnConnected", "success")
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.i("OnConnectionSuspended", "failed")
    }

}