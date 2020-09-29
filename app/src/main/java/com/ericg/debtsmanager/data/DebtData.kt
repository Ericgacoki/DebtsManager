/*
 * Copyright (c)  Updated by eric on  9/30/20 1:56 AM
 */

package com.ericg.debtsmanager.data

/**
 * @author eric
 * @date 8/12/20
 */

data class DebtData(
    val docID: String,
    val name: String,
    val startDate: String,
    val dueDate: String,
    val debtStatus: Int,
    val phone: String,
    val initialAmt: Int,
    val amtPaid: Int,
    val paymentsDone: Int,
    var remainingAmt: Int?,
    var progressPercentage: Int?
) {

    init {
        remainingAmt = (initialAmt - amtPaid)
        progressPercentage = (amtPaid * 100 / initialAmt)
    }

    /**@__IMPORTANT__ A no-argument constructor is required by firestore documents   */

    private constructor() : this(
        "", "", "", "", 1, "", 1, 0, 0, null, null
    )
}