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
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.sa.restaurant.app.RestaurantsActivity.presenter.RestaurantPresenter
import com.sa.restaurant.app.RestaurantsActivity.presenter.RestaurantPresenterImpl
import com.sa.restaurant.utils.Toastutils


/**
 * Landing class for login and register fragment
 * Created On :- 19 july 2018
 * Created by :- jay.ghodasara
 */

class MainActivity : AppCompatActivity(),communicate {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(FacebookSdk.getApplicationContext())
        AppEventsLogger.activateApp(this)
        setContentView(R.layout.activity_main)
        var loginFragment:LoginFragment= LoginFragment()

//        var sharedpref: SharedPreferences = this.getSharedPreferences("applicationcontext",0)
//        var editor: SharedPreferences.Editor= sharedpref.edit()
//        editor.putString("username", applicationContext.toString())
//        editor.apply()
    //    loginFragment.arguments=
        Fragmentutils.addFragment(this,loginFragment,fragmentManager,R.id.container)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var restaurantPresenter: RestaurantPresenter = RestaurantPresenterImpl()
            restaurantPresenter.checklocationpermission(this)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {

            99 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                       Toastutils.showToast(this,"Permission Granted")
//                        mMap.isMyLocationEnabled = true
                    }
                } else {
                    Toast.makeText(applicationContext, " Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


 override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
////        for (fragment in supportFragmentManager.fragments) {
////            fragment.onActivityResult(requestCode, resultCode, data)
////            Log.d("Activity", "ON RESULT CALLED")
////        }
//            Log.i("Activity","OnActivity Result")
            val fragment = fragmentManager.findFragmentById(R.id.container)
            fragment.onActivityResult(requestCode, resultCode, data)
//
  }
    override fun loginsuccessfull() {

//        var sharedPreferences:SharedPreferences=getSharedPreferences("applicationcontext",0)
//        var context:Context= sharedPreferences.getString("username",null)
//        var intent:Intent=Intent(applicationContext,RestaurantActivity::class.java)
//        startActivity(intent)
    }
}
