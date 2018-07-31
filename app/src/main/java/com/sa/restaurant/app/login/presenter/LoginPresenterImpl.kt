package com.sa.restaurant.app.login.presenter

import android.app.Activity
import com.sa.restaurant.app.login.LoginFragment
import com.sa.restaurant.app.roomDatabase.Mydatabase
import com.sa.restaurant.app.roomDatabase.Table
import com.sa.restaurant.utils.Toastutils

/**
 * Login presenterImpl that handles the data manipulation inside the database
 *
 */


class LoginPresenterImpl : LoginPresenter {


    override fun validateUser(username: String, password: String, mydatabase: Mydatabase, activity: Activity): Boolean {
        var result: List<Table> = mydatabase.myDao().userLogin(username, password)

        if (result.isNotEmpty()) {
            var loginView: LoginFragment = LoginFragment()
            var email = result[0].email
            var UserName = result[0].name
            var Number = result[0].mobilenumber

            var table: Table = Table()
            table.name = UserName
            table.email = email
            table.mobilenumber = Number
            table.password = result[0].password
            table.loginStatus = "yes"
            mydatabase.myDao().update(table)

            loginView.validuser(activity, UserName, email, Number, password)
            return true
        } else {
            Toastutils.showsSnackBar(activity, "Invalid Username or Password")
            return false
        }
    }
}