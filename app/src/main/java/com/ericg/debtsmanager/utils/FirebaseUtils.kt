/*
 * Copyright (c)  Updated by eric on  8/11/20 3:52 PM
 */

package com.ericg.debtsmanager.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

/**
 * @author eric
 * @date 8/11/20
 */

object FirebaseUtils{
    // init lazily
    val mAuth: FirebaseAuth? by lazy { FirebaseAuth.getInstance() }
    val mUser: FirebaseUser? by lazy { mAuth!!.currentUser }
    val userDataBase: FirebaseFirestore? by lazy { FirebaseFirestore.getInstance()}
}