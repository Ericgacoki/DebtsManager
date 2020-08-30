/*
 * Copyright (c)  Updated by eric on  8/13/20 11:44 PM
 */

package com.ericg.debtsmanager.network

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.ericg.debtsmanager.extensions.toast

/**
 * @author eric
 * @date 8/13/20
 */

/** these functions belongs to extension functions category
* @see -> com.ericg.debtsmanager.extensions for more */

fun Activity.browse(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
        startActivity(Intent.createChooser(intent, "Select browser"))

    } catch (e: Exception) {
        toast(e.message.toString())
    }
}

fun Activity.sendEmail(subject: String, toAddress: Array<String>, body: String = "") {
    val sendEmailIntent = Intent(Intent.ACTION_SEND, Uri.parse("mailto"))
    sendEmailIntent.apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_EMAIL, toAddress)
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }

    try {
        startActivity(Intent.createChooser(sendEmailIntent, "Select Gmail App"))
    } catch (e: Exception) {
        toast(e.message.toString())
    }
}