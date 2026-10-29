package com.example.splitmate.domain.model

import java.time.Instant

class CorrectionRequest private constructor(
    val id: String,
    val groupId: String,
    val expenseId: String,
    val requestedByUserId: String,
    val reviewerUserId: String,
    val reason: String,
    val createdAt: Instant,
    initialStatus: CorrectionStatus,
    initialResolvedAt: Instant?,
    initialResolvedByUserId: String?
) {
    var status: CorrectionStatus = initialStatus
        private set

    var resolvedAt: Instant? = initialResolvedAt
        private set

    var resolvedByUserId: String? =
        initialResolvedByUserId
        private set

    init {
        require(id.isNotBlank()) {
            "Correction request ID cannot be blank"
        }

        require(groupId.isNotBlank()) {
            "Group ID cannot be blank"
        }

        require(expenseId.isNotBlank()) {
            "Expense ID cannot be blank"
        }

        require(requestedByUserId.isNotBlank()) {
            "Requester user ID cannot be blank"
        }

        require(reviewerUserId.isNotBlank()) {
            "Reviewer user ID cannot be blank"
        }

        require(reason.isNotBlank()) {
            "Correction reason cannot be blank"
        }

        require(reason.length <= 500) {
            "Correction reason cannot exceed 500 characters"
        }

        val isPending =
            status == CorrectionStatus.PENDING

        require(
            if (isPending) {
                resolvedAt == null &&
                        resolvedByUserId == null
            } else {
                resolvedAt != null &&
                        !resolvedByUserId.isNullOrBlank()
            }
        ) {
            "Correction status and resolution details are inconsistent"
        }
    }

    fun approve(
        requestedByUserId: String,
        approvedAt: Instant
    ) {
        require(requestedByUserId == reviewerUserId) {
            "Only the correction reviewer can approve it"
        }

        transitionTo(
            newStatus = CorrectionStatus.APPROVED,
            resolutionTime = approvedAt,
            resolvedByUserId = requestedByUserId
        )
    }

    fun reject(
        requestedByUserId: String,
        rejectedAt: Instant
    ) {
        require(requestedByUserId == reviewerUserId) {
            "Only the correction reviewer can reject it"
        }

        transitionTo(
            newStatus = CorrectionStatus.REJECTED,
            resolutionTime = rejectedAt,
            resolvedByUserId = requestedByUserId
        )
    }

    fun cancel(
        requestedByUserId: String,
        cancelledAt: Instant
    ) {
        require(requestedByUserId == this.requestedByUserId) {
            "Only the correction requester can cancel it"
        }

        transitionTo(
            newStatus = CorrectionStatus.CANCELLED,
            resolutionTime = cancelledAt,
            resolvedByUserId = requestedByUserId
        )
    }

    private fun transitionTo(
        newStatus: CorrectionStatus,
        resolutionTime: Instant,
        resolvedByUserId: String
    ) {
        check(status == CorrectionStatus.PENDING) {
            "Only a pending correction can change status"
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
            expenseId: String,
            requestedByUserId: String,
            reviewerUserId: String,
            reason: String,
            createdAt: Instant
        ): CorrectionRequest {
            return CorrectionRequest(
                id = id,
                groupId = groupId,
                expenseId = expenseId,
                requestedByUserId = requestedByUserId,
                reviewerUserId = reviewerUserId,
                reason = reason,
                createdAt = createdAt,
                initialStatus = CorrectionStatus.PENDING,
                initialResolvedAt = null,
                initialResolvedByUserId = null
            )
        }

        internal fun restore(
            id: String,
            groupId: String,
            expenseId: String,
            requestedByUserId: String,
            reviewerUserId: String,
            reason: String,
            createdAt: Instant,
            status: CorrectionStatus,
            resolvedAt: Instant?,
            resolvedByUserId: String?
        ): CorrectionRequest {
            return CorrectionRequest(
                id = id,
                groupId = groupId,
                expenseId = expenseId,
                requestedByUserId = requestedByUserId,
                reviewerUserId = reviewerUserId,
                reason = reason,
                createdAt = createdAt,
                initialStatus = status,
                initialResolvedAt = resolvedAt,
                initialResolvedByUserId =
                    resolvedByUserId
            )
        }
    }
}