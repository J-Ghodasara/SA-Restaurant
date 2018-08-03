package com.sa.restaurant.helpers

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewAnimationUtils
import com.sa.restaurant.R
import android.animation.AnimatorListenerAdapter
import android.os.Build
import android.support.annotation.RequiresApi


class CircleReveal(){



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun reveal(activity: Activity, viewId:Int, posFromRight:Int, containsOverFlow:Boolean, isShow:Boolean){
        var view: View = activity.findViewById(viewId)
        var width:Int=view.width
        if(posFromRight>0)
            width-=(posFromRight*activity.resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_material))-(activity.resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)/ 2);
        if(containsOverFlow) {
            width-=activity.resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material)
        }

        var cx:Int = width
        var cy= (view.height)/2

        var anim: Animator
        if(isShow){
            anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f,width.toFloat())

        }  else
            anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, width.toFloat(), 0f)
        anim.duration = 220.toLong()

        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (!isShow) {
                    super.onAnimationEnd(animation)
                    view.visibility = View.INVISIBLE
                }
            }
        })

        if(isShow)
            view.visibility = View.VISIBLE

        // start the animation
        anim.start()
    }
}