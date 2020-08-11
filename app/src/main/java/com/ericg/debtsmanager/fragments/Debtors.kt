/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */

package com.ericg.debtsmanager.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.adapters.DebtorsAdapter
import kotlinx.android.synthetic.main.dialog_add_debtor.view.*
import kotlinx.android.synthetic.main.fragment_debtors.*
import java.util.*

class Debtors : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_debtors, container, false)

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()

        fabAddDebtor.setOnClickListener {
            showAddDebtorDialog()
        }

        swipeToRefreshDebtors.setOnRefreshListener {

          //  swipeToRefreshDebtors.isRefreshing = false
          //  toast("refreshed successfully")
        }
    }

    private fun updateUI() {
        debtorsRecyclerView.apply {
            adapter = DebtorsAdapter(context)
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            // scroll to bottom automatically
            scrollToPosition(DebtorsAdapter(this.context).itemCount - 1)

            setOnScrollChangeListener{ view: View, i: Int, i1: Int, i2: Int, i3: Int ->

            }
        }

        noDebtors.visibility = if (DebtorsAdapter(context!!).itemCount == 0) {
            VISIBLE
        } else {
            INVISIBLE
        }
    }

    @SuppressLint("InflateParams")
    private fun showAddDebtorDialog() {
        val addDebtor = AlertDialog.Builder(this.context)
        val addDebtorView = layoutInflater.inflate(R.layout.dialog_add_debtor, null)

        /**
         *       set today as the max starting date and the min deadline
         */
        val today = Calendar.getInstance().timeInMillis

        addDebtorView.apply {
            addDebtorStartDate.maxDate = today
            addDebtorDeadline.minDate = today
        }
        addDebtor.apply {
            setView(addDebtorView)
            create().show()
        }
    }
}