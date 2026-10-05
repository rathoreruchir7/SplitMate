package com.example.splitmate.domain.service

import com.example.splitmate.domain.model.Group
import com.example.splitmate.domain.model.Payment
import java.time.Instant

object PaymentFactory {

    fun createPayment(
        paymentId: String,
        group: Group,
        payerUserId: String,
        recipientUserId: String,
        amountInPaise: Long,
        createdAt: Instant
    ): Payment {
        require(group.hasMember(payerUserId)) {
            "Payment payer must be a group member"
        }

        require(group.hasMember(recipientUserId)) {
            "Payment recipient must be a group member"
        }

        return Payment.create(
            id = paymentId,
            groupId = group.id,
            payerUserId = payerUserId,
            recipientUserId = recipientUserId,
            amountInPaise = amountInPaise,
            createdAt = createdAt
        )
    }
}