package com.sa.restaurant.helpers

import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.sa.restaurant.R
import com.squareup.picasso.Picasso

/**
 * CustomInfoWindow class for showing a custom InfoWindow
 * Created On :- 23 july 2018
 * Created by :- jay.ghodasara
 */


class CustomInfoWindow(var context: Context) : GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(marker: Marker?): View {
        var layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view: View = layoutInflater.inflate(R.layout.custom_window, null)

        var Name = view.findViewById<TextView>(R.id.name_infowindow)
        var address = view.findViewById<TextView>(R.id.address_infowindow)
        var ratings = view.findViewById<RatingBar>(R.id.ratingbar_infowindow)
        var open = view.findViewById<TextView>(R.id.open_status_infowindow)
        var imgUrl = view.findViewById<ImageView>(R.id.img_infowindow)


        Name.text = marker!!.title
        var infoWindowPojo: InfoWindowPojo = marker.tag as InfoWindowPojo
        address.text = infoWindowPojo.Address
        if (infoWindowPojo.ratings!!.toFloat() == 14.0f) {
            ratings.visibility = View.GONE
        } else {
            ratings.rating = infoWindowPojo.ratings!!.toFloat()
        }


        if (infoWindowPojo.openStatus == "NotAvailable") {
            open.visibility = View.GONE
        } else {
            if (infoWindowPojo.openStatus == "true") {
                open.text = "Currently Open"
            } else {
                open.text = "Currently Closed"
            }

        }
        if (infoWindowPojo.image == " ") {
            imgUrl.visibility = View.GONE
        } else {
            Picasso.get().load(infoWindowPojo.image).into(imgUrl)
        }



        return view
    }

    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }

}