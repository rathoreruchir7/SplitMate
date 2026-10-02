package com.example.splitmate.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.Instant

class PaymentTest {

    private val createdAt =
        Instant.parse("2026-10-02T10:00:00Z")

    private val resolvedAt =
        Instant.parse("2026-10-02T11:00:00Z")

    private fun createPayment(): Payment {
        return Payment.create(
            id = "payment-1",
            groupId = "group-1",
            payerUserId = "user-2",
            recipientUserId = "user-1",
            amountInPaise = 30_000,
            createdAt = createdAt
        )
    }

    @Test
    fun `new payment starts pending`() {
        val payment = createPayment()

        assertEquals(
            PaymentStatus.PENDING_CONFIRMATION,
            payment.status
        )

        assertNull(payment.resolvedAt)
        assertNull(payment.resolvedByUserId)
    }

    @Test
    fun `recipient can confirm pending payment`() {
        val payment = createPayment()

        payment.confirm(
            requestedByUserId = "user-1",
            confirmedAt = resolvedAt
        )

        assertEquals(
            PaymentStatus.CONFIRMED,
            payment.status
        )

        assertEquals(resolvedAt, payment.resolvedAt)
        assertEquals("user-1", payment.resolvedByUserId)
    }

    @Test
    fun `recipient can reject pending payment`() {
        val payment = createPayment()

        payment.reject(
            requestedByUserId = "user-1",
            rejectedAt = resolvedAt
        )

        assertEquals(
            PaymentStatus.REJECTED,
            payment.status
        )

        assertEquals("user-1", payment.resolvedByUserId)
    }

    @Test
    fun `payer can cancel pending payment`() {
        val payment = createPayment()

        payment.cancel(
            requestedByUserId = "user-2",
            cancelledAt = resolvedAt
        )

        assertEquals(
            PaymentStatus.CANCELLED,
            payment.status
        )

        assertEquals("user-2", payment.resolvedByUserId)
    }

    @Test
    fun `non recipient cannot confirm payment`() {
        val payment = createPayment()

        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            payment.confirm(
                requestedByUserId = "user-3",
                confirmedAt = resolvedAt
            )
        }

        assertEquals(
            "Only the payment recipient can confirm it",
            exception.message
        )

        assertEquals(
            PaymentStatus.PENDING_CONFIRMATION,
            payment.status
        )
    }

    @Test
    fun `confirmed payment cannot change state again`() {
        val payment = createPayment()

        payment.confirm(
            requestedByUserId = "user-1",
            confirmedAt = resolvedAt
        )

        val exception = assertThrows(
            IllegalStateException::class.java
        ) {
            payment.reject(
                requestedByUserId = "user-1",
                rejectedAt = resolvedAt.plusSeconds(60)
            )
        }

        assertEquals(
            "Only a pending payment can change status",
            exception.message
        )
    }

    @Test
    fun `resolution time cannot be before creation time`() {
        val payment = createPayment()

        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            payment.confirm(
                requestedByUserId = "user-1",
                confirmedAt = createdAt.minusSeconds(60)
            )
        }

        assertEquals(
            "Resolution time cannot be before creation time",
            exception.message
        )

        assertEquals(
            PaymentStatus.PENDING_CONFIRMATION,
            payment.status
        )
    }
}