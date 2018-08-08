package com.sa.restaurant.app.favorites

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.sa.restaurant.R


class NoItemsFragment : Fragment() {

companion object {
    var noItemsAvailable:Boolean=true
}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        noItemsAvailable=true
        return inflater.inflate(R.layout.fragment_no_items, container, false)
    }



}
