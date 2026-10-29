package com.example.splitmate.domain.split

import com.example.splitmate.domain.model.ExpenseShare

interface SplitStrategy <I : SplitInput> {

    fun calculateShares(
        totalAmountInPaise: Long,
        input: I
    ): List<ExpenseShare>
}