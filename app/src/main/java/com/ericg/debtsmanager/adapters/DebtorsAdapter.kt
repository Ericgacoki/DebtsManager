/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */

package com.ericg.debtsmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ericg.debtsmanager.R
import kotlinx.android.synthetic.main.row_debtor_item.view.*
import kotlin.coroutines.coroutineContext

class DebtorsAdapter(context: Context) :
    RecyclerView.Adapter<DebtorsAdapter.CustomDebtorViewHolder>() {

    private var lastPosition = -1
    private val thisContext = context

    private var debtorNames = arrayListOf(
        "Kevoh Chiwa",
        "Kevoh Chiwa 2",
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
        "Diana mson"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomDebtorViewHolder {

        val debtorItem: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_debtor_item, parent, false)
        return CustomDebtorViewHolder(debtorItem)
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


        holder.view.editDebtor.setOnClickListener {
            debtorNames.add(position,"added 1")
            DebtorsAdapter(thisContext).apply{
                notifyDataSetChanged()
                }
            Toast.makeText(thisContext, "you added debtor ${position +1} -> $name." , Toast.LENGTH_SHORT).show()
        }
        
        holder.view.expandDebtorCard.setOnClickListener {
            Toast.makeText(thisContext, "expand debtor $position for $name ", Toast.LENGTH_SHORT).show()
        }

        if (holder.adapterPosition > lastPosition) {
            // when scrolling down
            holder.view.startAnimation(
                AnimationUtils.loadAnimation(
                    thisContext,
                    R.anim.quick_from_bottom
                )
            )
            lastPosition = holder.adapterPosition

        } else {
            holder.view.startAnimation(
                AnimationUtils.loadAnimation(
                    thisContext,
                    R.anim.quick_from_top
                )
            )
            lastPosition = holder.adapterPosition
        }
    }

    class CustomDebtorViewHolder(val view: View) : RecyclerView.ViewHolder(view)


}

