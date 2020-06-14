/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */

package com.ericg.debtsmanager

/**
 *      created by: Eric gacoki
 *      started on: Fri May 8, 2020 10:10 HRS EAT
 *      #30DaysOfKotlin
 * */

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.ericg.debtsmanager.firebaseAuth.CreateAccountActivity
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

        val pIndices = 0..5
        val imagesIndices = 0..6

        val pImages =
            arrayOf(
                getDrawable(R.drawable.money_couple),
                getDrawable(R.drawable.money_hand),
                getDrawable(R.drawable.money_in_hand),
                getDrawable(R.drawable.money_loan),
                getDrawable(R.drawable.money_plant),
                getDrawable(R.drawable.money_savings),
                getDrawable(R.drawable.money_wheelbarrow)
            )

        val pSlogans = arrayOf(
            "Your all time financial assistant",
            "Help at hand",
            "Easily manage your debts",
            "Just for you",
            "The most trusted care taker",
            "Give out to the qualified"
        )

        showRandomImage(imagesIndices, pImages)
        showRandomSlogan(pIndices, pSlogans)
    }

    private fun showRandomSlogan(indices: IntRange, slogans: Array<String>) {
        val randomIndex = indices.indexOf(indices.random())
        tvSlogan.text = slogans[randomIndex]
    }

    private fun showRandomImage(indices: IntRange, images: Array<Drawable?>) {
        val randomIndex = indices.indexOf(element = indices.random())
        logo.setImageDrawable(images[randomIndex])
    }

    @Suppress("LocalVariableName")
    private fun splashOut() {

        val AUTO_SIGN_IN = "autoSignIn"
        val HAS_ACCOUNT = "hasAccount"

        val sharedPrefsAutoSignIn: SharedPreferences = getSharedPreferences(AUTO_SIGN_IN, 0)
        val autoSignIn = sharedPrefsAutoSignIn.getBoolean(AUTO_SIGN_IN, false)

        val sharedPrefsHasAccount: SharedPreferences = getSharedPreferences(HAS_ACCOUNT, 0)
        val hasAccount = sharedPrefsHasAccount.getBoolean(HAS_ACCOUNT, false)

        if (autoSignIn && hasAccount) {

            val intent = Intent(this, ParentActivity::class.java)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
            finish()
        } else if (!autoSignIn && hasAccount) {
            val intent = Intent(this, SignInActivity::class.java)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
            finish()
        } else {
            val intent = Intent(this, CreateAccountActivity::class.java)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
            finish()
        }
    }
}