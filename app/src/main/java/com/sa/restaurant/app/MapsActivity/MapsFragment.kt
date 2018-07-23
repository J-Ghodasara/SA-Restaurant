package com.sa.restaurant.app.MapsActivity


import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker

import com.sa.restaurant.R
import com.sa.restaurant.app.RestaurantsActivity.IGoogleApiServices
import com.sa.restaurant.app.RestaurantsActivity.RestaurantActivity
import com.sa.restaurant.app.RestaurantsActivity.RetrofitnearbyClient


/**
 * A simple [Fragment] subclass.
 *
 */
class MapsFragment : Fragment(), OnMapReadyCallback,GoogleMap.OnMarkerClickListener {
    override fun onMarkerClick(p0: Marker?): Boolean {
      return true
    }

    companion object {
        private lateinit var mMap: GoogleMap
    }

    lateinit var iGoogleApiServices: IGoogleApiServices
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        mMap.setOnMarkerClickListener(this)
        Toast.makeText(context, "OnMapReady", Toast.LENGTH_LONG).show()

        if (ActivityCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            RestaurantActivity.googleClient
            mMap.isMyLocationEnabled = true


        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view:View= inflater.inflate(R.layout.fragment_maps, container, false)

        iGoogleApiServices = RetrofitnearbyClient.getClient("https://maps.google.com/").create(IGoogleApiServices::class.java)
        if (ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)
            fusedLocationProviderClient.requestLocationUpdates(RestaurantActivity.locationreq, RestaurantActivity.locationcallback, Looper.myLooper())
        }

    return view
    }


}
