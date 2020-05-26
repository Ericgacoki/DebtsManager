package com.ericg.debtsmanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.ericg.debtsmanager.adapters.DebtorsAdapter
import kotlinx.android.synthetic.main.activity_debtors.*

class Debtors : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN for full screen
        supportActionBar?.hide()
        setContentView(R.layout.activity_debtors)

        dMyDebts.setOnClickListener {
            startActivity(Intent(this, MyDebts::class.java))
        }

        dLoans.setOnClickListener {
            startActivity(Intent(this, Loans::class.java))
        }

        dInstallments.setOnClickListener {
            startActivity(Intent(this, Installments::class.java))
        }

        debtorsRecyclerView.apply {
            adapter = DebtorsAdapter()
            layoutManager = LinearLayoutManager(this@Debtors, LinearLayoutManager.VERTICAL, false)
        }
      //  LinearSnapHelper().attachToRecyclerView(debtorsRecyclerView)
    }
}