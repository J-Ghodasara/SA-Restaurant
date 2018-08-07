package com.sa.restaurant

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.sa.restaurant.app.RestaurantsActivity.RestaurantActivity
import com.sa.restaurant.app.RestaurantsActivity.presenter.RestaurantPresenter
import com.sa.restaurant.app.RestaurantsActivity.presenter.RestaurantPresenterImpl
import com.sa.restaurant.utils.Toastutils
import kotlinx.android.synthetic.main.activity_error.*

/**
 * Error activity if location permission is not granted this activity will be presented
 * Created On :- 4 aug 2018
 * Created by :- jay.ghodasara
 */


class ErrorActivity : AppCompatActivity() {

    var firstTime: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)


        val mPreferences = this.getSharedPreferences("first_time", Context.MODE_PRIVATE)
        firstTime = mPreferences.getBoolean("firstTime", true)
        if (firstTime as Boolean) {
            val editor = mPreferences.edit()
            editor.putBoolean("firstTime", false)
            editor.apply()
        }
        permission.setOnClickListener(View.OnClickListener {


            checklocationpermission(this)

        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {

            99 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    MainActivity.isPermissionGranted = true
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toastutils.showToast(this, "Permission Granted")
                        var sharedPreferences = getSharedPreferences("permissionGranted", Context.MODE_PRIVATE)
                        var editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putBoolean("permission", true)
                        editor.apply()
                        var intent: Intent = Intent(this, RestaurantActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
//                        mMap.isMyLocationEnabled = true
                    }
                } else {
                    var sharedPreferences = getSharedPreferences("permissionGranted", Context.MODE_PRIVATE)
                    var editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putBoolean("permission", true)
                    editor.apply()
                    Toast.makeText(applicationContext, " Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

//    override fun onBackPressed() {
//        finish()
//    }


    fun checklocationpermission(context: Activity): Boolean {
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
}
