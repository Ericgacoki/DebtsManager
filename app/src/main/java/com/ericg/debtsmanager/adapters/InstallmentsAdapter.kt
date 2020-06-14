/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */

package com.ericg.debtsmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ericg.debtsmanager.R

class InstallmentsAdapter : RecyclerView.Adapter<InstallmentsAdapter.IViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IViewHolder {
        val installmentItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_installment_item, parent, false)
        return IViewHolder(installmentItem)
    }

    override fun getItemCount() = 2

    override fun onBindViewHolder(holder: IViewHolder, position: Int) {
    }

    class IViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}