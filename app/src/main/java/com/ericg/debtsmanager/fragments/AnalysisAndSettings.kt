package com.ericg.debtsmanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.adapters.AnalysisAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_analysis_and_settings.*

class AnalysisAndSettings : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_analysis_and_settings, container, false)

    @Suppress("LocalVariableName")
    override fun onStart() {
        super.onStart()

        dataAnalysisRecyclerView.apply {
            adapter = AnalysisAdapter()
        }

        val mUserEmail = FirebaseAuth.getInstance().currentUser!!.email
        profileUserEmail.text = mUserEmail

        val USER_NAME = "userName"
        val userName = this.activity!!.getSharedPreferences(USER_NAME, 0).getString(USER_NAME, "")
        profileUserName.text = userName

    }
}