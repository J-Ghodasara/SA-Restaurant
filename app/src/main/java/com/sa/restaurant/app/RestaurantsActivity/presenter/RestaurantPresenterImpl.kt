package com.sa.restaurant.app.RestaurantsActivity.presenter

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.ViewGroup
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.sa.restaurant.adapters.restaurantadapter
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

    }

    override fun BuildLocationreq(): LocationRequest {
        locationReq = LocationRequest()
        locationReq.interval = 30000
        Log.i("Called", "in onconnected")
        locationReq.fastestInterval = 5000
        locationReq.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        return locationReq
    }


     fun nearbyplaces(context: Context, typeplace: String, location: Location, iGoogleApiServices: IGoogleApiServices,restaurantadapter: restaurantadapter) {

        val url = geturl(location.latitude, location.longitude, typeplace)
        iGoogleApiServices.getnearbyplaces(url).enqueue(object : retrofit2.Callback<POJO> {
            override fun onFailure(call: Call<POJO>?, t: Throwable?) {
                Toastutils.showToast(context, "Failed")
            }

            override fun onResponse(call: Call<POJO>?, response: Response<POJO>?) {
                var photoitem: List<PhotosItem> = ArrayList()
                pojo = response!!.body()!!
                Log.i("Response",response.body()!!.results.toString())
                //  var latlng2: LatLng? = null
                var photoreference:String
                if (response!!.body()!! != null) {
                    for (i in 0 until response.body()!!.results!!.size) {
//                        val markerOptions: MarkerOptions = MarkerOptions()
                        val googlePlace = response.body()!!.results!![i]
                        val address= response.body()!!.results!![i].vicinity
                        val placename = googlePlace.name
                        if(googlePlace.photos==null){
                            photoreference="CmRaAAAARs96HXjLZFkFS1Nzb2FfsTnesaYVp-lGptxA3o-rLDlNgZJqjpse57PIB42_tUQnErkBkuWEcJMTSKBScC5eYrzLA3s4Pt8MihxpMD3gLi_7zOxD9i2-fxxOp7v9fs_pEhC7cZWc4cvi5UmJO1_IyOYsGhR2X0rUzKq54WzXiAsdUVFZwBQpHw"
                        }else{
                            photoitem = googlePlace.photos.toList()
                            photoreference = photoitem[0].photo_reference
                        }


                        var restaurantData:RestaurantData=RestaurantData()
                        restaurantData.Name=placename
                        restaurantData.Address=address
                        restaurantData.image=photoreference
                        list.add(restaurantData)
                        //  latlng2 = LatLng(lat, lng)


//                        markerOptions.position(latlng2)
//                        markerOptions.title(placename)
//                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//                        mMap.addMarker(markerOptions)

                    }

                    Log.i("Total places", list.size.toString())
                    var restaurantView:RestaurantView= RestaurantActivity()
                    restaurantView.restaurantslist(list,context,restaurantadapter)



                    //mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng2))
                }else{
                    nearbyplaces(context,typeplace,location,iGoogleApiServices,restaurantadapter)
                    Log.i("List not found","Trying again")
                }
            }


        })

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
        googleplaceurl.append("&key=" + "AIzaSyB0_n9UBObCELuk4pLP8XL1kIKghrPNdks")

        return googleplaceurl.toString()
    }


    override fun Buildlocationcallback(iGoogleApiServices: IGoogleApiServices, context: ViewGroup, activity: Context, adapter: restaurantadapter): LocationCallback {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                loc = p0!!.locations[p0.locations.size - 1]

                latlng = LatLng(loc.latitude, loc.longitude)
//                if (myLocation != null) {
//                    myLocation!!.remove()
//                }
                AREA_LANDMARKS[GEOFENCE_ID_STAN_UNI] = latlng!!
        if(loc != null){
            count++
            if(count==1){
               // var RestaurantView: RestaurantView = RestaurantActivity()
             //   var restaurantPresenter: RestaurantPresenter = RestaurantPresenterImpl()
               nearbyplaces(activity, "restaurant", loc, iGoogleApiServices,adapter)

              //  RestaurantView.getcurrentlatlng(loc, iGoogleApiServices, context, activity, adapter)
                  Log.i("Count success","$count")
            }

        }

//                var markerOptions: MarkerOptions = MarkerOptions()
//                markerOptions.position(latlng!!)
//                markerOptions.title("My Location")
//                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                // myLocation = mMap.addMarker(markerOptions)
                Log.i("Move with $count", "animated with location $loc")
//                if (mCount == 1) {
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
//                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng))
//                    mCount++
//                }

            }
        }

        return locationCallback


    }


}