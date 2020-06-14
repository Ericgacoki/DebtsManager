/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */


package com.ericg.debtsmanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.adapters.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*

class Profile : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataAnalysisRecyclerView.apply {
            adapter = AnalysisAdapter()
        }

        updateProfile()
    }

    @Suppress("LocalVariableName")
    private fun updateProfile() {

        val mUserEmail = FirebaseAuth.getInstance().currentUser!!.email
        profileUserEmail.text = mUserEmail

        val USER_NAME = "userName"
        val userName = this.activity!!.getSharedPreferences(USER_NAME, 0).getString(USER_NAME, "")
        profileUserName.text = userName

        pDebtors.text = DebtorsAdapter().itemCount.toString()
        pMyDebts.text = MyDebtsAdapter().itemCount.toString()
        pLoans.text   = LoansAdapter().itemCount.toString()
        pInstallments.text = InstallmentsAdapter().itemCount.toString()

    }
}