package com.example.splitmate.domain.model

data class ExpenseShare(
    val userId: String,
    val amountInPaise: Long
) {
    init {
        require(userId.isNotBlank()) {
            "User ID cannot be blank"
        }

        require(amountInPaise > 0) {
            "Expense share must be greater than zero"
        }
    }
}
