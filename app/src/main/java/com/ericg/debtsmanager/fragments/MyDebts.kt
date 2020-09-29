/*
 * Copyright (c)  Updated by eric on  9/30/20 1:56 AM
 */

package com.ericg.debtsmanager.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.TOTAL_MY_DEBTS
import com.ericg.debtsmanager.adapters.MyDebtsAdapter
import com.ericg.debtsmanager.data.DebtData
import com.ericg.debtsmanager.extensions.openAddDebtFragment
import com.ericg.debtsmanager.extensions.snackBuilder
import com.ericg.debtsmanager.extensions.toast
import com.ericg.debtsmanager.network.Internet.isConnected
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

        mRecyclerView.apply {
            adapter = myDebtsAdapter
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        }
// todo send debt type via safe args
        fabAddMyDebt.setOnClickListener { openAddDebtFragment() }
        dataLoadingLayout2.setOnClickListener { /* do nothing but prevent clicks to views below it*/ }

        swipeToRefreshMyDebts.setOnRefreshListener {
            loadMyDebts(load = false).observe(viewLifecycleOwner, {
                if (it) {
                    toast("refreshed successfully !")
                    activateBtmNav(true)
                    swipeToRefreshMyDebts.isRefreshing = false
                }
            })
        }
    }

    override fun onStart() {
        super.onStart()
        loadMyDebts()
    }

    private fun activateBtmNav(activate: Boolean) {
        val navItems =
            arrayOf(
                R.id.debtors,
                R.id.myDebts,
                R.id.profile,
                R.id.fabAddMyDebt
            )

        if (!activate) {

            navItems.forEach { item ->
                val foundItem = activity!!.findViewById<View>(item)
                foundItem.isClickable = false
                foundItem.isEnabled = false
            }
        } else {
            val myDebts = activity!!.findViewById<View>(R.id.myDebts)

            navItems.forEach { item ->
                val foundItem = activity!!.findViewById<View>(item)
                foundItem.isClickable = true
                foundItem.isEnabled = true
            }

            myDebts.apply {
                isClickable = false
                isEnabled = false
            }
        }
    }

    private val retrieved: MutableLiveData<Boolean> = MutableLiveData(false)

    private fun loadMyDebts(
        load: Boolean? = true
    ): MutableLiveData<Boolean> {

        if (!isConnected()!!) {
            fabAddMyDebt.snackBuilder("Its better when online !", 2000).apply {
                setTextColor(Color.WHITE)
                setBackgroundTint(Color.GREEN)

                setActionTextColor(Color.YELLOW)
                setAction("switch") {
                    // todo open internet connection settings
                }
                show()
            }
        }

        activateBtmNav(false)
        if (load!!) loadingBar(load) else loadingBar(false)
        noDebts.visibility = INVISIBLE

        val viewModel = ViewModelProvider(this).get(GetDataViewModel::class.java)
        viewModel.getData("myDebts")?.addOnCompleteListener { it ->
            if (it.isSuccessful) {
                retrieved.value = true
                activateBtmNav(true)
                loadingBar(false)

                myDebtsList = it.result!!.toObjects(DebtData::class.java)
                myDebtsAdapter.myDebtsList = myDebtsList
                myDebtsAdapter.notifyDataSetChanged()

                val numMyDebts = myDebtsList.size

                noDebts.visibility = if (numMyDebts == 0) VISIBLE else INVISIBLE

                activity!!.getSharedPreferences(TOTAL_MY_DEBTS, 0).edit().putFloat(
                    TOTAL_MY_DEBTS, numMyDebts.toFloat()
                ).apply()
            } else {
                activateBtmNav(true)
                loadingBar(false)
            }
        }
        return retrieved
    }

    private fun loadingBar(show: Boolean) {
        if (show) {
            dataLoadingLayout2.visibility = VISIBLE
            loadingRing2.apply {
                setViewColor(ActivityCompat.getColor(this.context, R.color.colorGreen))
                startAnim()
            }
        } else {
            dataLoadingLayout2.visibility = INVISIBLE
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