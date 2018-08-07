package com.sa.restaurant.app.signUp.presenter

import android.app.Activity
import android.app.FragmentManager
import com.sa.restaurant.app.roomDatabase.Mydatabase
import com.sa.restaurant.app.roomDatabase.Table
import com.sa.restaurant.app.signUp.RegisterFragment
import com.sa.restaurant.app.signUp.view.RegisterView
import com.sa.restaurant.utils.Toastutils

/**
 * RegisterPresenterImpl Class for business Logic of Register Fragment
 * Created On :- 23 july 2018
 * Created by :- jay.ghodasara
 */



class RegisterPresenterImpl : RegisterPresenter {

    lateinit var registerView: RegisterView


    override fun checkforuser(name: String, email: String, number: String, password: String, mydatabase: Mydatabase, v: Activity, fragmentmanager: FragmentManager) {
        var result: List<Table> = mydatabase.myDao().checkuser(name, email)

        if (result.isNotEmpty()) {

            Toastutils.showsSnackBar(v, "User Already registered!! Username & Email must be Unique")


        } else {

            var table: Table = Table()
            table.name = name
            table.email = email
            table.mobilenumber = number
            table.password = password
            table.loginStatus = "no"

            mydatabase.myDao().adduser(table)

            registerView = RegisterFragment()

            registerView.showSnackBar(v, fragmentmanager)

        }

    }
}