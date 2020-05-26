package com.ericg.debtsmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_my_debts.*

class MyDebts : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_debts)

       // myDebtsHrScrollView.smoothScrollTo(300,0)

        mDebtors.setOnClickListener {
            startActivity(Intent(this, Debtors::class.java))
        }
    }
}
