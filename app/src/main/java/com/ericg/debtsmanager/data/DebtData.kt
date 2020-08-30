/*
 * Copyright (c)  Updated by eric on  8/12/20 10:55 AM
 */

package com.ericg.debtsmanager.data

/**
 * @author eric
 * @date 8/12/20
 */

data class DebtData(
    var name: String,
    var startDate: String,
    var dueDate: String,
    var debtStatus: Int,
    var phone: String,
    var initialAmt: Int,
    var amtPaid: Int,
    var paymentsDone: Int,
    var remainingAmt: Int?,
    var progressPercentage: Int?
) {
    init {
        name = name.trim()
        remainingAmt = (initialAmt - amtPaid)
        progressPercentage = (amtPaid * 100 / initialAmt)
    }
}