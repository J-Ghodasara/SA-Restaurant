package com.sa.restaurant.app.signUp


import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.arch.persistence.room.Room
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sa.restaurant.MainActivity
import com.sa.restaurant.R
import com.sa.restaurant.app.login.LoginFragment
import com.sa.restaurant.app.roomDatabase.Mydatabase
import com.sa.restaurant.app.signUp.presenter.RegisterPresenter
import com.sa.restaurant.app.signUp.presenter.RegisterPresenterImpl
import com.sa.restaurant.app.signUp.view.RegisterView
import com.sa.restaurant.utils.Fragmentutils
import com.sa.restaurant.utils.Toastutils
import kotlinx.android.synthetic.main.fragment_register.*
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Fragment for Registration
 * Created On :- 19 july 2018
 * Created by :- jay.ghodasara
 */


class RegisterFragment : Fragment(), View.OnClickListener, RegisterView {


    lateinit var mydb: Mydatabase


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_register, container, false)
        MainActivity.isVisible=false
        MainActivity.isLoginVisible=false


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mydb = Room.databaseBuilder(activity, Mydatabase::class.java, "Database").allowMainThreadQueries().build()
        btn_signup.setOnClickListener(this)
        tv_signup_Login.setOnClickListener(this)

    }

    override fun onClick(v: View?) {


        when (v!!.id) {
            R.id.btn_signup -> {

                var name: String = et_signup_Name.text.toString()
                var email: String = et_signup_Email.text.toString()
                var password: String = et_signup_Password.text.toString()
                var confirmpassword: String = et_signup_CPassword.text.toString()
                var number: String = et_signup_mobileno.text.toString()


                if (!validateemail(email)) run {
                    method1(email)

                    et_signup_Email.requestFocus()
                } else if (!validatename(name)) run {
                    method2(name)

                    et_signup_Name.requestFocus()
                } else if (!validatemobileno(number)) run {
                    method3(number)

                    et_signup_mobileno.requestFocus()
                } else if (!validatepassword(password)) run {
                    method4(password)

                    et_signup_Password.requestFocus()
                } else if (!confirmpassword(confirmpassword)) run {
                    method5(confirmpassword)

                    et_signup_CPassword.requestFocus()
                } else {
                    var registerPresenter: RegisterPresenter = RegisterPresenterImpl()

                    registerPresenter.checkforuser(name, email, number, password, mydb, activity, fragmentManager)
                }





            }
            R.id.tv_signup_Login -> {
                var loginFragment: LoginFragment = LoginFragment()
                Fragmentutils.replaceFragment(activity, loginFragment, fragmentManager, R.id.container)
            }

        }
    }

    override fun showSnackBar(v: Activity, fragmentmanager: FragmentManager) {

        var loginFragment: LoginFragment = LoginFragment()

        Toastutils.showsSnackBar(v, "Registered Successfully")

        Fragmentutils.replaceFragment(v, loginFragment, fragmentmanager, R.id.container)
    }

    fun validateemail(email: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val EMAIL_PATTERN = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z_\\-\\.]+)\\.([a-zA-Z]{2,5})\$"
        pattern = Pattern.compile(EMAIL_PATTERN)
        matcher = pattern.matcher(email)

        return matcher.matches()

    }


    fun validatepassword(password: String?): Boolean {
        val PASSWORD_PATTERN="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\$@\$!%*?&])[A-Za-z\\d\$@\$!%*?&]{8,}"
        val pattern: Pattern
        val matcher: Matcher
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)

        return matcher.matches()
    }

    fun confirmpassword(E: String): Boolean {
        val cpa = et_signup_Password.text.toString()
        val cp = et_signup_CPassword.text.toString()

        // if(!(cpass==cp))
        run { return cpa == cp }

    }

    fun validatemobileno(mob: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PATTERN = "^[0-9]{10}$"
        pattern = Pattern.compile(PATTERN)
        matcher = pattern.matcher(mob)

        return matcher.matches()

    }

    fun validatename(name: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val namePATTERN = "^[a-zA-Z0-9._-]+$"
        pattern = Pattern.compile(namePATTERN)
        matcher = pattern.matcher(name)

        return matcher.matches()

    }


    fun method4(id: String) {
        if (id == "") {
            input_layout_login_Password.isErrorEnabled=true
            input_layout_login_Password.error = "This is required field"
        } else {
            input_layout_login_Password.error = "It should be minimum 5 in length"
        }
    }

    fun method2(id: String) {
        if (id == "") {
            input_layout_Name.isErrorEnabled=true
            input_layout_Name.error = "This is required field"
        } else {
            input_layout_Name.error = "It should contain only letters"
        }
    }


    fun method3(id: String) {
        if (id == "") {
            input_layout_mobileno.isErrorEnabled=true
            input_layout_mobileno.error = "This is required field"
        } else {
            input_layout_mobileno.error = "It should contain only 10 digits"
        }
    }

    fun method1(id: String) {
        if (id == "") {
            signup_Email.isErrorEnabled=true
            signup_Email.error = "This is required field"
        } else {
            signup_Email.error = "Enter valid email"
        }
    }

    fun method5(id: String) {
        if (id == "") {
            input_layout_CPassword.isErrorEnabled=true
            input_layout_CPassword.error = "This is required field"
        } else {
            input_layout_CPassword.error = "Password & Confirm password must be same"
        }
    }

}
