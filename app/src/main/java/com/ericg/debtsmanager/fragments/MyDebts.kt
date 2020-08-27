/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */

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
import com.ericg.debtsmanager.data.DebtData
import com.ericg.debtsmanager.extensions.openAddDebtFragment
import com.ericg.debtsmanager.extensions.toast
import kotlinx.android.synthetic.main.fragment_my_debts.*

@Suppress("SpellCheckingInspection")
class MyDebts : Fragment(), MyDebtsAdapter.MyDebtItemClickListener {

    private val myDebt1 = DebtData(
        "Mson D", "02/10/2019", "01/20/2020", 1, "0702437...",
        50130, 12350, 2, null, null
    )
    private val myDebt2 = DebtData(
        "Jd Kate", "3/10/2012", "01/20/2025", 1, "07024377...",
        701300, 120350, 5, null, null
    )
    private val myDebt3 = DebtData(
        "Jd Kate", "3/10/2016", "01/20/2025", 1, "07024...",
        701300, 120350, 5, null, null
    )
    private val myDebt4 = DebtData(
        "Jd Kate", "3/10/2018", "01/20/2025", 1, "07...",
        701300, 120350, 5, null, null
    )

    private val myDebtsList: ArrayList<DebtData> = arrayListOf(myDebt1, myDebt2, myDebt3, myDebt4)
    private val myDebtsAdapter = MyDebtsAdapter(myDebtsList, this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_my_debts, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabAddMyDebt.setOnClickListener { openAddDebtFragment() }

        val numMyDebts = myDebtsList.size
        noDebts.visibility = if (numMyDebts == 0) {
            VISIBLE
        } else {
            INVISIBLE
        }
    }

    override fun onStart() {
        super.onStart()

        mRecyclerView.apply {
            adapter = myDebtsAdapter
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        }

    }

    override fun onMyDebtClicked(position: Int, itemView: View, viewId: Int) {

        when (viewId) {
            R.id.btnMyDebtAddPayment -> {
                toast("add payment")
            }
        }
    }
}