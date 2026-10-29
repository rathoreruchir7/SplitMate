package com.example.splitmate.domain.model

import java.time.Instant

class Payment private constructor(
    val id: String,
    val groupId: String,
    val payerUserId: String,
    val recipientUserId: String,
    val amountInPaise: Long,
    val createdAt: Instant,
    initialStatus: PaymentStatus,
    initialResolvedAt: Instant?,
    initialResolvedByUserId: String?
) {

    var status: PaymentStatus = initialStatus
        private set

    var resolvedAt: Instant? = initialResolvedAt
        private set

    var resolvedByUserId: String? =
        initialResolvedByUserId
        private set

    init {
        require(id.isNotBlank()) {
            "Payment ID cannot be blank"
        }

        require(groupId.isNotBlank()) {
            "Group ID cannot be blank"
        }

        require(payerUserId.isNotBlank()) {
            "Payer user ID cannot be blank"
        }

        require(recipientUserId.isNotBlank()) {
            "Recipient user ID cannot be blank"
        }

        require(payerUserId != recipientUserId) {
            "Payer and recipient must be different users"
        }

        require(amountInPaise > 0) {
            "Payment amount must be greater than zero"
        }

        val isPending =
            status == PaymentStatus.PENDING_CONFIRMATION

        require(
            if (isPending) {
                resolvedAt == null &&
                        resolvedByUserId == null
            } else {
                resolvedAt != null &&
                        !resolvedByUserId.isNullOrBlank()
            }
        ) {
            "Payment status and resolution details are inconsistent"
        }
    }

    fun confirm(
        requestedByUserId: String,
        confirmedAt: Instant
    ) {
        require(requestedByUserId == recipientUserId) {
            "Only the payment recipient can confirm it"
        }

        transitionTo(
            newStatus = PaymentStatus.CONFIRMED,
            resolutionTime = confirmedAt,
            resolvedByUserId = requestedByUserId
        )
    }

    fun reject(
        requestedByUserId: String,
        rejectedAt: Instant
    ) {
        require(requestedByUserId == recipientUserId) {
            "Only the payment recipient can reject it"
        }

        transitionTo(
            newStatus = PaymentStatus.REJECTED,
            resolutionTime = rejectedAt,
            resolvedByUserId = requestedByUserId
        )
    }

    fun cancel(
        requestedByUserId: String,
        cancelledAt: Instant
    ) {
        require(requestedByUserId == payerUserId) {
            "Only the payment payer can cancel it"
        }

        transitionTo(
            newStatus = PaymentStatus.CANCELLED,
            resolutionTime = cancelledAt,
            resolvedByUserId = requestedByUserId
        )
    }

    private fun transitionTo(
        newStatus: PaymentStatus,
        resolutionTime: Instant,
        resolvedByUserId: String
    ) {
        check(status == PaymentStatus.PENDING_CONFIRMATION) {
            "Only a pending payment can change status"
        }

        require(!resolutionTime.isBefore(createdAt)) {
            "Resolution time cannot be before creation time"
        }

        status = newStatus
        resolvedAt = resolutionTime
        this.resolvedByUserId = resolvedByUserId
    }

    companion object {

        fun create(
            id: String,
            groupId: String,
            payerUserId: String,
            recipientUserId: String,
            amountInPaise: Long,
            createdAt: Instant
        ): Payment {
            return Payment(
                id = id,
                groupId = groupId,
                payerUserId = payerUserId,
                recipientUserId = recipientUserId,
                amountInPaise = amountInPaise,
                createdAt = createdAt,
                initialStatus =
                    PaymentStatus.PENDING_CONFIRMATION,
                initialResolvedAt = null,
                initialResolvedByUserId = null
            )
        }

        internal fun restore(
            id: String,
            groupId: String,
            payerUserId: String,
            recipientUserId: String,
            amountInPaise: Long,
            createdAt: Instant,
            status: PaymentStatus,
            resolvedAt: Instant?,
            resolvedByUserId: String?
        ): Payment {
            return Payment(
                id = id,
                groupId = groupId,
                payerUserId = payerUserId,
                recipientUserId = recipientUserId,
                amountInPaise = amountInPaise,
                createdAt = createdAt,
                initialStatus = status,
                initialResolvedAt = resolvedAt,
                initialResolvedByUserId =
                    resolvedByUserId
            )
        }
    }
}