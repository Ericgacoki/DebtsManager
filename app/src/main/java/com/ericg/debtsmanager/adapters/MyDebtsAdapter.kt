/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */

package com.ericg.debtsmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ericg.debtsmanager.R
import kotlinx.android.synthetic.main.row_mydebt_item.view.*


class MyDebtsAdapter : RecyclerView.Adapter<MyDebtsAdapter.MViewHolder>() {
    // just testing
    val nums = (20 downTo 1)
    val conv = nums.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        val myDebtItem =
            LayoutInflater.from(parent.context).inflate(R.layout.row_mydebt_item, parent, false)
        return MViewHolder(myDebtItem)
    }

    override fun getItemCount() = conv.size

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {

        val text = conv[position].toString()
        holder.view.myDebtText.text = text
    }

    class MViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}