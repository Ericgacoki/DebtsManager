/*
 * Copyright (c)  Updated by eric on  8/9/20 2:23 AM
 */

package com.ericg.debtsmanager.admin

import android.content.Intent
import android.graphics.Color.BLUE
import android.graphics.Color.WHITE
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.extensions.snackBuilder
import com.ericg.debtsmanager.extensions.toast
import kotlinx.android.synthetic.main.activity_about_dev.*

/**
 * @author eric
 * @date 8/9/20
 */

open class AboutDeveloper : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_dev)

        handleClicks()
    }

    private fun handleClicks() {
        // TODO pass real links
        myTwitter.setOnClickListener {
            val twitterLink = "https://www.twitter.com"
            browseUrl(twitterLink)
        }

        myGithub.setOnClickListener {
            val githubLink = "https://www.github.com/Ericgacoki"
            browseUrl(githubLink)
        }

        myInstagram.setOnClickListener {
            val iGLink = "https://www.instagram.com"
            browseUrl(iGLink)
        }

        myWhatsApp.setOnClickListener {
            val whatsAppIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("tel:+254716965216"))
            whatsAppIntent.putExtra(Intent.EXTRA_SUBJECT, "Hey there!")
            whatsAppIntent.type = "textPlain"
            try {
                startActivity(Intent.createChooser(whatsAppIntent, "Select whatsApp"))
            } catch (e: Exception) {
                toast(e.toString())
            }
        }

        myFacebook.setOnClickListener {
            myFacebook.snackBuilder("Sorry, I don't use facebook").apply {
                setBackgroundTint(BLUE)
                setTextColor(WHITE)
                setAction("I know") {}
                show()
            }
        }

        myEmail.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND, Uri.parse("mailto:"))
            emailIntent.apply {
                type = "textPlain"
                putExtra(Intent.EXTRA_SUBJECT, "Hey there!")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("gacokieric@gmail.com"))
            }
            try {
                startActivity(Intent.createChooser(emailIntent, "Select your email client"))
            } catch (e: Exception) {
                toast(e.toString())
            }
        }
    }


    private fun browseUrl(url: String) {
        val browseIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            startActivity(Intent.createChooser(browseIntent, "Select Browser"))

        } catch (e: Exception) {
            toast(e.toString())
        }
    }

    private var exit = false

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (exit) {
            super.onBackPressed()
            finish()
        } else {
            toast("press again to exit")
            exit = true
            Handler().postDelayed({ exit = false }, 2000)
        }
    }
}