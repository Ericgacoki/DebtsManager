/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */

package com.ericg.debtsmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ericg.debtsmanager.R
import kotlinx.android.synthetic.main.row_debtor_item.view.*

class DebtorsAdapter : RecyclerView.Adapter<DebtorsAdapter.CustomDebtorViewHolder>() {
    private val debtorNames = arrayOf(
        "Eric Griezeman",
        "Diana mson",
        "Jane briasert",
        "Scquill Ngatha",
        "Sergey maureen",
        "Allan mohan",
        "Shyu Kim",
        "Mark Android",
        "Shiny Kotlin",
        "Good Python",
        "Old Java"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomDebtorViewHolder {

        val debtorItem: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_debtor_item, parent, false)
        return CustomDebtorViewHolder(
            debtorItem
        )
    }

    override fun getItemCount() = debtorNames.size
    // TODO() return the number of debtors in current user database. If its null, return 0 instead
    // test the recyclerView for now


    override fun onBindViewHolder(holder: CustomDebtorViewHolder, position: Int) {
        /**
        TODO() populate the recyclerView with data from current user database
        the debtor data includes: name, debtAmount, date, due, progress, phone
         */
        // testing
        val name = debtorNames[position]
        holder.view.debtorName.text = name
    }

    class CustomDebtorViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}

