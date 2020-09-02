/*
 * Copyright (c)  Updated by eric on  9/2/20 8:38 PM
 */

package com.ericg.debtsmanager.admin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
            val iGLink = "https://www.linkedin.com"
            browse(iGLink)
        }

        myWhatsApp.setOnClickListener {
            val whatsAppIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:+254716965216"))
            whatsAppIntent.putExtra(Intent.EXTRA_TEXT, "Hey there!")
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
            sendEmail("Debts manager regards", toAddress = arrayOf("gacokieric@gmail.com"))
        }

        donateToDeveloper()
    }

    private fun donateToDeveloper() {
        payPal.setOnClickListener {
            browse("https://www.paypal.com")
        }

        mPesa.setOnClickListener {
            // TODO use Mpesa Daraja API or a floating widget with my details,
            val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("phone", "+254716965216")
            clipBoard.setPrimaryClip(clipData)
            toast("phone number copied")
        }

        hireMeOnUpWork.setOnClickListener {
            browse("https://upwork.com")
        }
    }
}
