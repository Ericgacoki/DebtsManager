/*
 * Copyright (c)  Updated by eric on  9/12/20 12:17 AM
 */

@file:Suppress("DEPRECATION")

package com.ericg.debtsmanager

import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_exit_screen.*


class ExitScreen: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exit_screen)

        val userName = getSharedPreferences(USER_NAME, 0).getString(USER_NAME, "")
        exitTextMessage.text = "Thank you $userName"
        Handler().postDelayed({finish()},1000)
    }
}