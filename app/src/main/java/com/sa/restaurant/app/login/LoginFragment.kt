package com.sa.restaurant.app.login


import android.app.Activity
import android.os.Bundle
import android.app.Fragment
import android.app.FragmentManager
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.facebook.*
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.ProfilePictureView
import com.sa.restaurant.MainActivity
import com.sa.restaurant.R
import com.sa.restaurant.app.RestaurantsActivity.RestaurantActivity
import com.sa.restaurant.app.login.presenter.LoginPresenter
import com.sa.restaurant.app.login.presenter.LoginPresenterImpl
import com.sa.restaurant.app.login.view.LoginView
import com.sa.restaurant.app.roomDatabase.Mydatabase
import com.sa.restaurant.app.roomDatabase.Table
import com.sa.restaurant.app.signUp.RegisterFragment
import com.sa.restaurant.communicate
import com.sa.restaurant.utils.Fragmentutils
import com.sa.restaurant.utils.Toastutils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.*


/**
 * Login fragment
 * Created On :- 19 july 2018
 * Created by :- jay.ghodasara
 */

class LoginFragment : Fragment(), View.OnClickListener,LoginView {

lateinit var loginpresenter: LoginPresenter
    lateinit var mydb:Mydatabase
    lateinit var callbackManager: CallbackManager
    private var firstTime: Boolean? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var layoutInflater:LayoutInflater= activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        var view:View= layoutInflater.inflate(R.layout.fragment_login, container, false)
        return view
    }

//    private fun isFirstTime(): Boolean {
//        if (firstTime == null) {
//            val mPreferences = activity.getSharedPreferences("first_time", Context.MODE_PRIVATE)
//            firstTime = mPreferences.getBoolean("firstTime", true)
//            if (firstTime as Boolean) {
//                val editor = mPreferences.edit()
//                editor.putBoolean("firstTime", false)
//                editor.apply()
//            }
//        }
//        return firstTime as Boolean
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        mydb= Room.databaseBuilder(activity, Mydatabase::class.java,"Database").allowMainThreadQueries().build()

//      if(isFirstTime()){
//
//      }else{
//          var sharedpref: SharedPreferences = activity.getSharedPreferences("UserInfo", 0)
//          var  Username = sharedpref.getString("username", null)
//          var  uid=  mydb.myDao().getUserId(Username!!)
//         var list= mydb.myDao().getUserdetails(uid)
//          if(list[0].loginStatus=="yes"){
//              var intent:Intent= Intent(activity,RestaurantActivity::class.java)
//              startActivity(intent)
//          }
//      }


        textview_Register.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        callbackManager = CallbackManager.Factory.create()
        login_button.loginBehavior = LoginBehavior.WEB_ONLY
        login_button.setReadPermissions(Arrays.asList("public_profile","email"))
//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"))
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                Log.i("Login","Success")

                var request: GraphRequest = GraphRequest.newMeRequest(result!!.accessToken) { `object`, response ->
                   var email=`object`.getString("email")
                    var name=`object`.getString("name")
                    //  birthday.text=`object`.getString("birthday")
                    //  gender.text=`object`.getString("gender")

                    var image:String=`object`.getString("id")
                   Log.i("FB Image",image)

                    var sharedpref:SharedPreferences= activity.getSharedPreferences("UserInfo",0)
                    var editor:SharedPreferences.Editor= sharedpref.edit()
                    editor.putString("username",name)
                    editor.putString("email",email)
                    editor.putString("number","Permission required from facebook")
                    editor.apply()
                    Toastutils.showToast(activity,"Login Successfull")
                    var intent:Intent= Intent(activity,RestaurantActivity::class.java)
                    startActivity(intent)

                }

                var bundle:Bundle= Bundle()
                bundle.putString("fields","id,name,email,gender,birthday")
                request.parameters=bundle
                request.executeAsync()

            }

            override fun onCancel() {
                Toast.makeText(activity, "Login Cancelled", Toast.LENGTH_LONG).show()
            }

            override fun onError(error: FacebookException?) {
                Toastutils.showsSnackBar(activity,"Login Failed")
            }
        })




    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i("Fragment","OnActivity Result")
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
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
//                    var  userData=  mydb.myDao().getUserInfo(username)
//                    var table:Table= Table()
//                    table.name=userData[0].name
//                    table.email=userData[0].email
//                    table.password=userData[0].password
//                    table.mobilenumber=userData[0].mobilenumber
//                    table.loginStatus="yes"
//                    mydb.myDao().update(table)
                    var intent:Intent= Intent(activity,RestaurantActivity::class.java)
                    startActivity(intent)
                }
            }

//            R.id.login_button->{
//
//                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"))
//            }

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
