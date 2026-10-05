package com.example.splitmate.domain.service

import com.example.splitmate.domain.model.Expense
import com.example.splitmate.domain.model.Group
import com.example.splitmate.domain.model.Payment
import com.example.splitmate.domain.model.PaymentStatus

object BalanceCalculator {

    fun calculate(
        group: Group,
        expenses: List<Expense>,
        payments: List<Payment> = emptyList()
    ): Map<String, Long> {
        require(
            expenses.map { it.id }.distinct().size ==
                    expenses.size
        ) {
            "Expenses cannot contain duplicates"
        }

        require(
            payments.map { it.id }.distinct().size ==
                    payments.size
        ) {
            "Payments cannot contain duplicates"
        }

        val balances = group.members
            .associate { member ->
                member.userId to 0L
            }
            .toMutableMap()

        expenses.forEach { expense ->
            require(expense.groupId == group.id) {
                "Expense does not belong to the group"
            }

            require(group.hasMember(expense.paidByUserId)) {
                "Expense payer must be a group member"
            }

            val currentPayerBalance =
                balances.getValue(expense.paidByUserId)

            balances[expense.paidByUserId] =
                Math.addExact(
                    currentPayerBalance,
                    expense.totalAmountInPaise
                )

            expense.shares.forEach { share ->
                require(group.hasMember(share.userId)) {
                    "Expense participant must be a group member"
                }

                val currentParticipantBalance =
                    balances.getValue(share.userId)

                balances[share.userId] =
                    Math.subtractExact(
                        currentParticipantBalance,
                        share.amountInPaise
                    )
            }
        }

        payments.forEach { payment ->
            require(payment.groupId == group.id) {
                "Payment does not belong to the group"
            }

            require(group.hasMember(payment.payerUserId)) {
                "Payment payer must be a group member"
            }

            require(group.hasMember(payment.recipientUserId)) {
                "Payment recipient must be a group member"
            }

            if (payment.status == PaymentStatus.CONFIRMED) {
                val currentPayerBalance =
                    balances.getValue(payment.payerUserId)

                balances[payment.payerUserId] =
                    Math.addExact(
                        currentPayerBalance,
                        payment.amountInPaise
                    )

                val currentRecipientBalance =
                    balances.getValue(payment.recipientUserId)

                balances[payment.recipientUserId] =
                    Math.subtractExact(
                        currentRecipientBalance,
                        payment.amountInPaise
                    )
            }
        }

        val totalBalance = balances.values.fold(0L) {
                currentTotal,
                balance ->
            Math.addExact(currentTotal, balance)
        }

        check(totalBalance == 0L) {
            "Group balances must sum to zero"
        }

        return balances.toMap()
    }
}