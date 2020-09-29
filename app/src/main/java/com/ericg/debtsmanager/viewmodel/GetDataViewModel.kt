/*
 * Copyright (c)  Updated by eric on  9/30/20 1:56 AM
 */

package com.ericg.debtsmanager.viewmodel

import androidx.lifecycle.ViewModel
import com.ericg.debtsmanager.utils.FirebaseUtils.mAuth
import com.ericg.debtsmanager.utils.FirebaseUtils.userDataBase
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.util.*

open class GetDataViewModel : ViewModel() {

    fun getData(type: String): Task<QuerySnapshot>? {

        val querySnapshot: Task<QuerySnapshot>?
        val userUID = mAuth?.currentUser?.uid as String

        querySnapshot = if (type.toLowerCase(Locale.ROOT) == "debtor") {
            userDataBase
                ?.collection("users/${userUID}/debtors")
                ?.orderBy("name", Query.Direction.ASCENDING)
                ?.get()
        } else {
            userDataBase
                ?.collection("users/${userUID}/myDebts")
                ?.orderBy("name", Query.Direction.ASCENDING)
                ?.get()
        }
        return querySnapshot
    }
}