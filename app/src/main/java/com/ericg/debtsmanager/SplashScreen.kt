/*
 * Copyright (c)  Updated by eric on  9/13/20 12:31 AM
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
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.ericg.debtsmanager.auth.CreateAccountActivity
import com.ericg.debtsmanager.auth.SignInActivity
import kotlinx.android.synthetic.main.activity_splash.*

@Suppress("DEPRECATION")
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
                ContextCompat.getDrawable(this, R.drawable.money_couple),
                ContextCompat.getDrawable(this, R.drawable.money_hand),
                ContextCompat.getDrawable(this, R.drawable.money_in_hand),
                ContextCompat.getDrawable(this, R.drawable.money_loan),
                ContextCompat.getDrawable(this, R.drawable.money_plant),
                ContextCompat.getDrawable(this, R.drawable.money_savings),
                ContextCompat.getDrawable(this, R.drawable.money_wheelbarrow)
            )

        val pSlogans = arrayOf(
            "Your all time financial assistant",
            "Help at hand",
            "Easily manage your debts",
            "Just for you",
            "The most trusted care taker",
            "Give out to the qualified"
        )
        checkTheme()
        showRandomImage(imagesIndices, pImages)
        showRandomSlogan(pIndices, pSlogans)
    }
    @Suppress("LocalVariableName")
    private fun checkTheme(){
        val darkTheme = getSharedPreferences(DARK_MODE_ENABLED, 0).getBoolean(DARK_MODE_ENABLED, false)
        if (darkTheme){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            delegate.applyDayNight()
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            delegate.applyDayNight()
        }
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

        val sharedPrefsAutoSignIn: SharedPreferences = getSharedPreferences(AUTO_SIGN_IN, 0)
        val autoSignIn = sharedPrefsAutoSignIn.getBoolean(AUTO_SIGN_IN, false)

        val sharedPrefsHasAccount: SharedPreferences = getSharedPreferences(HAS_ACCOUNT, 0)
        val hasAccount = sharedPrefsHasAccount.getBoolean(HAS_ACCOUNT, false)

        if (hasAccount && autoSignIn) {

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
        } else { // this might be a new user
            val intent = Intent(this, CreateAccountActivity::class.java)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
            finish()
        }
    }
}
