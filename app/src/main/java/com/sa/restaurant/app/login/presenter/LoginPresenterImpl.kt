package com.sa.restaurant.app.login.presenter

import android.app.Activity
import android.app.FragmentManager
import com.sa.restaurant.app.login.LoginFragment
import com.sa.restaurant.app.roomDatabase.Mydatabase
import com.sa.restaurant.app.roomDatabase.Table
import com.sa.restaurant.utils.Toastutils

class LoginPresenterImpl:LoginPresenter{


    override fun validateUser(username: String, password: String,mydatabase: Mydatabase,activity: Activity):Boolean {
             var result:List<Table> = mydatabase.myDao().userLogin(username,password)

        if(result.isNotEmpty()){
              var loginView:LoginFragment= LoginFragment()
              var email=result[0].email
            var UserName=result[0].name
            var Number=result[0].mobilenumber
            loginView.validuser(activity,UserName,email,Number)
            return true
        }else{
            Toastutils.showsSnackBar(activity,"Invalid Username or Password")
            return false
        }
    }
}