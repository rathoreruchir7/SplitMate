package com.example.splitmate.domain.split

import com.example.splitmate.domain.model.ExpenseShare

interface SplitStrategy {

    fun calculateShares(
        totalAmountInPaise: Long,
        participantUserIds: List<String>
    ): List<ExpenseShare>
}