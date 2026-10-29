package com.example.splitmate.domain.service

import com.example.splitmate.domain.model.Expense
import com.example.splitmate.domain.model.ExpenseShare
import com.example.splitmate.domain.model.Group
import com.example.splitmate.domain.split.EqualSplitStrategy
import com.example.splitmate.domain.split.ExactSplitStrategy
import java.time.Instant

object ExpenseFactory {
    fun createEqualExpense(
        expenseId: String,
        group: Group,
        description: String,
        totalAmountInPaise: Long,
        paidByUserId: String,
        createdByUserId: String,
        participantUserIds: List<String>,
        createdAt: Instant
    ): Expense {
        val shares = EqualSplitStrategy.calculateShares(
            totalAmountInPaise = totalAmountInPaise,
            participantUserIds = participantUserIds
        )

        return createExpense(
            expenseId = expenseId,
            group = group,
            description = description,
            totalAmountInPaise = totalAmountInPaise,
            paidByUserId = paidByUserId,
            createdByUserId = createdByUserId,
            shares = shares,
            createdAt = createdAt
        )
    }

    fun createExactExpense(
        expenseId: String,
        group: Group,
        description: String,
        totalAmountInPaise: Long,
        paidByUserId: String,
        createdByUserId: String,
        amountsByUserId: Map<String, Long>,
        createdAt: Instant
    ): Expense {
        val shares = ExactSplitStrategy.calculateShares(
            totalAmountInPaise = totalAmountInPaise,
            amountsByUserId = amountsByUserId
        )

        return createExpense(
            expenseId = expenseId,
            group = group,
            description = description,
            totalAmountInPaise = totalAmountInPaise,
            paidByUserId = paidByUserId,
            createdByUserId = createdByUserId,
            shares = shares,
            createdAt = createdAt
        )
    }

    private fun createExpense(
        expenseId: String,
        group: Group,
        description: String,
        totalAmountInPaise: Long,
        paidByUserId: String,
        createdByUserId: String,
        shares: List<ExpenseShare>,
        createdAt: Instant
    ): Expense {
        require(group.hasMember(paidByUserId)) {
            "Payer must be a group member"
        }

        require(group.hasMember(createdByUserId)) {
            "Expense creator must be a group member"
        }

        require(
            shares.all { share ->
                group.hasMember(share.userId)
            }
        ) {
            "All expense participants must be group members"
        }

        return Expense(
            id = expenseId,
            groupId = group.id,
            description = description,
            totalAmountInPaise = totalAmountInPaise,
            paidByUserId = paidByUserId,
            createdByUserId = createdByUserId,
            shares = shares,
            createdAt = createdAt
        )
    }
}