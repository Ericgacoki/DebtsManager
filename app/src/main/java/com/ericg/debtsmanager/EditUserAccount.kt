/*
 * Copyright (c)  Updated by eric on  8/2/20 6:36 PM
 */

package com.ericg.debtsmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.utils.toast

/**
 * @author eric
 * @date 8/2/20
 */

class EditUserAccount: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)
    }

    override fun onStart() {
        super.onStart()
        toast("View.onStart")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}