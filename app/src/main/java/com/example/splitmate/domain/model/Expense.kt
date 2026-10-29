package com.example.splitmate.domain.model

import java.time.Instant

class Expense(
    val id: String,
    val groupId: String,
    val description: String,
    val totalAmountInPaise: Long,
    val paidByUserId: String,
    val createdByUserId: String,
    shares: List<ExpenseShare>,
    val createdAt: Instant
) {

    private val shareList: List<ExpenseShare> =
        shares.toList()

    val shares: List<ExpenseShare>
        get() = shareList.toList()

    init {
        require(id.isNotBlank()) {
            "Expense ID cannot be blank"
        }

        require(groupId.isNotBlank()) {
            "Group ID cannot be blank"
        }

        require(description.isNotBlank()) {
            "Expense description cannot be blank"
        }

        require(totalAmountInPaise > 0) {
            "Expense amount must be greater than zero"
        }

        require(paidByUserId.isNotBlank()) {
            "Payer user ID cannot be blank"
        }

        require(createdByUserId.isNotBlank()) {
            "Creator user ID cannot be blank"
        }

        require(shareList.isNotEmpty()) {
            "Expense must have at least one share"
        }

        require(
            shareList
                .map { it.userId }
                .distinct()
                .size == shareList.size
        ) {
            "Expense cannot contain duplicate shares"
        }

        val totalShareAmount = shareList.sumOf {
            it.amountInPaise
        }

        require(totalShareAmount == totalAmountInPaise) {
            "Expense shares must equal the total amount"
        }
    }
}