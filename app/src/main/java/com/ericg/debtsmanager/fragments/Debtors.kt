/*
 * Copyright (c)  Updated by eric on  9/9/20 4:44 PM
 */

package com.ericg.debtsmanager.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.adapters.DebtorsAdapter
import com.ericg.debtsmanager.data.DebtData
import com.ericg.debtsmanager.extensions.openAddDebtFragment
import com.ericg.debtsmanager.extensions.toast
import com.ericg.debtsmanager.viewmodel.GetDataViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_add_debt_payment.*
import kotlinx.android.synthetic.main.dialog_add_debt_payment.view.*
import kotlinx.android.synthetic.main.dialog_edit_debtor.view.*
import kotlinx.android.synthetic.main.fragment_debtors.*

class Debtors : Fragment(), DebtorsAdapter.OnDebtorClickListener {

    private var debtorsList: List<DebtData> = ArrayList()

    private val debtorsAdapter =
        DebtorsAdapter(
            thisContext = null, debtorsList = debtorsList, listener = this
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

    private fun loadData(): MutableLiveData<Boolean> {
        activateBtmNav(false)
        val retrieved: MutableLiveData<Boolean> = MutableLiveData(false)

        val viewModel = ViewModelProvider(this).get(GetDataViewModel::class.java)
        viewModel.getData("Debtor")?.addOnCompleteListener { it ->
            if (it.isSuccessful) {
                retrieved.value = true
                activateBtmNav(true)
                debtorsList = it.result!!.toObjects(DebtData::class.java)
                debtorsAdapter.debtorsList = debtorsList
                debtorsAdapter.notifyDataSetChanged()

                viewModel.numOfDebtors = debtorsList.size.toFloat()

            } else {
                activateBtmNav(true)
                toast("refreshing...")
            }
        }
        return retrieved
    }

    private fun activateBtmNav(activate: Boolean) {
        val navItems =
            arrayOf(R.id.debtors, R.id.myDebts, R.id.profile /*R.id.loans, R.id.installments*/)

        if (!activate) {

            navItems.forEach { item ->
                val foundItem = activity!!.findViewById<View>(item)
                foundItem.isClickable = false
                foundItem.isEnabled = false
            }
        } else {
            navItems.forEach { item ->
                val foundItem = activity!!.findViewById<View>(item)
                foundItem.isClickable = true
                foundItem.isEnabled = true
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun onSwipeToRefresh() {
        swipeToRefreshDebtors.setOnRefreshListener {
           loadData().observe(viewLifecycleOwner, {
                if (it) {

                    toast("refreshed successfully")
                    swipeToRefreshDebtors.isRefreshing = false
                } else {
                    toast("error loading data")
                }
            })
        }
    }

    private lateinit var newDate: String
    private fun showCalendar() {

        val calendarDialog = AlertDialog.Builder(this.context)
        val view = layoutInflater.inflate(R.layout.dialog_calendar, null)
        /*view.apply {
            pickNewDueDate.minDate = Calendar.getInstance().timeInMillis
            btnNewDueDateOk.setOnClickListener {
                newDate =
                    "${pickNewDueDate.dayOfMonth} /${pickNewDueDate.month} /${pickNewDueDate.year}"
                editDebtDueDate.setText(newDate)
               // calendarDialog.create().dismiss()
            }
        }*/
        calendarDialog.apply {
            setView(view)
            create()
            show()
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
                        // removeDebtorAt(position)
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
                    editDebtDueDate.setOnClickListener {

                        showCalendar()
                    }

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
                                    // todo save the changes made to this persons data and reload
                                    saved = true

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

    }

    private fun updateUI() {
        loadData()
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
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }
}