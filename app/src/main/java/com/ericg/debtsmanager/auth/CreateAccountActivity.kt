/*
 * Copyright (c)  Updated by eric on  9/25/20 12:48 PM
 */

package com.ericg.debtsmanager.auth

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.ericg.debtsmanager.*
import com.ericg.debtsmanager.contacts.contacts
import com.ericg.debtsmanager.extensions.toast
import com.ericg.debtsmanager.network.sendEmail
import com.ericg.debtsmanager.utils.FirebaseUtils.mAuth
import com.ericg.debtsmanager.utils.FirebaseUtils.mUser
import com.ericg.debtsmanager.utils.FirebaseUtils.userDataBase
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.dialog_report_issue.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val CHANNEL_ID = "Account Management "
private var NOTIFICATION_ID = 1

private var logInFailed = 0

@SuppressLint("InlinedApi")
val permissions = arrayOf(
    android.Manifest.permission.SEND_SMS,
    android.Manifest.permission.READ_CALENDAR,
    android.Manifest.permission.WRITE_CALENDAR,
    android.Manifest.permission.ACCESS_FINE_LOCATION,
    android.Manifest.permission.VIBRATE,
    android.Manifest.permission.WAKE_LOCK,
    android.Manifest.permission.SET_ALARM,
    android.Manifest.permission.READ_EXTERNAL_STORAGE,
    android.Manifest.permission.CAMERA,
    android.Manifest.permission.ACCESS_MEDIA_LOCATION,
    android.Manifest.permission.ACCESS_WIFI_STATE,
    android.Manifest.permission.CALL_PHONE
)

@Suppress("DEPRECATION")
class CreateAccountActivity : AppCompatActivity() {

    private var userName = ""
    private var userPhone = ""
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_create_account)

        handleClicks()
        loadingStatus(false, btnEnabled = true)
    }

    override fun onStart() {
        super.onStart()
        requestPermissions()
        createNotificationChannel()
    }


    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this@CreateAccountActivity,
            permissions, 5
        )
    }

    private fun createNotificationChannel() {
        // A registered channel is required for notifications in Oreo (API 27, android 8.0) and above

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val accountChannel = NotificationChannel(
                CHANNEL_ID, "Account management", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Account settings"
            }

            // register this channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(accountChannel)
        }
    }

    private fun notifyAccountManagement(
        nTitle: String,
        nContentText: String,
        nPendingIntent: PendingIntent? = null
    ) {
        val accountManager = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
        accountManager.apply {
            setSmallIcon(R.drawable.ic_dollar)
            title = nTitle
            color = getColor(R.color.colorOrange)
            setContentText(nContentText)
            priority = NotificationCompat.PRIORITY_HIGH
            setContentIntent(nPendingIntent)
            setAutoCancel(true)
        }
        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, accountManager.build())
        }

        NOTIFICATION_ID += 1   // This allows more than one notification without cancelling the previous one(s)
    }

    @SuppressLint("InflateParams")
    private fun handleClicks() {

        aBtnLogIn.setOnClickListener {
            verifyInputs()
            logInFailed += 1

            when (logInFailed) {
                5 -> {
                    notifyAccountManagement(
                        "Having trouble ?",
                        "Try checking connection, filling required fields or reporting the issue",
                        null
                    )
                }

                15 -> {
                    notifyAccountManagement(
                        "Too many trials",
                        "Hey, contact us for help"
                    )
                }
            }
        }

        aBtnContacts.setOnClickListener {
            requestPermissions()
            contacts().show()
        }

        aBtnSignIn.setOnClickListener {
            val signInIntent = Intent(this, SignInActivity::class.java)
            if (signInIntent.resolveActivity(packageManager) != null) {
                startActivity(signInIntent)
                finish()
            } else {
                toast("null intent")
            }
        }

        aBtnIssue.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                val issueDialog = AlertDialog.Builder(this)
                val issueDialogLayout: View =
                    layoutInflater.inflate(R.layout.dialog_report_issue, null)

                issueDialogLayout.rSendIssue.setOnClickListener {

                    val issueDescription = issueDialogLayout.rIssue.text.toString()
                    val mailSubject = "Debts manager create account issue"
                    val mailToAddress: Array<String> = arrayOf("debtsmanagerhelp@gmail.com")

                    if (issueDescription.trim().isNotEmpty()) {
                        sendEmail(mailSubject, mailToAddress, issueDescription)
                    } else {
                        toast("can't send empty description")
                    }
                }
                issueDialog.apply {
                    setView(issueDialogLayout)

                    create().show()
                }
            } else {
                requestPermissions()
            }
        }
    }

    private fun passwordIsStrong(): Boolean {
        var isStrong = false
        val userPassword = aPassword.text.toString().trim()
        val userConfirmPassword = aConfirmPassword.text.toString().trim()
        val passwords = arrayOf(aPassword, aConfirmPassword)

        if (userPassword == userConfirmPassword && userConfirmPassword.isNotEmpty()) {
            if (
            // check password's length

                (userPassword.trim().length >= 8)
                &&

                // Check if password contains special character(s)

                ((userPassword.contains("@", true)) ||
                        (userPassword.contains("_", true)) ||
                        (userPassword.contains("#", true)) ||
                        (userPassword.contains("$", true)) ||
                        (userPassword.contains("%", true)) ||
                        (userPassword.contains("&", true)) ||
                        (userPassword.contains("*", true)) ||
                        (userPassword.contains("!", true)) ||
                        (userPassword.contains("?", true)))
                &&

                //check if password contains at least one Int

                ((userPassword.contains("0", true)) ||
                        (userPassword.contains("1", true)) ||
                        (userPassword.contains("2", true)) ||
                        (userPassword.contains("3", true)) ||
                        (userPassword.contains("4", true)) ||
                        (userPassword.contains("5", true)) ||
                        (userPassword.contains("6", true)) ||
                        (userPassword.contains("7", true)) ||
                        (userPassword.contains("8", true)) ||
                        (userPassword.contains("9", true)))

            ) {
                isStrong = true
            }

        } else if (userPassword != userConfirmPassword) {
            aPassword.error = "not matching"
            aConfirmPassword.error = "not matching"
        } else {
            passwords.forEach { password ->
                if (password.text.toString().trim().isEmpty()) {
                    password.error = "${password.hint} is required"
                }
            }
        }
        return isStrong
    }

    private fun checkTheEmpty(items: Array<EditText>) {
        items.forEach { item ->
            if (item.text.toString().trim().isEmpty()) {
                item.error = "${item.hint} is required"
            }
        }
    }

    @Suppress("LocalVariableName")
    private fun setSharedPrefs() {
        val PRIVATE_MODE = 0

        getSharedPreferences(HAS_ACCOUNT, PRIVATE_MODE).edit()
            .putBoolean(HAS_ACCOUNT, true).apply()
        //getSharedPreferences(USER_NAME, PRIVATE_MODE).edit().putString(USER_NAME, userName).apply()

        getSharedPreferences(USER_NAME, PRIVATE_MODE).edit()
            .putString(USER_NAME, mUser!!.displayName).apply()

        getSharedPreferences(USER_PHONE, PRIVATE_MODE).edit()
            .putString(USER_PHONE, userPhone).apply()
    }

    private fun verifyInputs() {

        userName = aUserName.text.toString().trim()
        userPhone = aPhone.text.toString().trim()
        val userEmail = aEmail.text.toString().trim()
        val userPhone = aPhone.text.toString().trim()
        val userPassword = aPassword.text.toString().trim()
        val userConfirmPassword = aConfirmPassword.text.toString().trim()

        val notEmpty: Boolean =
            (userName.isNotEmpty() && userPhone.isNotEmpty() && userEmail.isNotEmpty() && userPhone.isNotEmpty()
                    && userPassword.isNotEmpty() && userConfirmPassword.isNotEmpty())

        //val inputs = arrayOf(userName, userEmail, userPhone, userPassword, userConfirmPassword)
        val inputsIds: Array<EditText> =
            arrayOf(aUserName, aEmail, aPhone, aPassword, aConfirmPassword)

        fun saveUserData() {
            if (notEmpty) {

                val userUID = mAuth!!.currentUser!!.uid
                val userDetails = hashMapOf(
                    "name" to userName,
                    "email" to userEmail,
                    "phone" to userPhone,
                    "password" to userPassword
                )

                GlobalScope.launch(Dispatchers.IO) {
                    withContext(this.coroutineContext) {
                        userDataBase
                            ?.collection("users")
                            ?.document(userUID)
                            ?.collection("userCredentials")
                            ?.document("credentials")
                            ?.set(userDetails)
                    }
                }
            }
        }

        val onTap = Intent(this, ParentActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, onTap, 0
        )

        if (notEmpty && passwordIsStrong()) {
            loadingStatus(true, btnEnabled = false)
            mAuth!!
                .createUserWithEmailAndPassword(
                    userEmail,
                    userPassword
                )
                .addOnCompleteListener { creating ->
                    if (creating.isSuccessful) {
                        loadingStatus(false, btnEnabled = false)

                        GlobalScope.launch(Dispatchers.IO) {
                            sendVerificationEmail()
                            saveUserData()
                            setSharedPrefs()
                        }

                        startActivity(
                            Intent(
                                this@CreateAccountActivity,
                                ParentActivity::class.java
                            )
                        )

                        toast("Welcome to Debts manager")
                        notifyAccountManagement(
                            "Congratulations $userName !",
                            "To manage your account, click profile >> " +
                                    "settings then perform any changes you wish",
                            pendingIntent
                        )
                        finish()
                    } else if (creating.isCanceled || !creating.isSuccessful) {
                        toast("Oops! an error occurred")
                        loadingStatus(false, btnEnabled = true)
                    }
                }

        } else if (!notEmpty) {
            checkTheEmpty(inputsIds)
        } else if (!passwordIsStrong() && userPassword == userConfirmPassword) {
            val warning = AlertDialog.Builder(this, 0)
            warning.apply {
                setTitle(getString(R.string.warning))
                setIcon(
                    ContextCompat.getDrawable(
                        this@CreateAccountActivity,
                        R.drawable.ic_warning
                    )
                )
                setMessage("Use a more secure password")
                setPositiveButton("Got it") { _, _ -> }
                setCancelable(false)

                create().show()
            }
        }
    }

    private fun loadingStatus(showLoading: Boolean, btnEnabled: Boolean) {

        if (showLoading) {
            aLoadingView.apply {
                setViewColor(getColor(R.color.colorGreen))
                setRoundColor(getColor(R.color.colorBlack))
                startAnim()
                visibility = View.VISIBLE
            }
            aLoading.visibility = View.VISIBLE
        } else {
            aLoadingView.apply {
                stopAnim()
                visibility = View.INVISIBLE
            }
            aLoading.visibility = View.INVISIBLE

        }

        if (btnEnabled) {
            buttonsStatus(true)
        } else {
            buttonsStatus(false)
        }
    }

    private fun buttonsStatus(enabled: Boolean) {
        val buttons = arrayOf(aBtnLogIn, aBtnContacts, aBtnSignIn, aBtnIssue)
        for (button in buttons) {
            button.isEnabled = enabled
        }
    }

    private fun sendVerificationEmail() {
        // update user
        if (mUser != null) {
            mUser!!
                .sendEmailVerification()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        toast(
                            "Link sent to ${mUser!!.email}. Please verify your email",
                            LENGTH_LONG
                        )
                    } else {
                        toast("Sending failed!")
                    }
                }
        } else {
            toast("no user to send email to")
        }
    }

    // disable back press
    override fun onBackPressed() = toast("can't go back at this stage")
}