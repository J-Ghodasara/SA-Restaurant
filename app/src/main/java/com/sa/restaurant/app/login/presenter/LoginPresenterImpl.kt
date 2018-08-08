package com.sa.restaurant.app.login.presenter

import android.app.Activity
import com.sa.restaurant.app.login.LoginFragment
import com.sa.restaurant.app.roomDatabase.Mydatabase
import com.sa.restaurant.app.roomDatabase.Table
import com.sa.restaurant.utils.Toastutils

/**
 * Login presenterImpl that handles the data manipulation inside the database
 * created by:- jay.ghodasara
 * created on:- 19 july 18
 */


class LoginPresenterImpl : LoginPresenter {


    override fun validateUser(username: String, password: String, mydatabase: Mydatabase, activity: Activity): Boolean {
        var result: List<Table> = mydatabase.myDao().userLogin(username, password)

        if (result.isNotEmpty()) {
            val loginView: LoginFragment = LoginFragment()
            val email = result[0].email
            val userName = result[0].name
            val number = result[0].mobilenumber

            val table: Table = Table()
            table.name = userName
            table.email = email
            table.mobilenumber = number
            table.password = result[0].password
            table.loginStatus = "yes"
            mydatabase.myDao().update(table)

            loginView.validuser(activity, userName, email, number, password)
            return true
        } else {
            Toastutils.showsSnackBar(activity, "Invalid Username or Password")
            return false
        }
    }
}