/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */

package com.ericg.debtsmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ericg.debtsmanager.R

class AnalysisAdapter : RecyclerView.Adapter<AnalysisAdapter.AnalysisViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalysisViewHolder {
        val analysisItem =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_analysis_item, parent, false)
        return AnalysisViewHolder(analysisItem)
    }

    override fun getItemCount() = 9

    override fun onBindViewHolder(holder: AnalysisViewHolder, position: Int) {

    }

    class AnalysisViewHolder(val view: View) : RecyclerView.ViewHolder(view)

}