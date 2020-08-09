/*
 * Copyright (c)  Updated by eric on  8/9/20 2:23 AM
 */

package com.ericg.debtsmanager.admin

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ericg.debtsmanager.R
import kotlinx.android.synthetic.main.activity_about_dev.*

/**
 * @author eric
 * @date 8/9/20
 */

class AboutDeveloper: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_dev)

        toggleLike()
    }

    private fun toggleLike() {
        var liked =  false
        like.setOnClickListener {
            liked = if (liked){
                like.setImageDrawable(getDrawable(R.drawable.ic_not_liked))
                likesText.visibility = View.INVISIBLE
                false
            } else {
                like.setImageDrawable(getDrawable(R.drawable.ic_liked))
                likesText.visibility = View.VISIBLE
                true
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}