package com.sa.restaurant.app.restaurantsActivity.presenter

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.sa.restaurant.R
import com.sa.restaurant.app.mapsActivity.MapsFragment
import com.sa.restaurant.app.mapsActivity.MapsFragment.Companion.mMap
import com.sa.restaurant.app.restaurantsActivity.IGoogleApiServices
import com.sa.restaurant.app.restaurantsActivity.model.POJO
import com.sa.restaurant.app.restaurantsActivity.model.PhotosItem
import com.sa.restaurant.app.restaurantsActivity.model.RestaurantData
import com.sa.restaurant.app.roomDatabase.FavoritesTable
import com.sa.restaurant.app.roomDatabase.Mydatabase
import com.sa.restaurant.helpers.CustomInfoWindow
import com.sa.restaurant.helpers.InfoWindowPojo
import com.sa.restaurant.utils.Toastutils
import retrofit2.Call
import retrofit2.Response
import java.util.*

/**
 * MapsPresenterImpl class
 * Created On :- 23 july 2018
 * Created by :- jay.ghodasara
 */

class MapsPresenterImpl : MapsPresenter, GoogleApiClient.OnConnectionFailedListener {

    lateinit var latlng2: LatLng

    override fun nearbyplaces2(context: Context, typeplace: String, location: Location, iGoogleApiServices: IGoogleApiServices, mMap: GoogleMap) {
        val url = geturl(location.latitude, location.longitude, typeplace)
        iGoogleApiServices.getnearbyplaces(url).enqueue(object : retrofit2.Callback<POJO> {
            override fun onFailure(call: Call<POJO>?, t: Throwable?) {
                Toastutils.showToast(context, "Failed")
                Log.i("ResponseNearbyMaps", "error")
            }

            override fun onResponse(call: Call<POJO>?, response: Response<POJO>?) {
                var open: String? = null
                var photoreference: String? = null
                var photoitem: List<PhotosItem> = ArrayList()
                pojo = response!!.body()!!
                Log.i("ResponseNearbyMaps", response.body()!!.results.toString())

                if (response!!.body()!! != null) {
                    for (i in 0 until response.body()!!.results!!.size) {
                        val markerOptions: MarkerOptions = MarkerOptions()
                        var myMarker: Marker
                        val googlePlace = response.body()!!.results!![i]
                        val address = response.body()!!.results!![i].vicinity
                        val placename = googlePlace.name
                        val rating = googlePlace.rating
                        val placeId: String = googlePlace.place_id
                        val lat = googlePlace.geometry.location.lat
                        val lon = googlePlace.geometry.location.lng

                        if (googlePlace.opening_hours == null) {
                            open = "NotAvailable"

                        } else {
                            open = googlePlace.opening_hours.open_now.toString()
                        }





                        Log.i("LatLng nearbyplaces", lat.toString() + "  " + lon)
                        if (googlePlace.photos == null) {
                            photoreference = "NotAvailable"
                        } else {
                            photoitem = googlePlace.photos.toList()
                            photoreference = photoitem[0].photo_reference
                        }
                        var imgUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$photoreference&sensor=false&key=${context.resources.getString(R.string.google_maps_key)}"
//                        var restaurantData: RestaurantData = RestaurantData()
//                        restaurantData.Name = placename
//                        restaurantData.Address = address
//                        restaurantData.image = photoreference
//                        restaurantData.rating=rating
//                        restaurantData.open=open
//                        restaurantData.lat = lat
//                        restaurantData.lng = lon
//                        restaurantData.placeId=placeId
//                        list.add(restaurantData)
                        latlng2 = LatLng(lat, lon)
//                        if(googlePlace.opening_hours==null){
//                            open="NotAvailable"
//                        }else{
//                            open= googlePlace.opening_hours.open_now.toString()
//                        }
//                        var loc: Location = Location("test")
//                        loc.latitude = lat
//                        loc.longitude = lon
//                        RestaurantPresenterImpl.hashMap[placename] = loc
//                        Log.i("LatLng nearbyplaces", lat.toString() + "  " + lon)
//                        if (googlePlace.photos == null) {
//                            photoreference = "NotAvailable"
//                        } else {
//                            photoitem = googlePlace.photos.toList()
//                            photoreference = photoitem[0].photo_reference
//                        }

                        var infoWindowPojo: InfoWindowPojo = InfoWindowPojo()
                        infoWindowPojo.Name = placename
                        infoWindowPojo.Address = address
                        infoWindowPojo.openStatus = open
                        infoWindowPojo.ratings = rating
                        infoWindowPojo.image = imgUrl

                        var customInfoWindow: CustomInfoWindow = CustomInfoWindow(context)
                        mMap.setInfoWindowAdapter(customInfoWindow)
                        // infoWindowPojo.timings=

                        markerOptions.position(latlng2)
                        markerOptions.title(placename)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        restaurantMarker = mMap.addMarker(markerOptions)
                        restaurantMarker!!.tag = infoWindowPojo
//                        restaurantMarker!!.showInfoWindow()


                    }
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11.0f))
                    Log.i("Total places", list.size.toString())


                } else {

                    Log.i("List not found", "Trying again")
                }
            }


        })
    }

    override fun favRestaurants(context: Context, typeplace: String, location: Location, iGoogleApiServices: IGoogleApiServices, mMap: GoogleMap) {
        mydb = Room.databaseBuilder(context, Mydatabase::class.java, "Database").allowMainThreadQueries().build()
        var sharedpref: SharedPreferences = context.getSharedPreferences("UserInfo", 0)
        var Username = sharedpref.getString("username", null)
        var uid = mydb.myDao().getUserId(Username!!)
        var list: List<FavoritesTable> = mydb.myDao().getFavorites(uid)

        var favorite_list: ArrayList<RestaurantData> = ArrayList()
        if (list.isNotEmpty()) {
            for (i in list.indices) {
                val markerOptions: MarkerOptions = MarkerOptions()
                var latitude = list[i].lat
                var longitude = list[i].lng
                var name = list[i].restaurantName
                var address = list[i].restaurantAddress
                var open = list[i].openStatus
                var rating = list[i].ratings


                var photoreference = list[i].restaurantPhoto
                var imgUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$photoreference&sensor=false&key=${context.resources.getString(R.string.google_maps_key)}"
                var latLng: LatLng = LatLng(latitude!!, longitude!!)
                var infoWindowPojo: InfoWindowPojo = InfoWindowPojo()
                infoWindowPojo.Name = name
                infoWindowPojo.Address = address
                infoWindowPojo.openStatus = open
                infoWindowPojo.ratings = rating
                infoWindowPojo.image = imgUrl

                var customInfoWindow: CustomInfoWindow = CustomInfoWindow(context)
                mMap.setInfoWindowAdapter(customInfoWindow)
                markerOptions.position(latLng)
                markerOptions.title(name)
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                restaurantMarker = mMap.addMarker(markerOptions)
                restaurantMarker!!.tag = infoWindowPojo
//                 restaurantMarker!!.showInfoWindow()
            }
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11.0f))
            Log.i("Total places", list.size.toString())
        } else {
            Log.i("List not found", "Trying again")
        }

    }


    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.i("OnConnectionFailed", "failed")
    }


    lateinit var iGoogleApiServices: IGoogleApiServices
    var pojo: POJO = POJO()

    lateinit var locationReq: LocationRequest
    lateinit var locationCallback: LocationCallback

    var latlng: LatLng? = null
    var mCount: Int = 1

    var GEOFENCE_ID_STAN_UNI = "My_Location"
    var list: ArrayList<RestaurantData> = ArrayList()
    var count: Int = 0
    var GEOFENCE_RADIUS_IN_METERS: Int = 1000
    lateinit var mydb: Mydatabase

    companion object {
        val AREA_LANDMARKS: HashMap<String, LatLng> = HashMap<String, LatLng>()
        var loc: Location? = null
        var myLocation: Marker? = null
        var restaurantMarker: Marker? = null
    }

    override fun BuildLocationreq(): LocationRequest {
        locationReq = LocationRequest()
        locationReq.interval = 30000
        Log.i("Called", "in onconnected")
        locationReq.fastestInterval = 5000
        locationReq.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        return locationReq
    }


    @SuppressLint("MissingPermission")
    override fun createClient(context: Context): GoogleApiClient {
        lateinit var gClient: GoogleApiClient

        return gClient

    }


    override fun checklocationpermission(context: Activity): Boolean {
        return if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(context, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(context, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 99)
            } else {
                ActivityCompat.requestPermissions(context, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 99)
            }
            false

        } else
            true

    }

    fun geturl(lat: Double, lng: Double, nearbyplace: String): String {

        var googleplaceurl: StringBuilder = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
        googleplaceurl.append("location=" + lat + "," + lng)
        googleplaceurl.append("&radius=" + 2000)
        googleplaceurl.append("&type=" + nearbyplace)
        googleplaceurl.append("&sensor=true")
        googleplaceurl.append("&key=" + "AIzaSyD7O4Q5UsRLWuP1P17WSDEuHttwKAcoSis")

        return googleplaceurl.toString()
    }


    override fun Buildlocationcallback(iGoogleApiServices: IGoogleApiServices, activity: Context): LocationCallback {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                loc = p0!!.locations[p0.locations.size - 1]
                Log.i("Location", "$loc")
                latlng = LatLng(loc!!.latitude, loc!!.longitude)
                if (myLocation != null) {
                    myLocation!!.remove()
                }
                AREA_LANDMARKS[GEOFENCE_ID_STAN_UNI] = latlng!!
                val geocoder: Geocoder = Geocoder(activity, Locale.getDefault())
                var address = geocoder.getFromLocation(latlng!!.latitude, latlng!!.longitude, 1)
                val city = address[0].locality
                val knownName = address[0].featureName
                var infoWindowPojo: InfoWindowPojo = InfoWindowPojo()
                infoWindowPojo.Name = "My Location"
                infoWindowPojo.Address = "$city $knownName"
                infoWindowPojo.openStatus = "NotAvailable"
                infoWindowPojo.ratings = 14.0f.toDouble()
                infoWindowPojo.image = " "

                var customInfoWindow: CustomInfoWindow = CustomInfoWindow(activity)
                mMap!!.setInfoWindowAdapter(customInfoWindow)
                var markerOptions: MarkerOptions = MarkerOptions()
                markerOptions.position(latlng!!)
                markerOptions.title("My Location")
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                myLocation = mMap!!.addMarker(markerOptions)
                myLocation!!.tag = infoWindowPojo
                Log.i("Move with $count", "animated with location $loc")
                if (mCount == 1) {
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latlng))
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latlng))
                    mCount++
                }


                if (loc != null) {
                    count++
                    if (count == 1) {

                        var mySharedPreferences: SharedPreferences = activity.getSharedPreferences("RestaurantsOnMaps", android.content.Context.MODE_PRIVATE)
                        var whatToShow = mySharedPreferences.getString("WhatToShow", null)
                        if (whatToShow == "all") {
                            nearbyplaces2(activity, "restaurant", loc!!, iGoogleApiServices, MapsFragment.mMap!!)
                        }
                        if (whatToShow == "fav") {
                            favRestaurants(activity, "restaurant", loc!!, iGoogleApiServices, MapsFragment.mMap!!)
                        }




                        Log.i("Count success", "$count")
                    }


                }
            }
        }

        return locationCallback


    }


}