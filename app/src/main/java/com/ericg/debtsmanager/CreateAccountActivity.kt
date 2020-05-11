package com.ericg.debtsmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class CreateAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_create_account)
    }
}
