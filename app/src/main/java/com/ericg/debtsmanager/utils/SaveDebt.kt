/*
 * Copyright (c)  Updated by eric on  9/12/20 12:17 AM
 */

package com.ericg.debtsmanager.utils

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ericg.debtsmanager.data.DebtData
import com.ericg.debtsmanager.utils.FirebaseUtils.mAuth
import com.ericg.debtsmanager.utils.FirebaseUtils.userDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SaveDebt(private val type: String, private val debtData: DebtData) {
    private var done = MutableLiveData<Boolean>()
    private val savingJob: Job

    init {
        savingJob = GlobalScope.launch(Dispatchers.IO) {

            val usersCollection = userDataBase?.collection("users")
            val userUID = mAuth?.currentUser?.uid as String

            usersCollection!!.document(userUID).set(hashMapOf("this doc" to "exists"))

            if (type == "Debtor") {
                usersCollection.document(userUID).collection("debtors").add(debtData)
                    .addOnSuccessListener { done.value = true }

            } else {
                usersCollection.document(userUID).collection("myDebts").add(debtData)
                    .addOnSuccessListener { done.value =  true}
            }
        }
    }

    fun done(): LiveData<Boolean> = done

}