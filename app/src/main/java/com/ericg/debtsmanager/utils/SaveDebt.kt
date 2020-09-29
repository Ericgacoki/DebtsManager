/*
 * Copyright (c)  Updated by eric on  9/30/20 1:56 AM
 */

package com.ericg.debtsmanager.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ericg.debtsmanager.data.DebtData
import com.ericg.debtsmanager.utils.FirebaseUtils.mAuth
import com.ericg.debtsmanager.utils.FirebaseUtils.userDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SaveDebt(
    private val docID: String,
    private val type: String,
    private val debtData: DebtData
) {
    private var done = MutableLiveData<Boolean>()
    private val savingJob: Job

    init {
        savingJob = GlobalScope.launch(Dispatchers.IO) {

            val usersCollection = userDataBase?.collection("users")
            val userUID = mAuth?.currentUser?.uid as String

            usersCollection!!.document(userUID).set(hashMapOf("this doc" to "exists"))

            if (type == "Debtor") {
                usersCollection.document(userUID).collection("debtors").document(docID)
                    .set(debtData)
                    .addOnCompleteListener {
                        done.value = it.isSuccessful
                    }

            } else {
                usersCollection.document(userUID).collection("myDebts").document(docID)
                    .set(debtData)
                    .addOnCompleteListener {
                        done.value = it.isSuccessful
                    }
            }
        }
    }

    fun done(): LiveData<Boolean> = done
}