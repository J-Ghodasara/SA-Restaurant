package com.sa.restaurant.utils

import android.app.Activity
import android.app.FragmentManager
import android.content.Context
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Toast

object Toastutils {

    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }


    fun showsSnackBar(v: Activity, message: String) {
        val snackview: View = v.findViewById(android.R.id.content)
        val snackbar = Snackbar
                .make(snackview, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

}