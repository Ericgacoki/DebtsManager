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

        val pIndices = 0..4
        val pIcons =
            arrayOf(
                getDrawable(R.drawable.applogo),
                getDrawable(R.drawable.profile8),
                getDrawable(R.drawable.user1),
                getDrawable(R.drawable.profile5),
                getDrawable(R.drawable.ic_dollar)
            )
        val pSlogans = arrayOf(
            "Your all time financial assistant",
            "Help at hand",
            "Easily manage your debts...",
            "Just for you",
            "The most trusted care taker"
        )
        showRandomIcon(pIndices, pIcons)
        showRandomSlogan(pIndices, pSlogans)
    }

    private fun showRandomSlogan(indices: IntRange, slogans: Array<String>) {
        val randomIndex = indices.indexOf(indices.random())
        tvSlogan.text = slogans[randomIndex]
    }

    private fun showRandomIcon(indices: IntRange, icons: Array<Drawable?>) {
        val randomIndex = indices.indexOf(element = indices.random())
        logo.setImageDrawable(icons[randomIndex])
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