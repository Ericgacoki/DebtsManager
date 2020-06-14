/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */

package com.ericg.debtsmanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.adapters.LoansAdapter
import kotlinx.android.synthetic.main.fragment_loans.*

class Loans : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_loans, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUi()

    }

    private fun updateUi() {

        lRecyclerView.apply {
            adapter = LoansAdapter()
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        }

        noLoans.visibility = if (LoansAdapter().itemCount == 0) {
            VISIBLE
        } else {
            View.INVISIBLE
        }
    }
}