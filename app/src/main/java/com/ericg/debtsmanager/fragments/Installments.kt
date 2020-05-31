package com.ericg.debtsmanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.adapters.InstallmentsAdapter
import kotlinx.android.synthetic.main.fragment_installmets.*

class Installments : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_installmets, container, false)

    override fun onStart() {
        super.onStart()

        iRecyclerView.apply {
            adapter = InstallmentsAdapter()
            layoutManager =
                LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)


        }
        fabAddInstallment.setOnClickListener {
            Toast.makeText(this.context, "Ready to go!", Toast.LENGTH_LONG).show()
        }
    }
}