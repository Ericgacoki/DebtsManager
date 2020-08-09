/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */

package com.ericg.debtsmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.fragments.Debtors
import kotlinx.android.synthetic.main.row_debtor_item.view.*

class DebtorsAdapter(context: Context) : RecyclerView.Adapter<DebtorsAdapter.CustomDebtorViewHolder>() {

    var lastPosition = -1
     val thisContext = context

    var debtorNames = arrayListOf(
        "Eric Griezeman",
        "Diana mson",
        "Jane briasert",
        "Schuyan moh",
        "Sergey maureen",
        "Mohammed mohan",
        "Shyu Kim",
        "Mark Android",
        "Shiny Kotlin",
        "Eric Griezeman",
        "Schuyan moh",
        "Sergey maureen",
        "Mohammed mohan",
        "Shyu Kim",
        "Mark Android",
        "Shiny Kotlin",
        "Eric Griezeman",
        "Diana mson",
        "Jane briasert",
        "Schuyan moh",
        "Sergey maureen",
        "Mohammed mohan",
        "Shyu Kim",
        "Mark Android",
        "Shiny Kotlin",
        "Good Python",
        "Very Old Java",
        "Shyu Kim 2",
        "Mark Android 2",
        "Shiny Kotlin 2",
        "Good Python 2",
        "Very Old Java 2"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomDebtorViewHolder {

        val debtorItem: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_debtor_item, parent, false)
        return CustomDebtorViewHolder(debtorItem)
    }

    override fun getItemCount() =  debtorNames.size
    // TODO() return the number of debtors in current user database. If its null, return 0 instead
    // test the recyclerView for now

    override fun onBindViewHolder(holder: CustomDebtorViewHolder, position: Int) {
        /**
        TODO() populate the recyclerView with data from current user database
        the debtor data includes: name, debtAmount, date, due, progress, phone
         */
        // testing

        if (holder.adapterPosition > lastPosition){
            holder.view.startAnimation(AnimationUtils.loadAnimation(thisContext, R.anim.from_right))
        val name = debtorNames[position]
        holder.view.debtorName.text = name} else{
            holder.view.startAnimation(AnimationUtils.loadAnimation(thisContext, R.anim.reverse))
        }
        lastPosition = holder.adapterPosition
    }

    class CustomDebtorViewHolder(val view: View) : RecyclerView.ViewHolder(view)

}

