/*
 * Copyright (c)  Updated by eric on  9/11/20 10:09 AM
 */

package com.ericg.debtsmanager.viewmodel

import androidx.lifecycle.ViewModel
import com.ericg.debtsmanager.utils.FirebaseUtils.mAuth
import com.ericg.debtsmanager.utils.FirebaseUtils.userDataBase
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Job

open class GetDataViewModel : ViewModel() {

    var numOfDebtors = 1F
    var numOfMyDebts = 1F

    fun getData(type: String): Task<QuerySnapshot>? {

        val querySnapshot: Task<QuerySnapshot>?

        val userUID = mAuth?.currentUser?.uid as String

        querySnapshot = if (type == "Debtor") {
            userDataBase
                ?.collection("users")
                ?.document(userUID)
                ?.collection("debtors")
                ?.orderBy("name", Query.Direction.ASCENDING)
                ?.get()
        } else {
            userDataBase
                ?.collection("users")
                ?.document(userUID)
                ?.collection("myDebts")
                ?.orderBy("name", Query.Direction.ASCENDING)
                ?.get()
        }
        return querySnapshot
    }
}