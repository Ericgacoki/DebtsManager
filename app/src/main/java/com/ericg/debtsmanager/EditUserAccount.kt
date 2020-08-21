/*
 * Copyright (c)  Updated by eric on  8/2/20 6:36 PM
 */

package com.ericg.debtsmanager

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.ericg.debtsmanager.auth.ResetPassword
import com.ericg.debtsmanager.communication.contacts
import com.ericg.debtsmanager.extensions.selectImage
import com.ericg.debtsmanager.extensions.snackBuilder
import com.ericg.debtsmanager.utils.FirebaseUtils.mUser
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_edit_account.*
import kotlinx.coroutines.runBlocking


@Suppress("PrivatePropertyName")
class EditUserAccount : AppCompatActivity() {

    private lateinit var selectedImageBitmap: Bitmap

    private var mUserName: String? = null
    private var mUserEmail: String? = null
    private var mUserPhone: String? = "+254716965216"

    private var displayName: String = ""
    private var editMode = false


    private fun editMode() {
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

    private fun fetchData() {

        mUserName = getSharedPreferences("userName", 0).getString("userName", "")
        displayName = if (mUser != null) {
            mUserEmail = mUser!!.email
            mUser!!.displayName.toString()
        } else {
            mUserName.toString()
        }
        // todo fetch image from cloud/cache
    }

    private fun updateFields(updateImage: Boolean) {
        if (updateImage) {
            // TODO delay 1 sec fetch image from cloud/cache, update dp if user exists otherwise leave the default
        }
        if (displayName.isNotEmpty()) {
            etNewUserName.setText(displayName)
        } else {
            etNewUserName.setText(mUserName)
        }

        if (!mUserEmail.isNullOrEmpty()) {
            etNewEmail.setText(mUserEmail)
        }

        etNewPhone.setText(mUserPhone)
    }

    private fun handleClicks() {

        change_dp.setOnClickListener {
            val editable: Array<EditText> = arrayOf(etNewPhone, etNewUserName, etNewEmail)

            if (editMode) { selectImage(RC_SELECT_MAIN_IMAGE)
            } else { change_dp.snackBuilder("First enable edit mode", 4000).apply {

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

        btnHelpToEditAccount.setOnClickListener {
            val contacts: BottomSheetDialog by lazy { contacts() }
            contacts.dismissWithAnimation = true
            contacts.show()
        }
        btnChangePassword.setOnClickListener {
            // todo create a change password activity or dialog, -> clear pref for auto sign in
            val intentChangePassword = Intent(applicationContext, ResetPassword::class.java)
            if (intentChangePassword.resolveActivity(packageManager) != null) {
                startActivity(intentChangePassword)
            }
        }
        btnSaveChanges.setOnClickListener {
            if (editMode) {
                //TODO  save changes
            } else {
                btnSaveChanges.snackBuilder("First enable edit mode", 5000).apply {
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

            selectedImageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImagePath)

            // TODO save this image to firestore -> check if user is null although at this point user can't be null
            // set saveIsComplete to true on a successful saving

        }
        super.onActivityResult(requestCode, resultCode, imageIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)

        editMode()
        runBlocking {
            fetchData()
        }
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
