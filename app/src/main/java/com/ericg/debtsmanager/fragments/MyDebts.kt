package com.ericg.debtsmanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.adapters.MyDebtsAdapter
import kotlinx.android.synthetic.main.fragment_my_debts.*

class MyDebts : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? =  inflater.inflate(R.layout.fragment_my_debts, container, false)


    override fun onStart() {
        super.onStart()

        mRecyclerView.apply {
            adapter = MyDebtsAdapter()
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        }

        val numMyDebts = MyDebtsAdapter().itemCount
        noDebts.visibility = if (numMyDebts == 0) { VISIBLE } else { INVISIBLE }
    }
}