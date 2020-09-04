/*
 * Copyright (c)  Updated by eric on  9/4/20 3:40 AM
 */

package com.ericg.debtsmanager.utils

import com.ericg.debtsmanager.data.DebtData
import com.ericg.debtsmanager.utils.FirebaseUtils.mAuth
import com.ericg.debtsmanager.utils.FirebaseUtils.userDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SaveDebt(val type: String, private val  debtData: DebtData) {
    init {

        // get collection
        val usersCollection = userDataBase?.collection("users")
        val userUID = mAuth?.currentUser?.uid as String

        GlobalScope.launch(Dispatchers.IO) {
            usersCollection!!.document(userUID).set(hashMapOf("doc" to "exists"))

            if (type == "Debtor") {
                usersCollection.document(userUID).collection("debtors").add(debtData)
            } else {
                usersCollection.document(userUID).collection("myDebts").add(debtData)
            }
        }
    }
}