package com.sa.restaurant.app.login


import android.app.Activity
import android.app.Fragment
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginResult
import com.sa.restaurant.MainActivity
import com.sa.restaurant.R
import com.sa.restaurant.app.RestaurantsActivity.RestaurantActivity
import com.sa.restaurant.app.login.presenter.LoginPresenter
import com.sa.restaurant.app.login.presenter.LoginPresenterImpl
import com.sa.restaurant.app.login.view.LoginView
import com.sa.restaurant.app.roomDatabase.Mydatabase
import com.sa.restaurant.app.roomDatabase.Table
import com.sa.restaurant.app.signUp.RegisterFragment
import com.sa.restaurant.utils.Fragmentutils
import com.sa.restaurant.utils.Toastutils
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Login fragment
 * Created On :- 19 july 2018
 * Created by :- jay.ghodasara
 */

class LoginFragment : Fragment(), View.OnClickListener, LoginView {

    lateinit var loginpresenter: LoginPresenter
    lateinit var mydb: Mydatabase
    lateinit var callbackManager: CallbackManager
    private var firstTime: Boolean? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        MainActivity.isLoginVisible=true
        var layoutInflater: LayoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mydb = Room.databaseBuilder(activity, Mydatabase::class.java, "Database").allowMainThreadQueries().build()
        val mPreferences = activity.getSharedPreferences("first_time", Context.MODE_PRIVATE)
        var firstTime2 = mPreferences.getBoolean("firstTime", true)
        if (firstTime2) {
            Log.i("User", "first login")
        } else {
            Log.i("User", "already login")
            var sharedpref: SharedPreferences = activity.getSharedPreferences("UserInfo", 0)
            var Username = sharedpref.getString("username", null)
            var uid = mydb.myDao().getUserId(Username!!)
            var list = mydb.myDao().getUserInfo(uid)
            Log.i("Login Status", list[0].loginStatus.toString() + "  " + uid.toString() + "  " + list[0])
            if (list[0].loginStatus == "yes") {
                var intent: Intent = Intent(activity, RestaurantActivity::class.java)
                startActivity(intent)
            }
        }


        var view: View = layoutInflater.inflate(R.layout.fragment_login, container, false)
        return view
    }




    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired


//        btn_login.setOnEditorActionListener(object:TextView.OnEditorActionListener{
//            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
//                if(actionId==EditorInfo.IME_ACTION_DONE || actionId==EditorInfo.IME_ACTION_SEND) {
//                    var username: String = editText_Username.text.toString()
//                    var password: String = editText_Password.text.toString()
//
//                    if (!validatename(username)) run {
//                        if (!validateemail(username)) {
//                            method2(username)
//
//                            editText_Username.requestFocus()
//                        } else {
//                            check(username, password)
//                        }
//
//                    } else {
//                        check(username, password)
//                    }
//
//                }
//                return true
//            }
//
//        })


        textview_Register.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        callbackManager = CallbackManager.Factory.create()
        login_button.loginBehavior = LoginBehavior.WEB_ONLY
        login_button.setReadPermissions(Arrays.asList("public_profile", "email"))

        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                Log.i("Login", "Success")

                var request: GraphRequest = GraphRequest.newMeRequest(result!!.accessToken) { `object`, response ->
                    var email = `object`.getString("email")
                    var name = `object`.getString("name")


                    var image: String = `object`.getString("id")
                    Log.i("FB Image", image)

                    var sp: SharedPreferences = activity.getSharedPreferences("UserInfoforfb", 0)
                    var password = sp.getString("password", null)

                    var sharedpref: SharedPreferences = activity.getSharedPreferences("UserInfo", 0)
                    var editor: SharedPreferences.Editor = sharedpref.edit()
                    editor.putString("username", name)
                    editor.putString("email", email)
                    editor.putString("number", "Permission required from facebook")
                    editor.putString("password", password)
                    editor.apply()
                    val mPreferences = activity.getSharedPreferences("first_time", Context.MODE_PRIVATE)
                    firstTime = mPreferences.getBoolean("firstTime", true)
                    if (firstTime as Boolean) {
                        val editor = mPreferences.edit()
                        editor.putBoolean("firstTime", false)
                        editor.apply()
                    }
                    Toastutils.showToast(activity, "Login Successfull")
                    var intent: Intent = Intent(activity, RestaurantActivity::class.java)
                    startActivity(intent)

                }

                var bundle: Bundle = Bundle()
                bundle.putString("fields", "id,name,email,gender,birthday")
                request.parameters = bundle
                request.executeAsync()

            }

            override fun onCancel() {
                Toast.makeText(activity, "Login Cancelled", Toast.LENGTH_LONG).show()
            }

            override fun onError(error: FacebookException?) {
                Toastutils.showsSnackBar(activity, "Login Failed")
            }
        })



    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i("Fragment", "OnActivity Result")
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(v: View?) {

        when (v!!.id) {

            R.id.textview_Register -> {
                var registerFragment: RegisterFragment = RegisterFragment()
                Fragmentutils.replaceFragmentwithBackStack(activity, registerFragment, fragmentManager, R.id.container)
            }

            R.id.btn_login -> {


                var username: String = editText_Username.text.toString()
                var password: String = editText_Password.text.toString()

                if (!validatename(username)) run {
                    if (!validateemail(username)) {
                        method2(username)

                        editText_Username.requestFocus()
                    } else {
                        check(username, password)
                    }

                } else {
                    check(username, password)
                }

            }

        }
    }

    fun check(username: String, password: String) {
        if (!validatepassword(password)) run {
            method4(password)

            editText_Password.requestFocus()

        } else {
            loginpresenter = LoginPresenterImpl()
            var issucess: Boolean = loginpresenter.validateUser(username, password, mydb, activity)

            if (issucess) {

                var uid = mydb.myDao().getUserId(username)
                Log.i("Username",username)
                var userData = mydb.myDao().getUserInfo(uid)
                Log.i("UID",uid.toString())
                var table: Table = Table()
                table.id = uid
                table.name = userData[0].name
                table.email = userData[0].email
                table.password = userData[0].password
                table.mobilenumber = userData[0].mobilenumber
                table.loginStatus = "yes"
                mydb.myDao().update(table)
                Log.i("User", "status Updated")
                val mPreferences = activity.getSharedPreferences("first_time", Context.MODE_PRIVATE)
                firstTime = mPreferences.getBoolean("firstTime", true)
                if (firstTime as Boolean) {
                    val editor = mPreferences.edit()
                    editor.putBoolean("firstTime", false)
                    editor.apply()
                }

                var intent: Intent = Intent(activity, RestaurantActivity::class.java)
                startActivity(intent)
            }

        }
    }

    fun validatepassword(password: String?): Boolean {
        return password != null && password.length >= 5

    }

    fun validateemail(email: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern = Pattern.compile(EMAIL_PATTERN)
        matcher = pattern.matcher(email)

        return matcher.matches()

    }

    fun validatename(name: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val namePATTERN = "^[a-zA-Z]+$"
        pattern = Pattern.compile(namePATTERN)
        matcher = pattern.matcher(name)
        return matcher.matches()
    }

    fun method4(id: String) {
        if (id == "") {
            input_layout_login_Password.isErrorEnabled=true
            input_layout_login_Password.error = "This is required field"
        } else {
           input_layout_login_Password.error = "It should be minimum of 5 in length"
        }
    }

    fun method2(id: String) {
        if (id == "") {
            input_layout_login_Username.isErrorEnabled=true
            input_layout_login_Username.error = "This is required field"
        } else {
            input_layout_login_Username.error = "Plzz enter Valid Username/Email"
        }
    }


    override fun validuser(v: Activity, username: String?, email: String?, Number: String?, password: String) {
        Toastutils.showsSnackBar(v, "Login Successfull")
        var sharedpref: SharedPreferences = v.getSharedPreferences("UserInfo", 0)
        var editor: SharedPreferences.Editor = sharedpref.edit()
        editor.putString("username", username)
        editor.putString("email", email)
        editor.putString("number", Number)
        editor.putString("password", password)
        editor.apply()

        var sharedpref2: SharedPreferences = v.getSharedPreferences("UserInfoforfb", 0)
        var editor2: SharedPreferences.Editor = sharedpref.edit()
        editor.putString("password", password)
        editor.apply()




    }
}
