package com.sa.restaurant.utils

import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.Context
import com.sa.restaurant.R
import kotlinx.android.synthetic.main.activity_main.*

object Fragmentutils {

    fun replaceFragment(context: Context, fragment: Fragment, fragmentManager: FragmentManager, id: Int) {

        val fm: FragmentManager = fragmentManager
        val fragmentTransaction: FragmentTransaction = fm.beginTransaction()
        fragmentTransaction.replace(id, fragment).commit()

    }

    fun addFragment(context: Context, fragment: Fragment, fragmentManager: FragmentManager, id: Int) {
        val fm: FragmentManager = fragmentManager
        val fragmentTransaction: FragmentTransaction = fm.beginTransaction()
        fragmentTransaction.add(id, fragment).commit()
    }

    fun removeFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        val fm: FragmentManager = fragmentManager
        val fragmentTransaction: FragmentTransaction = fm.beginTransaction()
        fragmentTransaction.remove(fragment).commit()
    }

    fun addFragmentwithBackStack(context: Context, fragment: Fragment, fragmentManager: FragmentManager, id: Int) {
        val fm: FragmentManager = fragmentManager
        val fragmentTransaction: FragmentTransaction = fm.beginTransaction().addToBackStack(null)
        fragmentTransaction.add(id, fragment).commit()
    }

    fun replaceFragmentwithBackStack(context: Context, fragment: Fragment, fragmentManager: FragmentManager, id: Int) {
        val fm: FragmentManager = fragmentManager
        val fragmentTransaction: FragmentTransaction = fm.beginTransaction().addToBackStack(null)
        fragmentTransaction.replace(id, fragment).commit()
    }


}