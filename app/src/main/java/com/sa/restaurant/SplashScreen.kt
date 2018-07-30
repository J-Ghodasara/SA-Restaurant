package com.sa.restaurant

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)

        var t: Thread = Thread(Runnable {

            Thread.sleep(5000)
            var i: Intent = Intent(this, MainActivity::class.java)
            startActivity(i)
        })
        t.start()

    }

}
