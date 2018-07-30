package com.sa.restaurant.app.RestaurantsActivity

import android.os.Bundle
import android.app.Fragment
import android.os.ResultReceiver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sa.restaurant.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_restaurant_info.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RestaurantInfoFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [RestaurantInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class RestaurantInfoFragment : Fragment() {
    lateinit var restroName: String
    lateinit var restroAddress: String
    lateinit var restroImg: String

    companion object {
        var isInfoVisible: Boolean = false
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_restaurant_info, container, false)
        RestaurantActivity.homeIsVisible = false
        //RestaurantActivity.favIsVisibletouser=RestaurantActivity.favIsVisibletouser
        isInfoVisible = true
        restroName = arguments.getString("restroName", null).toString()
        restroAddress = arguments.getString("restroAddress", null).toString()
        restroImg = arguments.getString("restroImg", null).toString()


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        name.text = restroName
        address.text = restroAddress
        Picasso.get().load(restroImg).into(img)
    }


}
