/*
 * Copyright (c)  Updated by eric on  9/5/20 1:16 AM
 */

package com.ericg.debtsmanager.utils

import com.ericg.debtsmanager.data.DebtData
import com.ericg.debtsmanager.utils.FirebaseUtils.mAuth
import com.ericg.debtsmanager.utils.FirebaseUtils.userDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

open class SaveDebt(private val type: String, private val debtData: DebtData) {
    private var done = false
    val savingJob: Job

    init {
        savingJob = GlobalScope.launch(Dispatchers.IO) {

            val usersCollection = userDataBase?.collection("users")
            val userUID = mAuth?.currentUser?.uid as String

            usersCollection!!.document(userUID).set(hashMapOf("this doc" to "exists"))

            if (type == "Debtor") {
                usersCollection.document(userUID).collection("debtors").add(debtData)
                    .addOnCompleteListener { saving -> done = saving.isSuccessful }

            } else {
                usersCollection.document(userUID).collection("myDebts").add(debtData)
                    .addOnCompleteListener { saving -> done = saving.isSuccessful }
            }
        }
    }

    fun done(): Boolean {
        return if (this.savingJob.isCompleted) done else false
    }
}