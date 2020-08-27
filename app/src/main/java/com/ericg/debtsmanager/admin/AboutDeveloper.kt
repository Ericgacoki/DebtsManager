/*
 * Copyright (c)  Updated by eric on  8/9/20 2:23 AM
 */

package com.ericg.debtsmanager.admin

import android.content.Intent
import android.graphics.Color.BLUE
import android.graphics.Color.WHITE
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.extensions.snackBuilder
import com.ericg.debtsmanager.extensions.toast
import com.ericg.debtsmanager.network.browse
import com.ericg.debtsmanager.network.sendEmail
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
            browse(twitterLink)
        }

        myGithub.setOnClickListener {
            val githubLink = "https://www.github.com/Ericgacoki"
            browse(githubLink)
        }

        myLinkedIn.setOnClickListener {
            val iGLink = "https://www.instagram.com"
            browse(iGLink)
        }

        myWhatsApp.setOnClickListener {
            val whatsAppIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:+254716965216"))
            whatsAppIntent.putExtra(Intent.EXTRA_TEXT, "Hey there!")
          //  whatsAppIntent.type = "text/plain"
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
                setAction("Ok") {}
                show()
            }
        }

        myEmail.setOnClickListener {
            sendEmail("Debts manager regards", arrayOf("gacokieric@gmail.com"))
        }
    }
}