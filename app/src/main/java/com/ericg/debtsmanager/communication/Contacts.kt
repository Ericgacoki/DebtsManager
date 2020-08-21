/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */

package com.ericg.debtsmanager.communication

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.RC_PERMISSION_CALL
import com.ericg.debtsmanager.extensions.toast
import com.ericg.debtsmanager.network.browse
import com.ericg.debtsmanager.network.emailUs
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_contacts.view.*

fun Activity.contacts(): BottomSheetDialog {

    val contactsBuilder = BottomSheetDialog(this)
    val customView = layoutInflater.inflate(R.layout.dialog_contacts, null)

    customView.callUs.setOnClickListener {
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.CALL_PHONE
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

            val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:+254716965216"))
            try {
                startActivity(
                    Intent.createChooser(
                        callIntent,
                        "Select calling App [courtesy of Debts manager]"
                    )
                )
            } catch (e: Exception) {
                toast(e.message.toString())
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CALL_PHONE), RC_PERMISSION_CALL
            )
        }
    }
    customView.emailUs.setOnClickListener {
        val subject = "Debts manager help"
        val sendTo = arrayOf("gacokieric@gmail.com")   // debtsmanagerhelp@gmail.com
        emailUs(subject, sendTo)
    }

    customView.whatsAppUs.setOnClickListener {
        val body = "<Describe what help you need here>"
        val whatsAppIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:+254745623611"))

        whatsAppIntent.apply {
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            startActivity(Intent.createChooser(whatsAppIntent, "Select WhatsApp"))
        } catch (e: Exception) {
            toast(e.message.toString())
        }
    }

    customView.facebookUs.setOnClickListener {
        val fbUrl = "https://www.facebook.com"
        browse(fbUrl)
    }

    customView.igUs.setOnClickListener {
        val igUrl = "https://www.instagram.com"
        browse(igUrl)
    }

    customView.tweetUs.setOnClickListener {
        val twitterUrl = "https://www.twitter.com"
        browse(twitterUrl)
    }

    return contactsBuilder.apply {
        setContentView(customView)
        show()
    }
}












