/*
 * Copyright (c)  Updated by eric on  9/4/20 3:40 AM
 */

package com.ericg.debtsmanager.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.data.DebtData
import com.ericg.debtsmanager.extensions.selectImage
import com.ericg.debtsmanager.extensions.toast
import com.ericg.debtsmanager.utils.SaveDebt
import kotlinx.android.synthetic.main.fragment_add_debt.*
import java.util.*

/**
 * @author eric
 * @date 8/21/20
 */
private const val SELECT_DEBT_PROFILE_PIC = 5
private lateinit var debtDpBitMap: Bitmap

private val today = Calendar.getInstance().timeInMillis
private lateinit var debtType: String  // Debtor or My Debt from spinner

class AddDebt : Fragment() {

/*
    lateinit var userName: String
    lateinit var startDate: String
    lateinit var dueDate: String
    private var status: Int = 1  // good
    lateinit var userPhone: String
    var initialAmt by Delegates.notNull<Int>()
    var amtPaid by Delegates.notNull<Int>()
    var paymentsDone: Int = 0*/


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

    private fun previewDebt() {
        // TODO collect data from relevant fields and assign to the corresponding late init variables

        val previewDebtDialogBuilder = AlertDialog.Builder(this.context)
        var layout = R.layout.new_row_debtor_item // default

        if (::debtType.isInitialized) {
            layout = if (debtType == "Debtor") {
                R.layout.new_row_debtor_item
            } else {
                R.layout.row_mydebt_item
            }
        }

        val view = layoutInflater.inflate(layout, null)
        view.apply {
            //TODO set collected data to respective fields
        }

        previewDebtDialogBuilder.apply {
            // activity?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setView(view)
            setCancelable(true)
            create().show()
        }
    }

    private fun saveDebt() {
        SaveDebt(
            debtType,
            DebtData(
                "Eric gacoki",
                "20/01/2020",
                "13/11/2021",
                1,
                "0716965216",
                21450,
                8075,
                1,
                null,
                null
            )
        )
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