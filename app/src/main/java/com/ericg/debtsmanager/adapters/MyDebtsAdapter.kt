/*
 * Copyright (c)  Updated by eric on  9/5/20 1:16 AM
 */

package com.ericg.debtsmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.data.DebtData
import kotlinx.android.synthetic.main.row_mydebt_item.view.*


class MyDebtsAdapter(
    private val myDebtsList: ArrayList<DebtData>,
    private val clickListener: MyDebtItemClickListener
) : RecyclerView.Adapter<MyDebtsAdapter.MyDebtViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyDebtViewHolder {
        val myDebtItem =
            LayoutInflater.from(parent.context).inflate(R.layout.row_mydebt_item, parent, false)
        return MyDebtViewHolder(myDebtItem)
    }

    override fun getItemCount() = myDebtsList.size

    override fun onBindViewHolder(holder: MyDebtViewHolder, position: Int) {

        val debtAtPosition = myDebtsList[position]

        holder.bind(
            name = debtAtPosition.name,
            startDate = debtAtPosition.startDate,
            dueDate = debtAtPosition.dueDate,
            phone = debtAtPosition.phone,
            initialAmt = debtAtPosition.initialAmt,
            paymentsDone = debtAtPosition.paymentsDone.toString(),
            remainingAmt = debtAtPosition.remainingAmt!!.toInt(),
            progressPercentage = debtAtPosition.progressPercentage!!.toInt()
        )
    }

    inner class MyDebtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        // md == myDebt

        private val mdName: TextView = itemView.myDebtName
        private val mdRemainingAmt: TextView = itemView.myDebtAmt
        private val mdDate: TextView = itemView.myDebtDate
        private val mdDueDate: TextView = itemView.myDebtDueDate
        private val mdPhone: TextView = itemView.myDebtPhone

        private val mdProfilePic: ImageView = itemView.myDebtProfilePic
        //private val mdStatus: TextView = itemView.myDebtStatus

        private val mdInitialAmt: TextView = itemView.myDebtInitialAmt
        private val mdPaymentProgressBar: ProgressBar = itemView.myDebtProgressBar
        private val mdPaymentPercentage: TextView = itemView.myDebtProgressPercentage

        private val mdDeleteMyDebt: ImageView = itemView.btnDeleteMyDebt
        private val mdEditMyDebt: ImageView = itemView.btnEditMyDebt
        private val mdCall: ImageView = itemView.btnCallMyDebt

        private val mdAddPayment: TextView = itemView.btnMyDebtAddPayment
        private val mdNumPayments: TextView = itemView.myDebtNumPayments
        private val mdAddReminder: TextView = itemView.btnMyDebtAddReminder

        fun bind(
            name: String,
            startDate: String,
            dueDate: String,
            phone: String,
            initialAmt: Int,
            paymentsDone: String,
            remainingAmt: Int,
            progressPercentage: Int
        ) {
            // todo load profile picture with glide
            mdName.text = name
            mdPhone.text = phone
            mdDate.text = startDate
            mdDueDate.text = dueDate
            mdInitialAmt.text = initialAmt.toString()
            mdNumPayments.text = paymentsDone
            mdRemainingAmt.text = remainingAmt.toString()
            mdPaymentProgressBar.progress = progressPercentage
            mdPaymentPercentage.text = "$progressPercentage %"
        }

        init {
            itemView.setOnClickListener(this)
            mdDeleteMyDebt.setOnClickListener(this)
            mdEditMyDebt.setOnClickListener(this)
            mdCall.setOnClickListener(this)

            mdPaymentProgressBar.setOnClickListener(this)
            mdProfilePic.setOnClickListener(this)

            mdAddPayment.setOnClickListener(this)
            mdAddReminder.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION && view != null) {
                clickListener.onMyDebtClicked(adapterPosition, view, view.id)
            }
        }
    }

    interface MyDebtItemClickListener {
        fun onMyDebtClicked(position: Int, itemView: View, viewId: Int)
    }
}