/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */

package com.ericg.debtsmanager.communication

import android.app.Activity
import com.ericg.debtsmanager.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

fun Activity.contacts(): BottomSheetDialog {
    val contactsBuilder = BottomSheetDialog(this)
    val customView = layoutInflater.inflate(R.layout.dialog_contacts, null)
    contactsBuilder.apply {
        setContentView(customView)



    }
     return contactsBuilder
}