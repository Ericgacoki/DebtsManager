/*
 * Copyright (c)  Updated by eric on  8/31/20 11:17 AM
 */

package com.ericg.debtsmanager.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ericg.debtsmanager.ParentActivity
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.utils.FirebaseUtils.mAuth
import com.ericg.debtsmanager.utils.FirebaseUtils.mUser
import com.ericg.debtsmanager.communication.contacts
import com.ericg.debtsmanager.extensions.toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.canvas.CanvasCompat
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.dialog_reset_password.view.*

class ResetPassword : AppCompatActivity() {

    val thisContext = this

    private lateinit var getLink: Array<TextView>
    private lateinit var verify: Array<TextView>
    private lateinit var resetEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        getLink = arrayOf(rEmail, rBtnGetLink)
        verify = arrayOf(rNewPassword, rBtnVerify)

        loadingStatus(0) // no loading view is visible

        resetStep(1)
        handleClicks()
        showResetGuide()
    }

    override fun onStart() {
        super.onStart()
       loadingStatus(0)
    }

    @Suppress("DEPRECATION")
    // Extension function for Boolean class
    private fun Boolean.toggle(image: Int? = R.drawable.sad_face) {
        getLink.forEach { item ->
            item.visibility = View.INVISIBLE
            getLink[0].hint = ""
            // set hint to an empty string 'cause it does't respond to visibility
        }
        getLinkStateImage.apply {
            visibility = View.VISIBLE
            setImageDrawable(ContextCompat.getDrawable(thisContext, image!!))
        }
        Handler().postDelayed({
            kotlin.run {
                if (this) {
                    getLink.forEach { item ->
                        item.visibility = View.VISIBLE
                        getLink[0].hint = "Account Email"
                    }
                    getLinkStateImage.visibility = View.INVISIBLE
                } else {
                    if (image != null) {
                        getLinkStateImage.visibility = View.VISIBLE // this image does not disappear
                    }
                }
            }
        }, 2000)
    }

    @SuppressLint("InflateParams")
    private fun handleClicks() {
        rBtnBack.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        rBtnGetLink.setOnClickListener {
            getLinkEmail()
        }

        rBtnVerify.setOnClickListener {
            signInUser()
        }
        rInfo.setOnClickListener {
            showResetGuide()
        }
        rContacts.setOnClickListener {
            contacts().show()
        }
    }

    @SuppressLint("InflateParams")
    private fun showResetGuide() {
        val guideLayout = layoutInflater.inflate(R.layout.dialog_reset_password, null)
        val guideDialog = BottomSheetDialog(this)
        guideLayout.rdCancel.setOnClickListener {
            guideDialog.dismiss()
        }
        guideDialog.apply {
            setContentView(guideLayout)
            setCancelable(false)
            show()
        }
    }

    private fun getLinkEmail() {
        resetEmail = rEmail.text.toString().trim()

        if (resetEmail.isNotEmpty()) {
            when {
                mUser != null && (mUser!!.email == resetEmail) -> {
                    loadingStatus(1)
                    mAuth!!
                        .sendPasswordResetEmail(resetEmail)
                        .addOnCompleteListener { task ->
                            when {
                                task.isSuccessful -> {
                                    loadingStatus(0)
                                    resetStep(2)
                                    toast("Successfully sent link to $resetEmail")
                                    rBtnBack.isEnabled = false
                                }
                                task.isCanceled || !task.isSuccessful -> {

                                    loadingStatus(0)
                                    resetStep(1)
                                    toast("Failed")
                                    rBtnBack.isEnabled = true
                                }
                            }
                        }
                }

                mUser == null -> {
                    // todo warm user and offer a create new account 'cause it seem like they dont have one
                    toast("Error: no user found with that email")
                    true.toggle()
                }
                else -> {
                    // todo show bottom sheet dialog -> explain the error, create account btn
                    toast("Error: failed to fetch data")
                }
            }
        } else {
            true.toggle()
            rEmail.error = "Email is required!"
        }
    }

    private fun signInUser() {
        // mUser!!.reload()
        val newPassword = rNewPassword.text.toString().trim()
        if (newPassword.isNotEmpty() && mUser != null) {
            // todo update password in the database
            loadingStatus(2)
            try {
                mUser!!.reload()
                mUser!!
                    .updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            loadingStatus(0)
                            startActivity(Intent(this@ResetPassword, ParentActivity::class.java))
                            finish()
                        } else {

                            loadingStatus(0)
                            toast("failed")
                        }
                    }
            } catch (e: Exception) {
                toast(e.message.toString())
            }
        } else if (newPassword.isEmpty()) {
            rNewPassword.error = "NewPassword is required"
        }
    }

    private fun resetStep(
        step: Int,
        getLink: Array<TextView> = this.getLink,
        verify: Array<TextView> = this.verify
    ) {

        when (step) {
            1 -> {
                for (item in getLink) {
                    item.visibility = View.VISIBLE
                    getLink[0].hint = "Account Email"
                }
                rBtnGetLink.isEnabled = true

                for (item in verify) {
                    item.visibility = View.INVISIBLE
                    verify[0].hint = ""
                }
                rBtnVerify.isEnabled = false
            }

            2 -> {
                for (item in getLink) {
                    item.visibility = View.INVISIBLE
                    getLink[0].hint = ""
                }
                rBtnGetLink.isEnabled = false

                for (item in verify) {
                    item.visibility = View.VISIBLE
                    verify[0].hint = "New password"
                }
                rBtnVerify.isEnabled = true
                false.toggle(R.drawable.done)
            }
        }

    }

    private fun loadingStatus(show: Int) {
        when (show) {

            0 -> {
                eLoadingView.visibility = View.INVISIBLE
                cLoadingView.visibility = View.INVISIBLE
                //stop anim just in case the loading view is faded
                eLoadingView.stopAnim()
                cLoadingView.stopAnim()
            }

            1 -> {
                eLoadingView.apply {
                    visibility = View.VISIBLE
                    setViewColor(getColor(R.color.colorOrange))
                    setRoundColor(getColor(R.color.colorGreen))
                    startAnim()
                }

                cLoadingView.apply {
                    visibility = View.INVISIBLE
                    setRoundColor(getColor(R.color.colorOrange))
                    setViewColor(getColor(R.color.colorGreen))
                    stopAnim()
                }
            }

            2 -> {
                cLoadingView.apply {
                    visibility = View.VISIBLE
                    setRoundColor(getColor(R.color.colorOrange))
                    setViewColor(getColor(R.color.colorGreen))
                    startAnim()
                }

                eLoadingView.apply {
                    visibility = View.INVISIBLE
                    setViewColor(getColor(R.color.colorOrange))
                    setRoundColor(getColor(R.color.colorGreen))
                    stopAnim()
                }

            }
        }
    }
    // boiler plate code
    private val enableBack = false
    @Suppress("ConstantConditionIf")
    override fun onBackPressed() {
        // do nothing
        if (enableBack) {
            super.onBackPressed()
        }
    }
}