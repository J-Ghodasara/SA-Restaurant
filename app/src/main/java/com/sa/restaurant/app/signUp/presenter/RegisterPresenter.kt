package com.sa.restaurant.app.signUp.presenter

import android.app.Activity
import android.app.FragmentManager
import android.content.Context
import android.view.View
import com.sa.restaurant.app.roomDatabase.Mydatabase

/**
 * RegisterPresenter interface
 * Created On :- 23 july 2018
 * Created by :- jay.ghodasara
 */

interface RegisterPresenter {

    fun checkforuser(name: String, email: String, number: String, password: String, mydatabase: Mydatabase, v: Activity, fragmentmanager: FragmentManager)
}