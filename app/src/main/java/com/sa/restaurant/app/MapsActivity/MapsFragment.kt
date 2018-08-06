package com.sa.restaurant.app.MapsActivity


import android.annotation.SuppressLint
import android.app.Fragment
import android.app.PendingIntent
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker
import com.sa.restaurant.R
import com.sa.restaurant.adapters.GeofenceTransitionsIntentService
import com.sa.restaurant.app.MapsActivity.Weather.Model.PojoContext
import com.sa.restaurant.app.RestaurantsActivity.IGoogleApiServices
import com.sa.restaurant.app.RestaurantsActivity.RestaurantActivity
import com.sa.restaurant.app.RestaurantsActivity.RetrofitnearbyClient
import com.sa.restaurant.app.RestaurantsActivity.presenter.MapsPresenter
import com.sa.restaurant.app.RestaurantsActivity.presenter.MapsPresenterImpl
import com.sa.restaurant.app.RestaurantsActivity.presenter.RestaurantPresenterImpl
import com.sa.restaurant.app.roomDatabase.FavoritesTable
import com.sa.restaurant.app.roomDatabase.Mydatabase
import kotlinx.android.synthetic.main.fragment_maps.*


/**
 * Maps Fragment class on which the location of restaurants are located via google maps
 * Created On :- 25 july 2018
 * Created by :- jay.ghodasara
 */

class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    companion object {
        var mMap: GoogleMap? = null
        lateinit var gclient: GoogleApiClient
    }

    lateinit var contextt: Context
    var mMapView: MapView? = null
    lateinit var iGoogleApiServices: IGoogleApiServices
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationreq: LocationRequest
    lateinit var locationcallback: LocationCallback
    var GEOFENCE_RADIUS_IN_METERS: Int = 1000
    lateinit var mydb: Mydatabase


    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        mMap!!.setOnMarkerClickListener(this)
        Log.i("Map", "Ready")
        mydb = Room.databaseBuilder(activity, Mydatabase::class.java, "Database").allowMainThreadQueries().build()
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap!!.isMyLocationEnabled = true


            RestaurantActivity.dialog.dismiss()


        }
    }

    //creating client for adding geofences.
    fun callgeofence(context: Context) {
        contextt = context
        // RestaurantActivity.googleClient!!.reconnect()
        synchronized(this) {
            Log.i("Client", "created")
            gclient = GoogleApiClient.Builder(context).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build()
            gclient.connect()
            mydb = Room.databaseBuilder(context, Mydatabase::class.java, "Database").allowMainThreadQueries().build()

        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        p0!!.showInfoWindow()

        return true
    }

    //Creating geofence via lat lng of favorite restaurants
    fun geoFencingReq(lat: Double, lng: Double, placename: String): GeofencingRequest {
        var builder: GeofencingRequest.Builder = GeofencingRequest.Builder()
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        builder.addGeofence(getGeofence(lat, lng, placename))
        return builder.build()
    }


    fun getGeofence(latitude: Double, longitude: Double, placename: String): Geofence? {


        var geofence: Geofence = Geofence.Builder()
                .setRequestId(placename)
                .setCircularRegion(latitude, longitude, GEOFENCE_RADIUS_IN_METERS.toFloat())
                .setNotificationResponsiveness(1)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(1)
                .build()
        return geofence

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


           var view: View = inflater.inflate(R.layout.fragment_maps, container, false)

        mMapView = view.findViewById(R.id.map)

        mMapView!!.onCreate(savedInstanceState)
        mMapView!!.onResume()

        try {
            MapsInitializer.initialize(activity.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mMapView!!.getMapAsync(this)

        iGoogleApiServices = RetrofitnearbyClient.getClient("https://maps.google.com/").create(IGoogleApiServices::class.java)
        var mapsPresenter: MapsPresenter = MapsPresenterImpl()
        locationreq = mapsPresenter.BuildLocationreq()
        locationcallback = mapsPresenter.Buildlocationcallback(iGoogleApiServices, activity)

        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
            fusedLocationProviderClient.requestLocationUpdates(locationreq, locationcallback, Looper.myLooper())
        }

        return view
    }


    override fun onResume() {
        super.onResume()
        map.onResume()
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

    }

    var PROX_ALERT_INTENT = "com.sa.restaurant.proximity"
    var intent2: Intent = Intent(PROX_ALERT_INTENT)

    //Pending intent that will be triggered when the geofence transition occurs
    val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(contextt, GeofenceTransitionsIntentService::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        PendingIntent.getService(contextt, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    }

    //after the client is connected the following code executes and attaches pending intent to geofence
    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {
        Log.i("gClient", "connected")
        var pojo: PojoContext = PojoContext()


        var sharedpref: SharedPreferences = contextt.getSharedPreferences("UserInfo", 0)
        var Username = sharedpref.getString("username", null)
        var uid = mydb.myDao().getUserId(Username!!)
        var list: List<FavoritesTable> = mydb.myDao().getFavorites(uid)



        for (l in list.indices) {
            var name: String = list[l].restaurantName.toString()
            var location = RestaurantPresenterImpl.hashMap[name]

            var geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(contextt)
            Log.i("Location", location.toString())
            if (location != null) {
                LocationServices.GeofencingApi.addGeofences(gclient, geoFencingReq(location!!.latitude, location.longitude, name), geofencePendingIntent)
                        .setResultCallback(object : ResultCallback<Status> {
                            override fun onResult(p0: Status) {
                                Log.i("Geofence", "Added")
                            }
                        })
            } else {

            }

        }
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.i("Connection", "Suspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.i("OOps", "Connection Failed")
    }


}
