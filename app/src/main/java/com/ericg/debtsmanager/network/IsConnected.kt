/*
 * Copyright (c)  Updated by eric on  9/13/20 12:31 AM
 */

@file:Suppress("DEPRECATION")

package com.ericg.debtsmanager.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.fragment.app.Fragment

object Internet {
    fun Fragment.isConnected(): Boolean? {
        val connected: Boolean

        val connectivityManager: ConnectivityManager =
            context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        connected = activeNetwork?.isConnectedOrConnecting == true

        return connected
    }
}