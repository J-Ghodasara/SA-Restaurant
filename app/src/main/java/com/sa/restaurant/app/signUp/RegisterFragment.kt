package com.sa.restaurant.app.signUp


import android.app.Activity
import android.os.Bundle
import android.app.Fragment
import android.app.FragmentManager
import android.arch.persistence.room.Room
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.sa.restaurant.R
import com.sa.restaurant.app.login.LoginFragment
import com.sa.restaurant.app.roomDatabase.Mydatabase
import com.sa.restaurant.app.signUp.presenter.RegisterPresenter
import com.sa.restaurant.app.signUp.presenter.RegisterPresenterImpl
import com.sa.restaurant.app.signUp.view.RegisterView
import com.sa.restaurant.utils.Fragmentutils
import com.sa.restaurant.utils.Toastutils
import kotlinx.android.synthetic.main.fragment_register.*


/**
 * Fragment for Registration
 * Created On :- 19 july 2018
 * Created by :- jay.ghodasara
 */


class RegisterFragment : Fragment(), View.OnClickListener,RegisterView {


    lateinit var mydb:Mydatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
      var view:View = inflater.inflate(R.layout.fragment_register, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mydb= Room.databaseBuilder(activity, Mydatabase::class.java,"Database").allowMainThreadQueries().build()
        btn_signup.setOnClickListener(this)
        tv_signup_Login.setOnClickListener(this)

    }

    override fun onClick(v: View?) {


        when(v!!.id){
            R.id.btn_signup->{

                var name:String= et_signup_Name.text.toString()
                var email:String= et_signup_Email.text.toString()
                var password:String = et_signup_Password.text.toString()
                var confirmpassword:String=et_signup_CPassword.text.toString()
                var number:String= et_signup_mobileno.text.toString()



                var registerPresenter:RegisterPresenter= RegisterPresenterImpl()

                registerPresenter.checkforuser(name,email,number,password,mydb,activity,fragmentManager)

               // registerPresenter.insertintodb(name,email,number,password,mydb,activity,fragmentManager)


            }
            R.id.tv_signup_Login ->{
                var loginFragment: LoginFragment= LoginFragment()
                   Fragmentutils.replaceFragment(activity,loginFragment,fragmentManager,R.id.container)
            }

        }
    }

    override fun showSnackBar(v:Activity,fragmentmanager: FragmentManager) {

        var loginFragment: LoginFragment= LoginFragment()

        Toastutils.showsSnackBar(v,"Registered Successfully")

        Fragmentutils.replaceFragment(v,loginFragment,fragmentmanager,R.id.container)
    }




}
