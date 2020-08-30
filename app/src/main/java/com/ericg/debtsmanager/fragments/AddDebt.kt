/*
 * Copyright (c)  Updated by eric on  8/21/20 6:20 PM
 */

package com.ericg.debtsmanager.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.extensions.selectImage
import com.ericg.debtsmanager.extensions.toast
import kotlinx.android.synthetic.main.fragment_add_debt.*
import java.util.*

/**
 * @author eric
 * @date 8/21/20
 */
private const val SELECT_DEBT_PROFILE_PIC = 5
private lateinit var debtDpBitMap: Bitmap

class AddDebt : Fragment() {

    private val today = Calendar.getInstance().timeInMillis

    lateinit var debtType: String  // Debtor or My Debt from spinner

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
        selectDebtDueDate.minDate   = today
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

        btnExitAddDebt.setOnClickListener {
            super.getActivity()!!.onBackPressed()
        }

        debtProfilePic.setOnClickListener {
            activity?.selectImage(SELECT_DEBT_PROFILE_PIC)
        }

        btnPreviewDebt.setOnClickListener {
            previewDebt()
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

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        if (/*requestCode == SELECT_DEBT_PROFILE_PIC &&*/ /*resultCode == Activity.RESULT_OK && */ data != null && data.data != null) {
            debtDpBitMap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, data.data)
            debtProfilePic.setImageBitmap(debtDpBitMap)
        }
    }

    override fun onStart() {
        super.onStart()
        if (::debtDpBitMap.isInitialized) {
            debtProfilePic.setImageBitmap(debtDpBitMap)
        }
    }
}