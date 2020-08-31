/*
 * Copyright (c)  Updated by eric on  8/31/20 11:17 AM
 */

package com.ericg.debtsmanager.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ericg.debtsmanager.EditUserAccount
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.extensions.snackBuilder
import com.ericg.debtsmanager.extensions.toast
import com.ericg.debtsmanager.utils.FirebaseUtils.mAuth
import com.ericg.debtsmanager.utils.FirebaseUtils.mUser
import kotlinx.android.synthetic.main.new_reset_password.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * @author eric
 * @date 8/30/20
 */

class NewResetPassword : AppCompatActivity() {

    private var userAccountEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_reset_password)

        viewsState(enabled = true)
        listenToClicks()
    }

    private fun listenToClicks() {
        btnBack.setOnClickListener {
            // todo read the previous activity from shared prefs and decide where to navigate

            // testing
            val intentChangePassword = Intent(applicationContext, EditUserAccount::class.java)
            if (intentChangePassword.resolveActivity(packageManager) != null) {
                startActivity(intentChangePassword)
            }
        }

        btnGetResetPasswordLink.setOnClickListener {
            sendResetPasswordLink()
        }
    }

    private fun sendResetPasswordLink() {
        userAccountEmail = userResetPasswordEmail.text.toString()

        if (userAccountEmail.isNotEmpty() && mUser != null && mUser?.email == userAccountEmail) {
            showProgress()
            viewsState(enabled = false)

            GlobalScope.launch(Dispatchers.IO) {
                val sendingJob = Job(this.coroutineContext[Job])

                mAuth?.sendPasswordResetEmail(userAccountEmail)
                    ?.addOnCompleteListener { sending ->
                        if (sending.isSuccessful) {
                            // get a View for the snackBar.
                            btnGetResetPasswordLink.snackBuilder(
                                "link sent to $userAccountEmail",
                                2000
                            )
                                .apply {
                                    setBackgroundTint(getColor(R.color.colorGreen))
                                    setTextColor(getColor(R.color.colorWhite))
                                    show()
                                }
                            getLinkProgress.progress = 100
                            startActivity(Intent(this@NewResetPassword, SignInActivity::class.java))
                            finish()
                            sendingJob.complete()

                        } else if (!sending.isSuccessful || sending.isCanceled) {
                            viewsState(true)
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
        } else if (userAccountEmail.isEmpty()) userResetPasswordEmail.error =
            " ${userResetPasswordEmail.hint} is required !"
        else if (mUser == null) toast("user not found")
        else toast("$userAccountEmail is not verified !")
    }

    private fun viewsState(enabled: Boolean) {
        val views = arrayListOf(userResetPasswordEmail, btnGetResetPasswordLink)
        views.forEach { view ->
            view.isEnabled = enabled
            view.isClickable = enabled
        }
    }

    private fun showProgress() {

    }

    override fun onBackPressed() = toast("can't go back at this stage")
}