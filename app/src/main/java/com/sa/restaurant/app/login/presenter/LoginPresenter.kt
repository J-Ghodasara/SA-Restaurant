package com.sa.restaurant.app.login.presenter

import android.app.Activity
import android.app.FragmentManager
import com.sa.restaurant.app.roomDatabase.Mydatabase

/**
 * Login presenter
 * created by:- jay.ghodasara
 * created on:- 19 july 18
 */


interface LoginPresenter{

    fun validateUser(username:String,password:String,mydatabase: Mydatabase,activity: Activity):Boolean
}