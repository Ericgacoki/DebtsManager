package com.ericg.debtsmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import kotlinx.android.synthetic.main.activity_debtors.*

class Debtors : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        setContentView(R.layout.activity_debtors)

        debtorsRecyclerView.apply {
            adapter = DebtorsAdapter()
            layoutManager = LinearLayoutManager(this@Debtors, LinearLayoutManager.VERTICAL, false)
        }
        // LinearSnapHelper().attachToRecyclerView(debtorsRecyclerView)
    }
}