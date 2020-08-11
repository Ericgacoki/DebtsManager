/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */

package com.ericg.debtsmanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.adapters.InstallmentsAdapter
import com.ericg.debtsmanager.extensions.toast
import kotlinx.android.synthetic.main.fragment_installmets.*

class Installments : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_installmets, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()

        fabAddInstallment.setOnClickListener {
            toast("Coming soon!", 1)
        }
    }

    private fun updateUI() {

        iRecyclerView.apply {
            adapter = InstallmentsAdapter()
            layoutManager =
                LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        }
        noInstallments.visibility = if (InstallmentsAdapter().itemCount == 0) {
            View.VISIBLE
        } else {
            INVISIBLE
        }
    }
}