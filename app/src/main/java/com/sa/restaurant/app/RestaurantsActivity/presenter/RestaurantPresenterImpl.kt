package com.sa.restaurant.app.RestaurantsActivity.presenter

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.sa.restaurant.adapters.RestaurantAdapter
import com.sa.restaurant.app.RestaurantsActivity.IGoogleApiServices
import com.sa.restaurant.app.RestaurantsActivity.RestaurantActivity
import com.sa.restaurant.app.RestaurantsActivity.model.POJO
import com.sa.restaurant.app.RestaurantsActivity.model.PhotosItem
import com.sa.restaurant.app.RestaurantsActivity.model.RestaurantData
import com.sa.restaurant.app.RestaurantsActivity.view.RestaurantView
import com.sa.restaurant.utils.Toastutils
import retrofit2.Call
import retrofit2.Response

class RestaurantPresenterImpl : RestaurantPresenter, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.i("OnConnectionFailed", "failed")
    }

    override fun onConnected(p0: Bundle?) {
        Log.i("OnConnected", "success")
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    lateinit var iGoogleApiServices: IGoogleApiServices
    var pojo: POJO = POJO()

    lateinit var locationReq: LocationRequest
    lateinit var locationCallback: LocationCallback

    var latlng: LatLng? = null
    var GEOFENCE_ID_STAN_UNI = "My_Location"
    var list: ArrayList<RestaurantData> = ArrayList()
    var count: Int = 0

    companion object {
        val AREA_LANDMARKS: HashMap<String, LatLng> = HashMap<String, LatLng>()
        lateinit var loc: Location
        lateinit var gClient: GoogleApiClient
        var hashMap: HashMap<String, Location> = HashMap()
    }

    override fun BuildLocationreq(): LocationRequest {
        locationReq = LocationRequest()
        locationReq.interval = 60000
        Log.i("Called", "in onconnected")
        locationReq.fastestInterval = 5000
        locationReq.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        return locationReq
    }


    fun nearbyplaces(context: Context, typeplace: String, location: Location, iGoogleApiServices: IGoogleApiServices, restaurantadapter: RestaurantAdapter,recyclerView: RecyclerView) {

        val url = geturl(location.latitude, location.longitude, typeplace)
        iGoogleApiServices.getnearbyplaces(url).enqueue(object : retrofit2.Callback<POJO> {
            override fun onFailure(call: Call<POJO>?, t: Throwable?) {
                Toastutils.showToast(context, "Failed")
                Log.i("ResponseNearbyPlaces", "failed")
            }

            override fun onResponse(call: Call<POJO>?, response: Response<POJO>?) {
                var photoitem: List<PhotosItem> = ArrayList()

                pojo = response!!.body()!!
                Log.i("ResponseNearbyPlaces", response.body()!!.results.toString())
                //  var latlng2: LatLng? = null
                var open:String?=null
                var photoreference: String
                if (response!!.body()!! != null) {
                    for (i in 0 until response.body()!!.results!!.size) {

                        val googlePlace = response.body()!!.results!![i]
                        val address = response.body()!!.results!![i].vicinity
                        val placename = googlePlace.name
                        val lat = googlePlace.geometry.location.lat
                        val lng = googlePlace.geometry.location.lng
                        val rating = googlePlace.rating
                        val placeId:String=googlePlace.place_id
                        if(googlePlace.opening_hours==null){
                            open="NotAvailable"
                        }else{
                            open= googlePlace.opening_hours.open_now.toString()
                        }
                        var loc: Location = Location("test")
                        loc.latitude = lat
                        loc.longitude = lng
                        hashMap[placename] = loc
                        Log.i("LatLng nearbyplaces", lat.toString() + "  " + lng)
                        if (googlePlace.photos == null) {
                            photoreference = "NotAvailable"
                        } else {
                            photoitem = googlePlace.photos.toList()
                            photoreference = photoitem[0].photo_reference
                        }


                        var restaurantData: RestaurantData = RestaurantData()
                        restaurantData.Name = placename
                        restaurantData.Address = address
                        restaurantData.image = photoreference
                        restaurantData.rating=rating
                        restaurantData.open=open
                        restaurantData.lat = lat
                        restaurantData.lng = lng
                        restaurantData.placeId=placeId
                        list.add(restaurantData)
                        RestaurantActivity.mcount = 0
                        //  latlng2 = LatLng(lat, lng)




                    }

                    Log.i("Total places", list.size.toString())
                    var restaurantView: RestaurantView = RestaurantActivity()
                    restaurantView.restaurantslist(list, context, restaurantadapter,recyclerView)



                } else {

                    Log.i("List not found", "Trying again")
                }
            }


        })

    }

    override fun createClient(context: Context): GoogleApiClient {

        synchronized(this) {
            Log.i("Restaurant Client", "created")
            gClient = GoogleApiClient.Builder(context).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build()
            gClient.connect()
            return gClient
        }
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


    override fun Buildlocationcallback(iGoogleApiServices: IGoogleApiServices, context: ViewGroup, activity: Context, adapter: RestaurantAdapter,recyclerView: RecyclerView): LocationCallback {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                loc = p0!!.locations[p0.locations.size - 1]

                latlng = LatLng(loc.latitude, loc.longitude)

                AREA_LANDMARKS[GEOFENCE_ID_STAN_UNI] = latlng!!
                if (loc != null) {
                    count++
                    if (count == 1) {

                        nearbyplaces(activity, "restaurant", loc, iGoogleApiServices, adapter,recyclerView)


                        Log.i("Count success", "$count")
                    }

                    if (RestaurantActivity.mcount == 1) {
                        RestaurantActivity.mcount = 0
                        list.clear()
                        Log.i("cleared list", list.size.toString())
                        nearbyplaces(activity, "restaurant", loc, iGoogleApiServices, adapter,recyclerView)
                    }

                }


                Log.i("Move with $count", "animated with location $loc")


            }
        }

        return locationCallback


    }


}