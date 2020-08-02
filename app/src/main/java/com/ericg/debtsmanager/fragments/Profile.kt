/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */


package com.ericg.debtsmanager.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.adapters.*
import com.ericg.debtsmanager.firebaseAuth.CreateAccountActivity
import com.ericg.debtsmanager.firebaseAuth.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_profile.*
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.dialog_delete_or_log_out.view.*

private var mAuth: FirebaseAuth? = null
private var mUser: FirebaseUser? = null

class Profile : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onStart() {
        super.onStart()

        // initialize firebase variables

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth!!.currentUser
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataAnalysisRecyclerView.apply {
            adapter = AnalysisAdapter()
        }

        updateProfile()
        handleClicks()
    }

    private fun toast(msg: String) {
        Toast.makeText(this.activity, msg, LENGTH_SHORT).show()
    }

    @Suppress("LocalVariableName")
    private fun updateProfile() {

        val mUserEmail = FirebaseAuth.getInstance().currentUser!!.email
        profileUserEmail.text = mUserEmail

        val USER_NAME = "userName"
        val userName = this.activity!!.getSharedPreferences(USER_NAME ,0).getString(USER_NAME, "")
        profileUserName.text = userName

        pDebtors.text = DebtorsAdapter().itemCount.toString()
        pMyDebts.text = MyDebtsAdapter().itemCount.toString()
        pLoans.text = LoansAdapter().itemCount.toString()
        pInstallments.text = InstallmentsAdapter().itemCount.toString()

    }

    @Suppress("DEPRECATION")
    private fun handleClicks() {

        deleteAccount.setOnClickListener {
            deleteOrLogOut()
        }

        editAccount.setOnClickListener {
            // todo edit account
            editUserAccount()
        }

        var editDp = false
        profileDp.setOnClickListener {
            if (!editDp) {
                toast("double tap to edit")
            }
            editDp = true

            /** capture a double tap*/

            Handler().postDelayed({
                editDp = false
            }, 775)

            if (editDp) {
                editProfileDp()
            }
        }

        profileCardLayout.setOnClickListener {
            toast("${profileUserName.text}'s profile")

        }

    }

    private fun editUserAccount(){

        val confirmEditUserAccount = AlertDialog.Builder(this.context, 3)
        confirmEditUserAccount.apply {
            setIcon(R.drawable.ic_edit)
            setTitle("Sure to Edit account ?")
            setMessage("You can edit personal data. Do you wish to proceed ?")
            setPositiveButton("Yes"){_,_->

/*
                create an activity instead of a fragment or find a way to hide the bottomNav when editAccount fragment is displayed
*/
                parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.frameLayout, EditUserAccount())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }

            setNegativeButton("No"){_,_ ->
                // exit dialog
            }
            create().show()
        }

    }

    private fun editProfileDp() {
        // todo select to change dp
    }

    @Suppress("SpellCheckingInspection")
    @SuppressLint("InflateParams")
    private fun deleteOrLogOut() {

        val delog = AlertDialog.Builder(this.context)
        val delogView: View = layoutInflater.inflate(R.layout.dialog_delete_or_log_out, null)

        delogView.btnDelete.setOnClickListener {
            deleteAccount()
        }

        delogView.btnLogOut.setOnClickListener {
            logOut()
        }

        delog.apply {
            setView(delogView)
            create().show()
        }
    }

    private fun deleteAccount() {

        val confirmDelete = AlertDialog.Builder(this.context, 2)
        confirmDelete.apply {
            setIcon(R.drawable.ic_warning)
            setTitle("Delete account ?")
            setMessage("This action is potentially dangerous. Loss of your data is irreversible!")
            setPositiveButton("proceed") { _, _ ->

                if (mUser != null) {
                    mUser!!.delete()
                        .addOnCompleteListener { task ->
                            // todo show loading status
                            if (task.isSuccessful) {
                                // todo hide loading status
                                clearSharedPrefs("all")
                                startActivity(
                                    Intent(
                                        this.context,
                                        CreateAccountActivity::class.java
                                    )
                                )
                                toast("deleted successfully")
                            } else if (task.isCanceled || !task.isSuccessful) {
                                // todo hide loading status
                                toast("failed to delete")
                            }
                        }
                } else {
                    toast("null user!")
                }
            }

            setNegativeButton("cancel") { _, _ ->
                // close dialog
            }
            setCancelable(false)
            create().show()
        }

    }

    private fun logOut() {
        val confirmLogOut = AlertDialog.Builder(this.context, 3)
        confirmLogOut.apply {
            setTitle("Sure to log out ?")
            setIcon(R.drawable.ic_warning)
            setMessage("You can still sign in again.")
            setPositiveButton("proceed") { _, _ ->

                clearSharedPrefs("autoSignIn")
                startActivity(Intent(this.context, SignInActivity::class.java))
                toast("please sign in")
            }
            setNegativeButton("cancel") { _, _ ->
                // close dialog
            }
            setCancelable(false)
            create().show()
        }

    }

    @Suppress("LocalVariableName")
    private fun clearSharedPrefs(whichOne: String) {
        val AUTO_SIGN_IN = "autoSignIn"
        val HAS_ACCOUNT = "hasAccount"

        val autoSign: SharedPreferences = activity!!.getSharedPreferences(AUTO_SIGN_IN, 0)
        val autoSignEditor = autoSign.edit()

        val hasAccount: SharedPreferences = activity!!.getSharedPreferences(HAS_ACCOUNT, 0)
        val hasAccountEditor = hasAccount.edit()

        if (whichOne == "all") {
            autoSignEditor.clear().apply()
            hasAccountEditor.clear().apply()
        } else if (whichOne == "autoSignIn") {
            autoSignEditor.clear().apply()
        }
    }
}
