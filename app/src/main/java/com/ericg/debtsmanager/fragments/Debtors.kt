package com.ericg.debtsmanager.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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

    override fun onStart() {
        super.onStart()
        debtorsRecyclerView.apply {
            adapter = DebtorsAdapter()
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        }

        fabAddDebtor.setOnClickListener {
            showAddDebtorDialog()
        }
    }

    @SuppressLint("InflateParams")
    private fun showAddDebtorDialog() {
        val addDebtor = AlertDialog.Builder(this.context)
        val addDebtorView = layoutInflater.inflate(R.layout.dialog_add_debtor, null)
        /**
         *       set today as the max starting date and the min deadline (avoid )
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