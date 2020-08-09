/*
 * Copyright (c)  Updated by eric on  8/4/20 4:48 PM
 */

package com.ericg.debtsmanager.utils

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment

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
