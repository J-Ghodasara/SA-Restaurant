package com.sa.restaurant.utils

import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.Context
import com.sa.restaurant.R
import kotlinx.android.synthetic.main.activity_main.*

object Fragmentutils{

    fun replaceFragment(context: Context,fragment: Fragment, fragmentManager: FragmentManager,id:Int){

        var fm:FragmentManager= fragmentManager
        var fragmentTransaction:FragmentTransaction= fm.beginTransaction()
        fragmentTransaction.replace(id,fragment).commit()

    }

    fun addFragment(context: Context,fragment: Fragment, fragmentManager: FragmentManager,id:Int){
        var fm:FragmentManager= fragmentManager
        var fragmentTransaction:FragmentTransaction= fm.beginTransaction()
        fragmentTransaction.add(id,fragment).commit()
    }

    fun removeFragment(fragment:Fragment,fragmentManager: FragmentManager){
        var fm:FragmentManager= fragmentManager
        var fragmentTransaction:FragmentTransaction= fm.beginTransaction()
        fragmentTransaction.remove(fragment).commit()
    }


}