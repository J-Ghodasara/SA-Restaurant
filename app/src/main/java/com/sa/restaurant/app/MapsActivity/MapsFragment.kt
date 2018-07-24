package com.sa.restaurant.app.MapsActivity


import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.app.Fragment
import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker
import com.sa.restaurant.R
import com.sa.restaurant.app.RestaurantsActivity.IGoogleApiServices
import com.sa.restaurant.app.RestaurantsActivity.RestaurantActivity
import com.sa.restaurant.app.RestaurantsActivity.RetrofitnearbyClient
import com.sa.restaurant.app.RestaurantsActivity.presenter.MapsPresenter
import com.sa.restaurant.app.RestaurantsActivity.presenter.MapsPresenterImpl
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import kotlinx.android.synthetic.main.fragment_maps.*


/**
 * A simple [Fragment] subclass.
 *
 */
class MapsFragment : Fragment(), OnMapReadyCallback,GoogleMap.OnMarkerClickListener {
    override fun onMarkerClick(p0: Marker?): Boolean {
        p0!!.showInfoWindow()

//when{
//    p0!! == MapsPresenterImpl.myLocation -> {
//        MapsPresenterImpl.myLocation!!.showInfoWindow()
//        return true
//    }
//    p0 == MapsPresenterImpl.RestaurantMarker -> {
//
//        return true
//    }
//    else -> {
//        return true
//    }
//}
return true
    }

    companion object {
          var mMap: GoogleMap?=null
    }

    var mMapView: MapView? = null
    lateinit var iGoogleApiServices: IGoogleApiServices
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationreq:LocationRequest
    lateinit var locationcallback:LocationCallback

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        mMap!!.setOnMarkerClickListener(this)
     Log.i("Map","Ready")

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

         var mapsPresenter:MapsPresenter=MapsPresenterImpl()
            mapsPresenter.createClient(activity)
            mMap!!.isMyLocationEnabled = true
           }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment



       // var layoutInflater:LayoutInflater= activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view:View= inflater.inflate(R.layout.fragment_maps, container, false)

        mMapView= view.findViewById(R.id.map)

        mMapView!!.onCreate(savedInstanceState)
        mMapView!!.onResume()

        try{
            MapsInitializer.initialize(activity.applicationContext)
        }catch (e:Exception ) {
            e.printStackTrace()
        }

        mMapView!!.getMapAsync(this)

        iGoogleApiServices = RetrofitnearbyClient.getClient("https://maps.google.com/").create(IGoogleApiServices::class.java)
        var mapsPresenter:MapsPresenter=MapsPresenterImpl()
        locationreq = mapsPresenter.BuildLocationreq()
       locationcallback = mapsPresenter.Buildlocationcallback(iGoogleApiServices,activity)

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


}
