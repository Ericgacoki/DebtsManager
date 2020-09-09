/*
 * Copyright (c)  Updated by eric on  9/9/20 4:44 PM
 */

package com.ericg.debtsmanager.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.adapters.MyDebtsAdapter
import com.ericg.debtsmanager.data.DebtData
import com.ericg.debtsmanager.extensions.openAddDebtFragment
import com.ericg.debtsmanager.extensions.toast
import com.ericg.debtsmanager.viewmodel.GetDataViewModel
import kotlinx.android.synthetic.main.fragment_my_debts.*

@Suppress("SpellCheckingInspection")
class MyDebts : Fragment(), MyDebtsAdapter.MyDebtItemClickListener {


    private var myDebtsList: List<DebtData> = ArrayList()
    private val myDebtsAdapter = MyDebtsAdapter(myDebtsList, this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_my_debts, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadMyDebts()

        fabAddMyDebt.setOnClickListener { openAddDebtFragment() }

        swipeToRefreshMyDebts.setOnRefreshListener {
            loadMyDebts().observe(viewLifecycleOwner, {
                if (it) {
                    toast("refreshed successfully !")
                    swipeToRefreshMyDebts.isRefreshing = false
                } else {
                    toast("refreshing...")
                    swipeToRefreshMyDebts.isRefreshing = true
                }
            })
        }

        val numMyDebts = myDebtsList.size
        noDebts.visibility = if (numMyDebts == 0) {
            VISIBLE
        } else {
            INVISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        loadMyDebts()
    }

    override fun onResume() {
        super.onResume()
        loadMyDebts()
    }

    private fun activateBtmNav(activate: Boolean) {
        val navItems =
            arrayOf(R.id.debtors, R.id.myDebts, R.id.profile /*R.id.loans, R.id.installments*/)

        if (!activate) {

            navItems.forEach { item ->
                val foundItem = activity!!.findViewById<View>(item)
                foundItem.isClickable = false
                foundItem.isEnabled = false
            }
        } else {
            navItems.forEach { item ->
                val foundItem = activity!!.findViewById<View>(item)
                foundItem.isClickable = true
                foundItem.isEnabled = true
            }
        }
    }

    private fun loadMyDebts(): MutableLiveData<Boolean> {
        activateBtmNav(false)
        val retrieved: MutableLiveData<Boolean> = MutableLiveData(false)
        mRecyclerView.apply {
            adapter = myDebtsAdapter
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            // scrollToPosition(myDebtsList.size - 1)
        }

        val viewModel = ViewModelProvider(this).get(GetDataViewModel::class.java)
        viewModel.getData("myDebts")?.addOnCompleteListener { it ->
            if (it.isSuccessful) {
                retrieved.value =  true
                activateBtmNav(true)

                myDebtsList = it.result!!.toObjects(DebtData::class.java)
                myDebtsAdapter.myDebtsList = myDebtsList
                myDebtsAdapter.notifyDataSetChanged()

                viewModel.numOfMyDebts = myDebtsList.size.toFloat()

                val numMyDebts = myDebtsList.size
                noDebts.visibility = if (numMyDebts == 0) {
                    VISIBLE
                } else {
                    INVISIBLE
                }

            } else {
                activateBtmNav(true)
                toast("refreshing...")
            }
        }

        return retrieved
    }

    override fun onMyDebtClicked(position: Int, itemView: View, viewId: Int) {

        when (viewId) {
            R.id.btnMyDebtAddPayment -> {
                toast("add payment")
            }
        }
    }
}