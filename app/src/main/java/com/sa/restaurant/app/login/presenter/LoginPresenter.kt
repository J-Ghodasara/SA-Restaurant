package com.sa.restaurant.app.login.presenter

import android.app.Activity
import android.app.FragmentManager
import com.sa.restaurant.app.roomDatabase.Mydatabase

/**
 * Login presenter
 *
 */


interface LoginPresenter{

    fun validateUser(username:String,password:String,mydatabase: Mydatabase,activity: Activity):Boolean
}