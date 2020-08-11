/*
 * Copyright (c)  Updated by eric on  8/4/20 4:48 PM
 */

package com.ericg.debtsmanager.extensions

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ericg.debtsmanager.RC_SELECT_MAIN_IMAGE
import com.google.android.material.snackbar.Snackbar

/**
 * @author eric
 * @date 8/4/20
 */
fun Activity.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, duration).show()
}

fun Fragment.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, msg, duration).show()
}

fun View.snackBuilder(msg: String, duration: Int = 3000): Snackbar {
    return Snackbar.make(this, msg, duration)
}

fun Activity.selectImage(requestCode: Int) {
    // TODO crop the image before exiting the intent

    val imageIntent = Intent().apply {
        type = "image/*"
        action = Intent.ACTION_GET_CONTENT
        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
    }
    startActivityForResult(
        Intent.createChooser(imageIntent, "Select image"),
        requestCode
    )
}

fun Activity.userSharedPrefs(key: String, privateMode: Int = 0): SharedPreferences? {
   return getSharedPreferences(key, privateMode)
}


















