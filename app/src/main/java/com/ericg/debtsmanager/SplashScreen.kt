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
import com.ericg.debtsmanager.firebaseAuth.SignInActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)

        tvSlogan.startAnimation(AnimationUtils.loadAnimation(this, R.anim.frombottom))
        logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadeout))

        Handler().postDelayed({
            kotlin.run { splashOut() }
        }, 3000)

        val pIndices = 0..4
        val pSlogans = arrayOf(
            "Your all time financial assistant",
            "Help at hand",
            "Easily manage your debts...",
            "Just for you",
            "The most trusted care taker"
        )
        showRandomSlogan(pIndices, pSlogans)
    }

    private fun showRandomSlogan(indices: IntRange, slogans: Array<String>) {
        val randomIndex = indices.shuffled().last()
        tvSlogan.text = slogans[randomIndex]
    }

    private fun splashOut() {

        //todo  check if user is signed in or has account , their name .. using shared preferences then decide the next activity

        val intent = Intent(this, SignInActivity::class.java)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
        finish()
    }
}