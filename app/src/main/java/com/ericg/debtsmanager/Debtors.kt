package com.ericg.debtsmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.ericg.debtsmanager.adapters.DebtorsAdapter
import kotlinx.android.synthetic.main.activity_debtors.*

class Debtors : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()
        setContentView(R.layout.activity_debtors)

        debtorsRecyclerView.apply {
            adapter = DebtorsAdapter()
            layoutManager = LinearLayoutManager(this@Debtors, LinearLayoutManager.VERTICAL, false)
        }
        LinearSnapHelper().attachToRecyclerView(debtorsRecyclerView)
    }
}