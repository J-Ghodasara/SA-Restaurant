package com.sa.restaurant

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toolbar
import com.sa.restaurant.app.RestaurantsActivity.RestaurantActivity
import com.sa.restaurant.app.login.LoginFragment
import com.sa.restaurant.app.signUp.view.RegisterView
import com.sa.restaurant.utils.Fragmentutils

/**
 * Landing class for login and register fragment
 * Created On :- 19 july 2018
 * Created by :- jay.ghodasara
 */

class MainActivity : AppCompatActivity(),communicate {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var loginFragment:LoginFragment= LoginFragment()

//        var sharedpref: SharedPreferences = this.getSharedPreferences("applicationcontext",0)
//        var editor: SharedPreferences.Editor= sharedpref.edit()
//        editor.putString("username", applicationContext.toString())
//        editor.apply()
    //    loginFragment.arguments=
        Fragmentutils.addFragment(this,loginFragment,fragmentManager,R.id.container)

    }

    override fun loginsuccessfull() {

//        var sharedPreferences:SharedPreferences=getSharedPreferences("applicationcontext",0)
//        var context:Context= sharedPreferences.getString("username",null)
//        var intent:Intent=Intent(applicationContext,RestaurantActivity::class.java)
//        startActivity(intent)
    }
}
