package com.sa.restaurant

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toolbar
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.sa.restaurant.app.RestaurantsActivity.RestaurantActivity
import com.sa.restaurant.app.login.LoginFragment
import com.sa.restaurant.app.signUp.view.RegisterView
import com.sa.restaurant.utils.Fragmentutils
import android.R.attr.data
import android.app.Fragment
import android.util.Log
import android.R.attr.data
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.sa.restaurant.adapters.GeofenceTransitionsIntentService
import com.sa.restaurant.app.RestaurantsActivity.presenter.RestaurantPresenter
import com.sa.restaurant.app.RestaurantsActivity.presenter.RestaurantPresenterImpl
import com.sa.restaurant.utils.Toastutils


/**
 * Landing class for login and register fragment
 * Created On :- 19 july 2018
 * Created by :- jay.ghodasara
 */

class MainActivity : AppCompatActivity(), communicate {
    companion object {
        var isVisible: Boolean = false
        var permissionCount:Int = 0
        var isPermissionGranted:Boolean=false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(FacebookSdk.getApplicationContext())
        AppEventsLogger.activateApp(this)
        setContentView(R.layout.activity_main)


        if (isVisible) {

        } else {
            var loginFragment: LoginFragment = LoginFragment()
            Fragmentutils.removeFragment(loginFragment, fragmentManager)

            isVisible = true
            Fragmentutils.addFragment(this, loginFragment, fragmentManager, R.id.container)
        }

        val geofencePendingIntent: PendingIntent by lazy {
            val intent = Intent(this, GeofenceTransitionsIntentService::class.java)
            // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
            // addGeofences() and removeGeofences().
            PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        }





        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionCount==0) {
            permissionCount++
            var restaurantPresenter: RestaurantPresenter = RestaurantPresenterImpl()
            restaurantPresenter.checklocationpermission(this)
        }

    }

    override fun onBackPressed() {
        isVisible = false
        finishAffinity()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {

            99 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    var sharedPreferences:SharedPreferences=getSharedPreferences("permissionGranted", Context.MODE_PRIVATE)
                    var edit:SharedPreferences.Editor = sharedPreferences.edit()
                    edit.putBoolean("permission",true)
                    edit.apply()
                    isPermissionGranted=true
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toastutils.showToast(this, "Permission Granted")
//                        mMap.isMyLocationEnabled = true
                    }
                } else {
                    var sharedPreferences:SharedPreferences=getSharedPreferences("permissionGranted", Context.MODE_PRIVATE)
                    var edit:SharedPreferences.Editor = sharedPreferences.edit()
                    edit.putBoolean("permission",false)
                    edit.apply()
                    isPermissionGranted=false
                    Toast.makeText(applicationContext, " Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val fragment = fragmentManager.findFragmentById(R.id.container)
        fragment.onActivityResult(requestCode, resultCode, data)

    }

    override fun loginsuccessfull() {


    }
}
