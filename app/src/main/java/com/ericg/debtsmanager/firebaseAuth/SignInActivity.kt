package com.ericg.debtsmanager.firebaseAuth

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.ericg.debtsmanager.ParentActivity
import com.ericg.debtsmanager.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_report_issue.view.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.lang.Exception

private var elapseTime = 0
private var trials = 0

private var mAuth: FirebaseAuth? = null
private var mUser: FirebaseUser? = null
private var fDatabase: FirebaseFirestore? = null

class SignInActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_sign_in)

        hideTryAgain()
        handleClicks()

    }

    override fun onStart() {
        super.onStart()

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth!!.currentUser
        fDatabase = FirebaseFirestore.getInstance()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("InflateParams")
    private fun handleClicks() {
        sBtnSignIn.setOnClickListener {

            trials += 1
            elapseTime = trials * 6000
            when {
                trials % 5 == 0 -> {
                    tryAgain()

                    Handler().postDelayed({
                        hideTryAgain()
                    }, elapseTime.toLong())
                }
                else -> {
                    // testing todo remove the next 2 lines
                    startActivity(Intent(this, ParentActivity::class.java))
                    setSharedPrefs()

                   signInUser()
                }
            }
        }

        sBtnReset.setOnClickListener {
            startActivity(Intent(this, ResetPassword::class.java))
            finish()
        }

        sBtnLogIn.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
            finish()
        }

        sBtnIssue.setOnClickListener {
            val issueDialog = BottomSheetDialog(this)
            val issueDialogLayout: View =
                layoutInflater.inflate(R.layout.dialog_report_issue, null)

            //todo updateLengthCounter()
            issueDialogLayout.rSendIssue.setOnClickListener {

                val issueDescription = issueDialogLayout.rIssue.text.toString()
                val mailSubject = "Debts manager sign in issue"
                val mailToAddress: Array<String> = arrayOf("debtsmanagercare@gmail.com")
                val mailIntent = Intent(Intent.ACTION_SEND, Uri.parse("mailto:"))
                mailIntent.type = "text/plain"

                if (issueDescription.trim().isNotEmpty()) {

                    mailIntent.putExtra(Intent.EXTRA_EMAIL, mailToAddress)
                    mailIntent.putExtra(Intent.EXTRA_SUBJECT, mailSubject)
                    mailIntent.putExtra(Intent.EXTRA_TEXT, issueDescription)

                    try {
                        startActivity(Intent.createChooser(mailIntent,"Choose Email client [courtesy of Debts Manager]"))
                    }
                    catch (e: Exception){
                        toast(e.message.toString())
                    }

                } else {
                    toast("can't send empty description")
                }
            }
            issueDialog.apply {
                setContentView(issueDialogLayout)
                create()
                show()
            }
        }
    }

    private fun uIState(btnsEnabled: Boolean, showLoading: Boolean) {
        val btns = arrayOf(sBtnSignIn, sBtnReset, sBtnLogIn, sBtnIssue)

        if (btnsEnabled) {
            for (btn in btns) {
                btn.isEnabled = true
            }
        } else {
            for (btn in btns) {
                btn.isEnabled = false
            }
        }

        if (showLoading) {
            sLoadingView.apply {
                setRoundColor(getColor(R.color.colorOrange))
                setViewColor(getColor(R.color.colorGreen))
                startAnim()
                visibility = View.VISIBLE
            }
        } else {
            sLoadingView.apply {
                stopAnim()
                visibility = View.INVISIBLE
            }
        }
    }

    @Suppress("LocalVariableName")
    private fun setSharedPrefs(){
        val AUTO_SIGN_IN = "autoSignIn"
        val autoSignIn = autoSignIn.isChecked
        val signInPrefs: SharedPreferences = getSharedPreferences(AUTO_SIGN_IN, 0)
        val signInEditor = signInPrefs.edit()
        signInEditor.putBoolean(AUTO_SIGN_IN, autoSignIn).apply()
    }

    private fun signInUser() {
        val sUserEmail = sEmail.text.toString().trim()
        val sUserPassword = sPassword.text.toString().trim()

        val sInputIds = arrayOf(sEmail, sPassword)

        val notEmpty: Boolean = (sUserEmail.isNotEmpty() && sUserPassword.isNotEmpty())

        if (notEmpty) {
            uIState(btnsEnabled = false, showLoading = true)
            val next = Intent(this@SignInActivity, ParentActivity::class.java)
            mAuth!!
                .signInWithEmailAndPassword(sUserEmail, sUserPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        // todo check auto sign in and save it using shared preferences
                        setSharedPrefs()

                        uIState(btnsEnabled = true, showLoading = false)
                        if (next.resolveActivity(packageManager) != null) {
                            startActivity(next)
                            finish()
                        } else {
                            toast("Error: null activity!")
                        }
                    } else if (task.isCanceled || !task.isSuccessful) {
                        toast("sign in failed")
                        uIState(btnsEnabled = true, showLoading = false)
                    }
                }
        } else {
            for (id in sInputIds) {
                if (id.text.toString().trim().isEmpty()) {
                    id.error = "${id.hint} is required"
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun tryAgain() {
        val tryAgain = arrayOf(tryView, tryAgainIn, sChronometer, sTime)

        for (view in tryAgain) {
            view.visibility = View.VISIBLE
            view.startAnimation(AnimationUtils.loadAnimation(this,
                R.anim.anim_view_from_top
            ))
        }
        sChronometer.apply {
            isCountDown = true
            base = SystemClock.elapsedRealtime() + elapseTime
            start()
        }
        sOr.visibility = View.INVISIBLE
        uIState(btnsEnabled = false, showLoading = false)
    }

    private fun hideTryAgain(){
        val tryAgain = arrayOf(tryView, tryAgainIn, sChronometer, sTime)

        for (view in tryAgain){
            view.visibility = View.INVISIBLE
        }
        sChronometer.apply {
            stop()
        }

        sOr.visibility = View.VISIBLE
        uIState(btnsEnabled = true, showLoading = false)
    }

    private fun toast(msg: String) {
        Toast.makeText(this@SignInActivity, msg, LENGTH_LONG).show()
    }

    /** disable backPress */

    private val canGoBack = false

    override fun onBackPressed() {
        toast("use other buttons")

        if (canGoBack) {
            super.onBackPressed()
        }
    }
}