/*
 * Copyright (c)  Updated by eric on  10/1/20 11:52 PM
 */

package com.ericg.debtsmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.data.DebtData
import kotlinx.android.synthetic.main.new_row_debtor_item.view.*


class DebtorsAdapter(
    thisContext: Context?,
    var debtorsList: List<DebtData>,
    private val listener: OnDebtorClickListener
) : RecyclerView.Adapter<DebtorsAdapter.CustomDebtorViewHolder>() {

    private var lastPosition = -1
    private var context = thisContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomDebtorViewHolder {

        val debtorItem: View =
            LayoutInflater.from(parent.context).inflate(R.layout.new_row_debtor_item, parent, false)
        return CustomDebtorViewHolder(debtorItem)
    }

    override fun getItemCount() = debtorsList.size

    override fun onBindViewHolder(holder: CustomDebtorViewHolder, position: Int) {

        holder.bind(
            debtorsList[position]
        )

        @NonNull
        context = holder.itemView.context

        if (holder.adapterPosition > lastPosition && context != null) {
            // when scrolling down
            holder.itemView.startAnimation(
                AnimationUtils.loadAnimation(
                    this.context,
                    R.anim.quick_from_bottom
                )
            )
            lastPosition = holder.adapterPosition

        } else {
            // when scrolling up
            if (context != null) {
                holder.itemView.startAnimation(
                    AnimationUtils.loadAnimation(
                        this.context,
                        R.anim.quick_from_top
                    )
                )
                lastPosition = holder.adapterPosition
            }
        }
    }

    inner class CustomDebtorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        // Upper card info

        private val debtorName: TextView = itemView.debtorName
        private val debtAmount: TextView = itemView.debtAmount
        private val debtDate: TextView = itemView.debtDate
        private val debtDueDate: TextView = itemView.debtDueDate
        private val debtorPhone: TextView = itemView.debtorPhone

        private val debtorProfilePic: ImageView = itemView.debtorProfilePic
        private val debtStatus: ImageView = itemView.debtStatus

        // Bottom card info

        private val debtInitialAmt = itemView.debtsInitialAmount
        private val debtPaymentProgressBar: ProgressBar = itemView.debtPaymentProgress
        private val debtPaymentPercentage: TextView = itemView.debtPaymentPercentage

        // Card ImageViews (used as  Buttons)

        private val expandDebtorCard: ImageView = itemView.expandDebtorCard
        private val deleteDebtor: ImageView = itemView.deleteDebtor
        private val editDebtor: ImageView = itemView.editDebtor
        private val callDebtor: ImageView = itemView.callDebtor

        // More info
        private val addPayment: TextView = itemView.addDebtPayment
        private val numOfPayments: TextView = itemView.debtsNumPaymentsDone
        private val addReminder:TextView = itemView.AddDebtPaymentReminder

        fun bind(
            debtData: DebtData
        ) {

            // todo load profile picture with glide

            debtorName.text = debtData.name
            debtorPhone.text = debtData.phone
            debtDate.text = debtData.startDate
            debtDueDate.text = debtData.dueDate
            debtAmount.text = (debtData.initialAmt - debtData.amtPaid).toString()
            debtInitialAmt.text = debtData.initialAmt.toString()
            numOfPayments.text = debtData.paymentsDone.toString()
            debtPaymentProgressBar.progress = debtData.amtPaid * 100 / debtData.initialAmt
            debtPaymentPercentage.text = "${debtData.amtPaid * 100 / debtData.initialAmt} %"
        }

        init {
            // set click listener to specific views
            itemView.setOnClickListener(this)
            expandDebtorCard.setOnClickListener(this)
            debtStatus.setOnClickListener(this)
            editDebtor.setOnClickListener(this)
            callDebtor.setOnClickListener(this)
            deleteDebtor.setOnClickListener(this)

            debtPaymentProgressBar.setOnClickListener(this)
            addReminder.setOnClickListener(this)
            addPayment.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION && view != null) {
                listener.onItemClick(view, view.id, position)
            }
        }
    }

    interface OnDebtorClickListener {
        fun onItemClick(itemView: View, itemViewId: Int, position: Int)
    }
}