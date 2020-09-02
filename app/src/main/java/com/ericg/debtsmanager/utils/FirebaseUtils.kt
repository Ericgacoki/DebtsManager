/*
 * Copyright (c)  Updated by eric on  9/2/20 8:38 PM
 */

package com.ericg.debtsmanager.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

object FirebaseUtils {
    // init lazily
    val mAuth: FirebaseAuth? by lazy { FirebaseAuth.getInstance() }
    val mUser: FirebaseUser? by lazy { mAuth!!.currentUser }
    val userDataBase: FirebaseFirestore? by lazy { FirebaseFirestore.getInstance() }

    init {
        /* Enable offline support */
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        userDataBase?.firestoreSettings = settings
    }
}