/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */

package com.ericg.debtsmanager.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.adapters.DebtorsAdapter
import com.ericg.debtsmanager.data.DebtData
import com.ericg.debtsmanager.extensions.openAddDebtFragment
import com.ericg.debtsmanager.extensions.toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_add_debt_payment.*
import kotlinx.android.synthetic.main.dialog_add_debt_payment.view.*
import kotlinx.android.synthetic.main.fragment_add_debt.view.*
import kotlinx.android.synthetic.main.dialog_edit_debtor.view.*
import kotlinx.android.synthetic.main.fragment_debtors.*
import java.util.*

class Debtors : Fragment(), DebtorsAdapter.OnDebtorClickListener {

    // fetch data from firestore

    private val debtor1 = DebtData(
        "Eric", "Feb 1 2020", "Aug 3 2020", 0, "07245778346",
        5000, 4530, 1, null, null
    )


    private val debtor2 = DebtData(
        "Jane", "Feb 1 2020", "Aug 3 2020", 1, "17245778346",
        2000, 1000, 2, null, null
    )


    private val debtor3 = DebtData(
        "Marry", "jan 1 2020", "Aug 3 2020", 1,
        "2", 2361, 0, 0, null, null
    )

    private val debtor4 = DebtData(
        "John", "may 1 2020", "Aug 3 2022", 0, "37245778346",
        21000, 0, 0, null, null
    )

    private val debtor5 = DebtData(
        "Faith", "june 1 2020", "dec 3 2020", 0, "47245778346",
        3000, 0, 2, null, null
    )

    private val debtor6 = DebtData(
        "Ken brissat", "july 1 2020", "Aug 3 2021", 1, "5",
        2370, 0, 2, null, null
    )

    private val debtor7 = DebtData(
        "Maya hendrig", "Mar 1 2020", "Aug 3 2020", 0, "6",
        4283, 0, 3, null, null
    )


    val debtorsList: ArrayList<DebtData> =
        arrayListOf(debtor1, debtor2, debtor3, debtor4, debtor5, debtor6, debtor7)

    private val debtorsAdapter =
        DebtorsAdapter(
            thisContext = null,
            debtorsList = debtorsList,
            listener = this
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_debtors, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()
        onSwipeToRefresh()
        fabAddDebtor.setOnClickListener { openAddDebtFragment() }
    }

    @Suppress("DEPRECATION")
    private fun deactivateBottomNavigation(duration: Long = 5000) {
        val navItems =
            arrayOf(R.id.debtors, R.id.myDebts,R.id.profile /*R.id.loans, R.id.installments*/ )
        navItems.forEach { item ->
            val foundItem = activity!!.findViewById<View>(item)
            foundItem.isClickable = false
            foundItem.isEnabled = false
        }

        Handler().postDelayed({
            navItems.forEach { item ->
                val foundItem = activity!!.findViewById<View>(item)
                foundItem.isClickable = true
                foundItem.isEnabled = true
            }
        }, duration)
    }

    @Suppress("DEPRECATION")
    private fun onSwipeToRefresh() {
        swipeToRefreshDebtors.setOnRefreshListener {
            deactivateBottomNavigation()

            /** @Testing */
            debtorsList.apply {

                add(
                    DebtData(
                        "Added Eric 2", "Feb 1 2020", "Aug 3 2020", 1, "1234788743",
                        8000, 7300, 2, null, null
                    )
                )


                add(
                    DebtData(
                        "Added kevoh", "Feb 1 2020", "Aug 3 2020", 0, "1234788743",
                        2000, 2000, 1, null, null
                    )
                )

                add(
                    DebtData(
                        "Nurse sharon", "Feb 1 2020", "Aug 3 2020", 0, "01234788743",
                        70000, 59330, 0, null, null
                    )
                )
            }

            debtorsAdapter.notifyDataSetChanged()
            Handler().postDelayed(
                {
                    updateUI()
                    toast("refreshed successfully")
                    swipeToRefreshDebtors.isRefreshing = false
                },
                2000
            )
        }
    }

    override fun onItemClick(itemView: View, itemViewId: Int, position: Int) {

        when (itemViewId) {
            R.id.deleteDebtor -> {
                val name = debtorsList[position].name
                val message: String = if (debtorsList[position].remainingAmt == 0) {
                    "It's safe to delete $name"
                } else {
                    "$name hasn't cleared the debt yet!"
                }
                val themeResId: Int = if (debtorsList[position].remainingAmt == 0) 3 else 1

                val deleteDebtorDialogBuilder = AlertDialog.Builder(this.context, themeResId)
                deleteDebtorDialogBuilder.apply {
                    setTitle("Sure to delete ?")
                    setIcon(
                        ContextCompat.getDrawable(
                            this@Debtors.context!!,
                            R.drawable.ic_warning
                        )
                    )
                    setMessage(message)
                    setPositiveButton("proceed") { _, _ ->
                        removeDebtorAt(position)
                        toast("Debtor number ${position + 1} -> $name removed")

                    }
                    setNegativeButton("cancel") { _, _ ->
                        // cancel dialog
                    }
                    create().show()
                }
            }
            R.id.editDebtor -> {
                toast("edit the necessary details for ${debtorsList[position].name}")

                val name = debtorsList[position].name
                val phone = debtorsList[position].phone
                val currentDebt = debtorsList[position].remainingAmt
                val debtDueDate = debtorsList[position].dueDate

                val editDialogBuilder = BottomSheetDialog(this.context!!)
                val customEditDialogView = layoutInflater.inflate(R.layout.dialog_edit_debtor, null)

                customEditDialogView.apply {
                    editDebtorName.setText(name)
                    editDebtorPhone.setText(phone)
                    editDebtDueDate.setText(debtDueDate)

                    cancelEditing.setOnClickListener {
                        editDialogBuilder.dismiss()
                    }
                }
                editDialogBuilder.apply {
                    setContentView(customEditDialogView)
                    setCancelable(false)
                    create()
                    show()
                }
            }
            R.id.debtStatus -> {
                val message = if (debtorsList[position].debtStatus == 1) "on time" else "too late"
                toast(message)
            }
            R.id.expandDebtorCard -> {

                /*if (itemView.expandableDebtorCardLayout!!.visibility == View.GONE) {
                    // delay card transition (in this case, expansion)
                    TransitionManager.beginDelayedTransition(debtorCard, AutoTransition())

                    itemView.expandableDebtorCardLayout!!.visibility = VISIBLE
                    itemView.expandDebtorCard.setImageDrawable(context?.let {
                        ContextCompat.getDrawable(it, R.drawable.ic_up) })
                    toast("expanded card  for ${debtorsList[position].name}")

                } else {
                    expandableDebtorCardLayout!!.visibility = View.GONE
                    expandDebtorCard.setImageDrawable(context?.let { context ->
                        ContextCompat.getDrawable(
                            context, R.drawable.ic_down) })
                    toast("collapsed card  for ${debtorsList[position].name}")
                }*/

            }

            R.id.callDebtor -> {
                toast("proceed to call  ${debtorsList[position].name}")
                val callIntent = Intent(
                    Intent.ACTION_CALL,
                    Uri.parse("tel:${debtorsList[position].phone}")
                )
                try {
                    startActivity(callIntent)
                } catch (e: Exception) {
                    toast(e.toString())
                }
            }

            R.id.debtPaymentProgress -> {
                val debtor = debtorsList[position].name
                val amtPaid = debtorsList[position].amtPaid
                toast("$debtor has  paid $amtPaid")
            }

            R.id.addDebtPayment -> {
                val remainingAmt = debtorsList[position].remainingAmt
                val name = debtorsList[position].name
                var saved = false

                if (remainingAmt!! >= 1) {
                    toast("add payment for $name")
                    val paymentBuilder = AlertDialog.Builder(this.context)
                    val paymentView = layoutInflater.inflate(R.layout.dialog_add_debt_payment, null)

                    paymentView.apply {
                        debtPaymentTitle.text = "Add payment for $name"

                        saveDebtPayment.setOnClickListener {

                            if (newPaymentAmt.text.toString().isNotEmpty() &&
                                newPaymentAmt.text.toString().toInt() <= remainingAmt &&
                                newPaymentAmt.text.toString().toInt() > 0
                            ) {

                                val newPayedAmt = newPaymentAmt.text.toString().toInt()

                                //TODO  update to firestore and refresh                               .

                                val debtorCopy = debtorsList[position]
                                val newAmtPaid = (debtorCopy.amtPaid + newPayedAmt)
                                val newPaymentsDone = debtorCopy.paymentsDone + 1

                                val newDebtorCopy = DebtData(
                                    debtorCopy.name,
                                    debtorCopy.startDate,
                                    debtorCopy.dueDate,
                                    1,
                                    debtorCopy.phone,
                                    debtorCopy.initialAmt,
                                    newAmtPaid,
                                    newPaymentsDone,
                                    null,
                                    null
                                )
                                /* save once to avoid overpayment and negative balance */
                                if (!saved) {
                                    debtorsList.removeAt(position)
                                    debtorsAdapter.notifyItemRemoved(position)
                                    debtorsList.add(position, newDebtorCopy)
                                    debtorsAdapter.notifyItemInserted(position)
                                    debtorsAdapter.notifyDataSetChanged()
                                    debtorsRecyclerView.scrollToPosition(position)
                                    toast("saved successfully")
                                    saved = true
                                   // paymentBuilder.show().dismiss()

                                } else toast("you can only save once at a time")

                            } else if (newPaymentAmt.text.toString().isEmpty()) {
                                newPaymentAmt.error = "Can't be empty"
                            } else {
                                toast("enter amount between 1 and $remainingAmt inclusive")
                            }
                        }

                        eraseDebtPayment.setOnClickListener {
                            newPaymentAmt.setText("")
                        }
                    }

                    paymentBuilder.apply {
                        setNegativeButton("") { _, _ -> eraseDebtPayment.setOnClickListener { } }
                        setView(paymentView)
                        setCancelable(true)
                        create()
                        show()
                    }
                } else {
                    toast("This debt is cleared!")
                }
            }
        }
    }

    private fun removeDebtorAt(position: Int) {
        // TODO remove from firestore and refresh the data
        debtorsList.removeAt(position)
        debtorsAdapter.notifyItemRemoved(position)
        debtorsAdapter.notifyDataSetChanged()
    }

    private fun updateUI() {
        debtorsRecyclerView.apply {
            adapter = debtorsAdapter
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            // scroll to bottom
           scrollToPosition(debtorsList.size - 1)
            setOnScrollChangeListener { _: View, _: Int, _: Int, _: Int, _: Int ->
                fabAddDebtor.startAnimation(
                    android.view.animation.AnimationUtils.loadAnimation(
                        context, R.anim.hide_on_scroll
                    )
                )
            }
            /*
            addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                    fabAddDebtor.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(
                            context ,R.anim.hide_on_scroll
                        )
                    )
                    if (dy > 0){
                        searchDebtor.visibility = INVISIBLE
                    }
                    else {
                        searchDebtor.visibility = VISIBLE
                    }
                    super.onScrolled(recyclerView, dx, dy)
                }
            }) */
        }
    }
}