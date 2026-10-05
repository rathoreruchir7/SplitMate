package com.example.splitmate.domain.service

import com.example.splitmate.domain.model.Group
import com.example.splitmate.domain.model.GroupMember
import com.example.splitmate.domain.model.MemberRole
import com.example.splitmate.domain.model.PaymentStatus
import org.junit.Assert
import org.junit.Test
import java.time.Instant

class PaymentFactoryTest {

    private val timestamp =
        Instant.parse("2026-10-04T10:00:00Z")

    private val group = Group(
        id = "group-1",
        name = "Flatmates",
        createdByUserId = "user-1",
        members = listOf(
            GroupMember(
                userId = "user-1",
                role = MemberRole.ADMIN,
                joinedAt = timestamp
            ),
            GroupMember(
                userId = "user-2",
                role = MemberRole.MEMBER,
                joinedAt = timestamp
            ),
            GroupMember(
                userId = "user-3",
                role = MemberRole.MEMBER,
                joinedAt = timestamp
            )
        ),
        createdAt = timestamp
    )

    @Test
    fun `creates pending payment between group members`() {
        val payment = PaymentFactory.createPayment(
            paymentId = "payment-1",
            group = group,
            payerUserId = "user-2",
            recipientUserId = "user-1",
            amountInPaise = 30_000,
            createdAt = timestamp
        )

        Assert.assertEquals("payment-1", payment.id)
        Assert.assertEquals("group-1", payment.groupId)
        Assert.assertEquals("user-2", payment.payerUserId)
        Assert.assertEquals("user-1", payment.recipientUserId)
        Assert.assertEquals(30_000L, payment.amountInPaise)

        Assert.assertEquals(
            PaymentStatus.PENDING_CONFIRMATION,
            payment.status
        )
    }

    @Test
    fun `rejects payment when payer is not a group member`() {
        val exception = Assert.assertThrows(
            IllegalArgumentException::class.java
        ) {
            PaymentFactory.createPayment(
                paymentId = "payment-2",
                group = group,
                payerUserId = "outsider",
                recipientUserId = "user-1",
                amountInPaise = 30_000,
                createdAt = timestamp
            )
        }

        Assert.assertEquals(
            "Payment payer must be a group member",
            exception.message
        )
    }

    @Test
    fun `rejects payment when recipient is not a group member`() {
        val exception = Assert.assertThrows(
            IllegalArgumentException::class.java
        ) {
            PaymentFactory.createPayment(
                paymentId = "payment-3",
                group = group,
                payerUserId = "user-2",
                recipientUserId = "outsider",
                amountInPaise = 30_000,
                createdAt = timestamp
            )
        }

        Assert.assertEquals(
            "Payment recipient must be a group member",
            exception.message
        )
    }

    @Test
    fun `payment receives group id from group`() {
        val payment = PaymentFactory.createPayment(
            paymentId = "payment-4",
            group = group,
            payerUserId = "user-3",
            recipientUserId = "user-1",
            amountInPaise = 15_000,
            createdAt = timestamp
        )

        Assert.assertEquals(group.id, payment.groupId)
    }
}