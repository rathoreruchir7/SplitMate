package com.example.splitmate.domain.split

import com.example.splitmate.domain.model.ExpenseShare

object EqualSplitStrategy : SplitStrategy {

    override fun calculateShares(
        totalAmountInPaise: Long,
        participantUserIds: List<String>
    ): List<ExpenseShare> {
        require(totalAmountInPaise > 0) {
            "Total amount must be greater than zero"
        }

        require(participantUserIds.isNotEmpty()) {
            "At least one participant is required"
        }

        require(participantUserIds.none { it.isBlank() }) {
            "Participant user ID cannot be blank"
        }

        require(participantUserIds.distinct().size == participantUserIds.size) {
            "Participants cannot contain duplicates"
        }

        require(totalAmountInPaise >= participantUserIds.size.toLong()) {
            "Amount is too small to split between all participants"
        }

        val sortedUserIds = participantUserIds.sorted()

        val baseShare =
            totalAmountInPaise / sortedUserIds.size

        val remainder =
            totalAmountInPaise % sortedUserIds.size

        return sortedUserIds.mapIndexed { index, userId ->
            val extraPaise =
                if (index.toLong() < remainder) 1 else 0

            ExpenseShare(
                userId = userId,
                amountInPaise = baseShare + extraPaise
            )
        }
    }
}