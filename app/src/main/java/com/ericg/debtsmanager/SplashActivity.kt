package com.ericg.debtsmanager

/**
 *      created by: Eric gacoki
 *      started on: Fri May 8, 2020 10:10 HRS EAT
 *      #30DaysOfKotlin
 * */

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)

        btmText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.frombottom))
        logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadeout))

        Handler().postDelayed({
            kotlin.run { splashOut() }
        }, 2000)
    }

    private fun splashOut() {
        val intent = Intent(this, SignInActivity::class.java)
        if(intent.resolveActivity(packageManager) != null){
            startActivity(intent)
        }
        finish()
    }
}