/*
 * Copyright (c)  Updated by eric on  9/2/20 8:38 PM
 */

package com.ericg.debtsmanager.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.ericg.debtsmanager.AUTO_SIGN_IN
import com.ericg.debtsmanager.EditUserAccount
import com.ericg.debtsmanager.FROM_ACTIVITY
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.extensions.snackBuilder
import com.ericg.debtsmanager.extensions.toast
import com.ericg.debtsmanager.utils.FirebaseUtils.mAuth
import com.ericg.debtsmanager.utils.FirebaseUtils.mUser
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_reset_password_guide.view.*
import kotlinx.android.synthetic.main.new_reset_password.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NewResetPassword : Activity() {

    private var userAccountEmail: String = ""
    private lateinit var fromActivity: String
    private var showingLoadingView: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_reset_password)

        fromActivity =
            getSharedPreferences(FROM_ACTIVITY, 0).getString(FROM_ACTIVITY, "null") as String

        viewsState(enabled = true)
        listenToClicks()
    }

    private fun listenToClicks() {
        btnBack.setOnClickListener {
            super.onBackPressed()
        }

        btnGetResetPasswordLink.setOnClickListener {
            sendResetPasswordLink()
        }

        btnResetGuide.setOnClickListener{
            showResetGuide()
        }
    }

    @SuppressLint("InflateParams")
    private fun showResetGuide() {
        val guideDialog = BottomSheetDialog(this)
        val guideLayout = layoutInflater.inflate(R.layout.dialog_reset_password_guide, null)
        guideLayout.rdCancel.setOnClickListener {
            guideDialog.dismiss()
        }
        guideDialog.apply {
            setContentView(guideLayout)
            setCancelable(false)
            show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun sendResetPasswordLink() {
        userAccountEmail = userResetPasswordEmail.text.toString().trim()

        if (userAccountEmail.isNotEmpty() && mUser != null && mUser?.email == userAccountEmail) {
            showingLoadingView = true
            loadingView()
            viewsState(false)


            GlobalScope.launch(Dispatchers.IO) {
                val sendingJob = Job(this.coroutineContext[Job])

                mAuth?.sendPasswordResetEmail(userAccountEmail)
                    ?.addOnCompleteListener { sending ->
                        if (sending.isSuccessful) {
                            getLinkProgress.progress = 100
                            getSharedPreferences(AUTO_SIGN_IN, 0).edit()
                                .putBoolean(AUTO_SIGN_IN, false).apply()
                            startActivity(Intent(this@NewResetPassword, SignInActivity::class.java))
                            finish()
                            sendingJob.complete()

                        } else if (!sending.isSuccessful || sending.isCanceled) {
                            showingLoadingView = false
                            loadingView()
                            viewsState(true)

                            // get a View for the snackBar.
                            btnGetResetPasswordLink.snackBuilder(" server error !", 2000).apply {
                                setBackgroundTint(getColor(R.color.colorRed))
                                setTextColor(getColor(R.color.colorWhite))
                                setActionTextColor(getColor(R.color.colorBlack))
                                setAction("Try again") {
                                    sendResetPasswordLink()
                                }
                                show()
                            }
                            sendingJob.cancel()
                        }
                    }
            }
        } else if (userAccountEmail.isEmpty()) userResetPasswordEmail.error = " ${userResetPasswordEmail.hint} is required !"
        else if (mUser == null) toast("Email not found")
        else toast("$userAccountEmail is not verified !")
    }

    private fun viewsState(enabled: Boolean) {
        val views = arrayListOf(btnBack, userResetPasswordEmail, btnGetResetPasswordLink)
        views.forEach { view ->
            view.isEnabled = enabled
            view.isClickable = enabled
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadingView() {
        if (showingLoadingView) {
            btnGetResetPasswordLink.text = "Getting link ..."
            getLinkLoadingView.apply {
                visibility = View.VISIBLE
                setViewColor(getColor(R.color.colorOrange))
                startAnim()
            }
        } else {
            btnGetResetPasswordLink.text = "Get link"
            getLinkLoadingView.apply {
                stopAnim()
                visibility = View.INVISIBLE
            }
        }
    }

    override fun onBackPressed() {
        if (!showingLoadingView) {
            if (!::fromActivity.isInitialized) fromActivity =
                getSharedPreferences(FROM_ACTIVITY, 0).getString(FROM_ACTIVITY, "null") as String

            val backTo: Class<*> =
                if (fromActivity == "signIn") SignInActivity::class.java else EditUserAccount::class.java

            val intentChangePassword = Intent(applicationContext, backTo)
            if (intentChangePassword.resolveActivity(packageManager) != null) {
                startActivity(intentChangePassword)
                finish()
            }
        } else {
            toast("working on requested link ...")
        }
    }
}