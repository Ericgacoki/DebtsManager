/*
 * Copyright (c)  Updated by eric on  10/1/20 11:52 PM
 */

package com.ericg.debtsmanager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ericg.debtsmanager.auth.NewResetPassword
import com.ericg.debtsmanager.contacts.contacts
import com.ericg.debtsmanager.extensions.selectImage
import com.ericg.debtsmanager.extensions.snackBuilder
import com.ericg.debtsmanager.extensions.toast
import com.ericg.debtsmanager.utils.FirebaseUtils
import com.ericg.debtsmanager.utils.FirebaseUtils.mUser
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_edit_account.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@Suppress("PrivatePropertyName")
class EditUserAccount : AppCompatActivity() {

    private lateinit var selectedImageBitmap: Bitmap

    private var mUserName: String = ""
    private var mUserEmail: String = ""
    private var mUserPhone: String = ""

    private var editMode = false

    private fun notEditMode() {
        fetchData()
        val editable: Array<EditText> = arrayOf(etNewPhone, etNewUserName, etNewEmail)

        editable.forEach { it.isEnabled = false }
        switchEditMode.setOnCheckedChangeListener { _, on ->
            if (on) {
                editMode = true
                editable.forEach { it.isEnabled = true }
            } else {
                editMode = false
                // reset to default
                updateFields(false)
                editable.forEach { it.isEnabled = false }
            }
        }
    }

    private fun loading(show: Boolean) {
        if (show) {
            layLoadSavingChanges.visibility = VISIBLE
            loadSavingChanges.apply {
                setViewColor(ContextCompat.getColor(this@EditUserAccount, R.color.colorOrange))
                startAnim()
            }
        } else layLoadSavingChanges.visibility = INVISIBLE
    }

    private fun fetchData() {
        loading(true)
        val fetch: Job = GlobalScope.launch(Dispatchers.IO) {

            mUserEmail = mUser?.email.toString()

            val credentialsSnapshot = FirebaseUtils.userDataBase
                ?.collection("users")
                ?.document(FirebaseUtils.mAuth!!.currentUser!!.uid)
                ?.collection("userCredentials")
                ?.document("credentials")?.get()

            credentialsSnapshot?.addOnSuccessListener { data ->
                loading(false)

                mUserName = data?.get("name") as String
                mUserPhone = data.get("phone") as String
                //  mUserEmail = data.get("email") as String
            }
            credentialsSnapshot?.addOnFailureListener {
                loading(false)
            }
        }
    }

    private fun updateFields(updateImage: Boolean) {
        if (updateImage) {
            // TODO fetch image from cloud/cache, update dp if user exists otherwise leave the default
        }

        /**
         *  use .setText("...") since .text = "..." doesn't work in EditTexts
         */

        if (mUserName.isNotEmpty()) {
            etNewUserName.setText(mUserName)
        }

        if (mUserPhone.isNotEmpty()) {
            etNewPhone.setText(mUserPhone)
        }

        if (mUserEmail.isNotEmpty()) {
            etNewEmail.setText(mUserEmail)
        }
    }

    private fun handleClicks() {

        layLoadSavingChanges.setOnClickListener { /* 'lock' views below */ }

        change_dp.setOnClickListener {
            val editable: Array<EditText> = arrayOf(etNewPhone, etNewUserName, etNewEmail)

            if (editMode) {
                selectImage(RC_SELECT_MAIN_IMAGE)
            } else {
                change_dp.snackBuilder("First enable edit mode", 4000).apply {

                    setBackgroundTint(getColor(R.color.colorRed))
                    setTextColor(getColor(R.color.colorWhite))
                    setActionTextColor(getColor(R.color.colorBlack))
                    setAction("OK") {
                        editable.forEach { it.isEnabled = true }
                        switchEditMode.isChecked = true
                        editMode = true
                    }
                    show()
                }
            }
        }

        btnHelpToEditAccount.setOnClickListener {
            val contacts: BottomSheetDialog by lazy { contacts() }
            contacts.dismissWithAnimation = true
            contacts.show()
        }
        btnChangePassword.setOnClickListener {
            getSharedPreferences(FROM_ACTIVITY, Context.MODE_PRIVATE).edit()
                .putString(FROM_ACTIVITY, "editUserAccount").apply()

            val intentChangePassword = Intent(applicationContext, NewResetPassword::class.java)
            intentChangePassword.resolveActivity(packageManager)?.let {
                startActivity(intentChangePassword)
            }
        }
        btnSaveChanges.setOnClickListener {
            if (editMode) {
                val ets = arrayOf(etNewUserName, etNewPhone, etNewEmail)
                val nonIsEmpty = etNewUserName.text.toString().trim().isNotEmpty() &&
                        etNewPhone.text.toString().trim().isNotEmpty() &&
                        etNewEmail.text.toString().trim().isNotEmpty()

                if (nonIsEmpty /*&& edited*/) {

                    FirebaseUtils.userDataBase
                        ?.document("users/${FirebaseUtils.mAuth!!.currentUser!!.uid}/userCredentials/credentials")
                        ?.apply {
                            // set(hashMapOf("name" to mUserName, "phone" to mUserPhone , "email" to mUserEmail))

                            update(
                                hashMapOf<String, Any>(
                                    "name" to mUserName,
                                    "phone" to mUserPhone,
                                    "email" to mUserEmail
                                )
                            ).addOnSuccessListener {
                                toast("updated successfully")
                            }
                        }

                } else {
                    ets.forEach {
                        if (it.text.toString().trim().isEmpty()) {
                            it.error = "${it.hint} can't be empty"
                        }
                    }
                }

            } else {
                btnSaveChanges.snackBuilder("First enable edit mode", 3000).apply {
                    val editable: Array<EditText> = arrayOf(etNewPhone, etNewUserName, etNewEmail)

                    setBackgroundTint(getColor(R.color.colorLightOrange))
                    setTextColor(getColor(R.color.colorBlack))
                    setActionTextColor(getColor(R.color.colorWhite))
                    setAction("OK") {
                        editable.forEach { it.isEnabled = true }
                        switchEditMode.isChecked = true
                        editMode = true
                    }
                    show()
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, imageIntent: Intent?) {

        if (requestCode == RC_SELECT_MAIN_IMAGE && resultCode == Activity.RESULT_OK && imageIntent != null && imageIntent.data != null) {
            val selectedImagePath = imageIntent.data

            selectedImageBitmap =
                MediaStore.Images.Media.getBitmap(contentResolver, selectedImagePath)

            // TODO save this image to firestore -> check if user is null although at this point user can't be null
            // set saveIsComplete to true on a successful saving
        }
        super.onActivityResult(requestCode, resultCode, imageIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)


        notEditMode()
        updateFields(false)

        handleClicks()
    }

    override fun onStart() {
        super.onStart()
        // TODO pass saveIsComplete to the fun below
        updateFields(true)

        if (::selectedImageBitmap.isInitialized) {
            userProfilePicture.setImageBitmap(selectedImageBitmap)
        }
    }

    private var exit = false

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (exit) {
            // super.onBackPressed()
            finish()
        } else {
            toast("press again to exit")
            exit = true
            Handler().postDelayed({ exit = false }, 2000)
        }
    }
}