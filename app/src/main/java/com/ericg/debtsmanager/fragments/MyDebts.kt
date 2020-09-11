/*
 * Copyright (c)  Updated by eric on  9/12/20 12:17 AM
 */

package com.ericg.debtsmanager.fragments

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
import com.ericg.debtsmanager.extensions.toast
import com.ericg.debtsmanager.viewmodel.GetDataViewModel
import kotlinx.android.synthetic.main.fragment_my_debts.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

        fabAddMyDebt.setOnClickListener { openAddDebtFragment() }
        dataLoadingLayout2.setOnClickListener { /* do nothing*/ }

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
/*
    override fun onResume() {
        super.onResume()
        loadMyDebts()
    }*/

    private fun activateBtmNav(activate: Boolean) {
        val navItems =
            arrayOf(
                R.id.debtors,
                R.id.myDebts,
                R.id.profile /*R.id.loans, R.id.installments*/,
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
        cancel: Boolean? = false,
        load: Boolean? = true
    ): MutableLiveData<Boolean> {
        activateBtmNav(false)
        if (load!!) loadingBar(load) else loadingBar(false)
        noDebts.visibility = INVISIBLE

        val viewModel = ViewModelProvider(this).get(GetDataViewModel::class.java)
        val job = GlobalScope.launch(Dispatchers.IO) {
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
        }

        if (cancel!!) {
            job.cancel()
        }

        return retrieved
    }

    private fun loadingBar(show: Boolean) {
        if (show) {
            dataLoadingLayout2.visibility = VISIBLE
            loadingRing2.apply {
                setViewColor(ActivityCompat.getColor(this.context, R.color.colorOrange))
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