/*
 * Copyright (c)  Updated by eric on  8/9/20 2:23 AM
 */

package com.ericg.debtsmanager.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.extensions.snackBuilder
import kotlinx.android.synthetic.main.activity_about_dev.*

/**
 * @author eric
 * @date 8/9/20
 */

open class AboutDeveloper : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_dev)

        myFacebook.setOnClickListener {
            myFacebook.snackBuilder("Sorry, I don't use facebook").apply {
                setAction("I know") {}
                show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}