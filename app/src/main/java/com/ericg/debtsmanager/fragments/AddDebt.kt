/*
 * Copyright (c)  Updated by eric on  9/12/20 12:17 AM
 */

package com.ericg.debtsmanager.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color.RED
import android.graphics.Color.WHITE
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.data.DebtData
import com.ericg.debtsmanager.extensions.selectImage
import com.ericg.debtsmanager.extensions.snackBuilder
import com.ericg.debtsmanager.extensions.toast
import com.ericg.debtsmanager.utils.SaveDebt
import kotlinx.android.synthetic.main.fragment_add_debt.*
import kotlinx.android.synthetic.main.new_row_debtor_item.view.*
import kotlinx.android.synthetic.main.row_mydebt_item.view.*
import java.util.*
import kotlin.properties.Delegates

/**
 * @author eric
 * @date 8/21/20
 */
private const val SELECT_DEBT_PROFILE_PIC = 5
private lateinit var debtDpBitMap: Bitmap

class AddDebt : Fragment() {

    private val today = Calendar.getInstance().timeInMillis
    private lateinit var debtType: String  // Debtor or My Debt from spinner

    lateinit var userName: String
    lateinit var startDate: String
    lateinit var dueDate: String
    private var debtStatus: Int = 1  // good
    lateinit var debtPhone: String
    private var initialAmt by Delegates.notNull<Int>()
    private var amtPaid: Int = 0
    private var paymentsDone: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_debt, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateCalendars()
        updateSpinner()
        handleClicks()
    }

    private fun updateCalendars() {
        // set date restrictions
        selectDebtStartDate.maxDate = today
        selectDebtDueDate.minDate = today
    }

    private fun updateSpinner() {
        val debtTypes: Array<String> = arrayOf("Debtor", "My Debt")
        val spinnerAdapter: ArrayAdapter<String> =
            ArrayAdapter(this.context!!, R.layout.support_simple_spinner_dropdown_item, debtTypes)

        spinnerDebtDataType.adapter = spinnerAdapter

        spinnerDebtDataType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // this can never be true in our case
            }

            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedType = adapterView?.getItemAtPosition(position).toString()
                toast("selected type is $selectedType")
                debtType = selectedType
            }
        }
    }

    private fun handleClicks() {

        addDebtName.setOnClickListener { alterFieldsFocus(false) }
        addDebtAmount.setOnClickListener { alterFieldsFocus(false) }
        addDebtPhone.setOnClickListener { alterFieldsFocus(false) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)

            selectDebtStartDate.apply {
                setOnClickListener { alterFieldsFocus(true) }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setOnDateChangedListener { datePicker: DatePicker, i: Int, i1: Int, i2: Int ->
                        alterFieldsFocus(true)
                    }
                }
            }

        selectDebtDueDate.apply {
            setOnClickListener { alterFieldsFocus(true) }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setOnDateChangedListener { datePicker: DatePicker, i: Int, i1: Int, i2: Int ->
                    alterFieldsFocus(true)
                }
            }
        }

        btnExitAddDebt.setOnClickListener {
            super.getActivity()!!.onBackPressed()
        }

        debtProfilePic.setOnClickListener {
            activity?.selectImage(SELECT_DEBT_PROFILE_PIC)
        }

        btnPreviewDebt.setOnClickListener {
            previewDebt()
        }

        btnSaveDebt.setOnClickListener {
            saveDebt()
        }
    }

    private fun alterFieldsFocus(clearFocus: Boolean) {
        val inputFields = arrayOf(addDebtName, addDebtAmount, addDebtPhone)
        if (clearFocus) {
            inputFields.forEach {
                it.clearFocus()
            }
        } else {
            inputFields.forEach {
                it.requestFocus()
            }
        }
    }

    private var notEmpty: Boolean = false

    private fun collectData() {
        val inputs = arrayOf(addDebtName, addDebtAmount, addDebtPhone)
        notEmpty =
            addDebtName.text.toString().trim().isNotEmpty() &&
                    addDebtAmount.text.toString().trim().isNotEmpty() &&
                    addDebtPhone.text.toString().trim().isNotEmpty()

        if (notEmpty) {
            userName = addDebtName.text.toString().trim()
            initialAmt = addDebtAmount.text.toString().trim().toInt()
            debtPhone = addDebtPhone.text.toString().trim()

            val sDSD = selectDebtStartDate
            startDate = "${sDSD.dayOfMonth}/ ${sDSD.month + 1}/ ${sDSD.year}"

            val sDDD = selectDebtDueDate
            dueDate = "${sDDD.dayOfMonth}/ ${sDDD.month + 1} /${sDDD.year}"

        } else {
            inputs.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
            addDebtScrollView.scrollTo(0, 0)
        }
    }

    @SuppressLint("InflateParams")
    private fun previewDebt() {
        collectData()
        if (notEmpty) alterFieldsFocus(true)

        val previewDebtDialogBuilder = AlertDialog.Builder(this.context)
        var layout = R.layout.new_row_debtor_item // default

        if (::debtType.isInitialized) {
            layout = if (debtType == "Debtor") {
                R.layout.new_row_debtor_item
            } else {
                R.layout.row_mydebt_item
            }
        }

        val view: View
        if (layout == R.layout.new_row_debtor_item) {
            view = layoutInflater.inflate(R.layout.new_row_debtor_item, null)

            // set debtor values
            if (notEmpty) {
                view.apply {
                    debtorName.text = userName
                    debtAmount.text = initialAmt.toString()
                    debtDate.text = startDate
                    debtDueDate.text = dueDate
                    debtStatus.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@AddDebt.context as Context,
                            R.drawable.ic_on_time
                        )
                    )
                    debtorPhone.text = debtPhone

                    debtsInitialAmount.text = initialAmt.toString()
                    debtPaymentPercentage.text = "0 %"
                    debtsNumPaymentsDone.text = paymentsDone.toString()

                }
            }

        } else {
            view = layoutInflater.inflate(R.layout.row_mydebt_item, null)

            // set my debt values
            if (notEmpty) {
                view.apply {
                    myDebtName.text = userName
                    myDebtAmt.text = initialAmt.toString()
                    myDebtDate.text = startDate
                    myDebtDueDate.text = dueDate
                    myDebtStatus.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@AddDebt.context as Context,
                            R.drawable.ic_on_time
                        )
                    )
                    myDebtPhone.text = debtPhone

                    myDebtInitialAmt.text = initialAmt.toString()
                    myDebtProgressPercentage.text = "0 %"
                    myDebtNumPayments.text = paymentsDone.toString()
                }
            }

        }
        if (notEmpty) {
            previewDebtDialogBuilder.apply {
                setView(view)
                setCancelable(true)
                create().show()
            }
        }
    }

    private fun saveDebt() {
        collectData()
        if (notEmpty) {
            if (addDebtAmount.text.toString().trim().toInt() > 0) {
                alterFieldsFocus(true)

                btnSaveDebt.isClickable = false
                btnSaveDebt.isEnabled = false

                val newDebt = DebtData(
                    name = userName,
                    startDate = startDate,
                    dueDate = dueDate,
                    debtStatus = debtStatus,
                    phone = debtPhone,
                    initialAmt = initialAmt,
                    amtPaid = amtPaid,
                    paymentsDone = paymentsDone,
                    remainingAmt = null,
                    progressPercentage = null
                )

                val confirmSaveDebt = AlertDialog.Builder(this.context as Context, 3)
                confirmSaveDebt.apply {
                    setTitle("sure to save new $debtType ?")

                    setPositiveButton("Ok") { _, _ ->
                        /*         SaveDebt uses a coroutine so we don't have to do it again      */

                        SaveDebt(debtType, newDebt).apply {
                            done().observe(viewLifecycleOwner, { success ->
                                // todo check internet connection

                                when (success) {
                                    true -> {
                                        showSuccessDialog()
                                    }
                                    else -> {
                                        toast("failed to save!")
                                    }
                                }
                            })
                        }
                    }
                    setNegativeButton("cancel") { _, _ -> /* dismiss */ }
                    setOnDismissListener {
                        btnSaveDebt.apply {
                            isEnabled = true
                            isClickable = true
                        }
                    }
                    create().show()
                }
            } else {
                btnSaveDebt.snackBuilder("invalid amount input", 2000).apply {
                    setTextColor(WHITE)
                    setBackgroundTint(RED)
                    show()
                }

                addDebtAmount.error = "must be greater than zero"
                addDebtScrollView.scrollTo(0, 0)
            }
        }
    }

    // todo check Network
    private fun checkNet(): Boolean {
        var isConnected = false


        return isConnected
    }

    private fun onDismissAction() {

        val fragment: Fragment = if (debtType == "Debtor") Debtors() else MyDebts()

        parentFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    @SuppressLint("InflateParams")
    private fun showSuccessDialog() {
        val saved = AlertDialog.Builder(this.context)
        val view = layoutInflater.inflate(R.layout.dialog_debt_saving_done, null)

        saved.apply {
            setOnDismissListener { onDismissAction() }
            setView(view)
            show()
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        if (/*requestCode == SELECT_DEBT_PROFILE_PIC &&*/ /*resultCode == Activity.RESULT_OK && */ data != null && data.data != null) {
            debtDpBitMap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, data.data)
            activity!!.debtProfilePic?.setImageBitmap(debtDpBitMap)
        }
    }

    override fun onStart() {
        super.onStart()
        if (::debtDpBitMap.isInitialized) {
            activity!!.debtProfilePic?.setImageBitmap(debtDpBitMap)
        }
    }
}