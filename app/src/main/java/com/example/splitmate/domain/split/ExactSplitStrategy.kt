package com.example.splitmate.domain.split

import com.example.splitmate.domain.model.ExpenseShare

object ExactSplitStrategy :
    SplitStrategy<ExactSplitInput> {

    override fun calculateShares(
        totalAmountInPaise: Long,
        input: ExactSplitInput
    ): List<ExpenseShare> {
        return calculateShares(
            totalAmountInPaise = totalAmountInPaise,
            amountsByUserId = input.amountsByUserId
        )
    }

    fun calculateShares(
        totalAmountInPaise: Long,
        amountsByUserId: Map<String, Long>
    ): List<ExpenseShare> {
        require(totalAmountInPaise > 0) {
            "Total amount must be greater than zero"
        }

        require(amountsByUserId.isNotEmpty()) {
            "At least one participant is required"
        }

        require(amountsByUserId.keys.none { it.isBlank() }) {
            "Participant user ID cannot be blank"
        }

        require(amountsByUserId.values.all { it > 0 }) {
            "Every exact share must be greater than zero"
        }

        val safeAmounts = amountsByUserId.toMap()

        val totalShareAmount = safeAmounts.values.fold(0L) {
                currentTotal,
                amount ->
            Math.addExact(currentTotal, amount)
        }

        require(totalShareAmount == totalAmountInPaise) {
            "Exact shares must equal the total amount"
        }

        return safeAmounts.entries
            .sortedBy { it.key }
            .map { entry ->
                ExpenseShare(
                    userId = entry.key,
                    amountInPaise = entry.value
                )
            }
    }
}