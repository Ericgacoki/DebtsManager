/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */

package com.ericg.debtsmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ericg.debtsmanager.R

class LoansAdapter : RecyclerView.Adapter<LoansAdapter.LViewHolder>() {

    class LViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LViewHolder {
        val loanItem =
            LayoutInflater.from(parent.context).inflate(R.layout.row_loan_item, parent, false)
        return LViewHolder(loanItem)
    }

    override fun getItemCount() = 0

    override fun onBindViewHolder(holder: LViewHolder, position: Int) {
    }
}