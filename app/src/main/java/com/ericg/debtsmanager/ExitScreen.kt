/*
 * Copyright (c)  Updated by eric on  9/3/20 11:48 AM
 */

@file:Suppress("DEPRECATION")

package com.ericg.debtsmanager

import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

/**
 * @author eric
 * @date 9/3/20
 */
class ExitScreen: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exit_screen)

        Handler().postDelayed({finish()},1000)
    }
}