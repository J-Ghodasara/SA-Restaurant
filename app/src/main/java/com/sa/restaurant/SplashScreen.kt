package com.sa.restaurant

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity

/**
 * A simple splash screen that shows for 5 sec at first when activity starts
 *
 */
class SplashScreen : AppCompatActivity() {
    var t: Handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)

        t.postDelayed(Runnable {

            var i: Intent = Intent(this, MainActivity::class.java)
            startActivity(i)
        }, 5000)


    }

    override fun onPause() {
        t.removeCallbacksAndMessages(null)
        super.onPause()
    }



}
