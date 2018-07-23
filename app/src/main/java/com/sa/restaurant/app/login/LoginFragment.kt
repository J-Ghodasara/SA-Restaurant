package com.sa.restaurant.app.login


import android.app.Activity
import android.os.Bundle
import android.app.Fragment
import android.app.FragmentManager
import android.arch.persistence.room.Room
import android.content.Intent
import android.content.SharedPreferences
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sa.restaurant.MainActivity
import com.sa.restaurant.R
import com.sa.restaurant.app.RestaurantsActivity.RestaurantActivity
import com.sa.restaurant.app.login.presenter.LoginPresenter
import com.sa.restaurant.app.login.presenter.LoginPresenterImpl
import com.sa.restaurant.app.login.view.LoginView
import com.sa.restaurant.app.roomDatabase.Mydatabase
import com.sa.restaurant.app.signUp.RegisterFragment
import com.sa.restaurant.communicate
import com.sa.restaurant.utils.Fragmentutils
import com.sa.restaurant.utils.Toastutils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*



/**
 * Login fragment
 * Created On :- 19 july 2018
 * Created by :- jay.ghodasara
 */

class LoginFragment : Fragment(), View.OnClickListener,LoginView {

lateinit var loginpresenter: LoginPresenter
    lateinit var mydb:Mydatabase
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view:View= inflater.inflate(R.layout.fragment_login, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mydb= Room.databaseBuilder(activity, Mydatabase::class.java,"Database").allowMainThreadQueries().build()
        textview_Register.setOnClickListener(this)
        btn_login.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when(v!!.id){

            R.id.textview_Register ->{
            var registerFragment:RegisterFragment = RegisterFragment()
                Fragmentutils.replaceFragment(activity,registerFragment,fragmentManager,R.id.container)
            }

            R.id.btn_login ->{

                var username:String=editText_Username.text.toString()
                var password:String=editText_Password.text.toString()
                  loginpresenter=LoginPresenterImpl()
               var issucess:Boolean= loginpresenter.validateUser(username,password,mydb,activity)

                if(issucess){
                    var intent:Intent= Intent(activity,RestaurantActivity::class.java)
                    startActivity(intent)
                }
            }

        }
    }

    override fun validuser(v: Activity, username: String?, email: String?, Number: String?) {
        Toastutils.showsSnackBar(v,"Login Successfull")
        var sharedpref:SharedPreferences= v.getSharedPreferences("UserInfo",0)
        var editor:SharedPreferences.Editor= sharedpref.edit()
        editor.putString("username",username)
        editor.putString("email",email)
        editor.putString("number",Number)
        editor.apply()


//        var loginfrag:LoginFragment=LoginFragment()
//        var intent:Intent= Intent(v,RestaurantActivity::class.java)
//        startActivity(intent)

//        var communicate:communicate=MainActivity()
//        communicate.loginsuccessfull()

    }
}
