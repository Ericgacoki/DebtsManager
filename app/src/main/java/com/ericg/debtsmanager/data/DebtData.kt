/*
 * Copyright (c)  Updated by eric on  10/1/20 11:52 PM
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

    /**@__IMPORTANT__ A no-argument constructor is required by firestore documents   */

    private constructor() : this(
        "", "", "", "", 1, "", 1, 0, 0, null, null
    )
}