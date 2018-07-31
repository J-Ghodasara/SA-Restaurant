package com.sa.restaurant.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.sa.restaurant.R
import android.support.v4.view.ViewPager

/**
 * ViewPager adapter to show images on the Restaurant activity screen
 * Created On :- 21 july 2018
 * Created by :- jay.ghodasara
 */

class ViewPagerAdapter(var context: Context) : PagerAdapter() {

    var layoutInflater: LayoutInflater? = null
    var images = arrayOf<Int>(R.drawable.images, R.drawable.six, R.drawable.backgrounddark)

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var images = arrayOf<Int>(R.drawable.images, R.drawable.six, R.drawable.backgrounddark)
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view: View = layoutInflater!!.inflate(R.layout.cutom_layout, null)
        var imgView: ImageView = view.findViewById<ImageView>(R.id.imageView)
        imgView.setImageResource(images[position])

        val vp = container as ViewPager
        vp.addView(view, 0)
        return view

    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }

}