package com.example.splitmate.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.Instant

class CorrectionRequestTest {

    private val createdAt =
        Instant.parse("2026-10-08T10:00:00Z")

    private val resolvedAt =
        Instant.parse("2026-10-08T11:00:00Z")

    private fun createRequest(): CorrectionRequest {
        return CorrectionRequest.create(
            id = "correction-1",
            groupId = "group-1",
            expenseId = "expense-1",
            requestedByUserId = "user-2",
            reviewerUserId = "user-1",
            reason = "The dinner amount includes a personal item",
            createdAt = createdAt
        )
    }

    @Test
    fun `new correction request starts pending`() {
        val request = createRequest()

        assertEquals(
            CorrectionStatus.PENDING,
            request.status
        )

        assertNull(request.resolvedAt)
        assertNull(request.resolvedByUserId)
    }

    @Test
    fun `reviewer can approve pending correction`() {
        val request = createRequest()

        request.approve(
            requestedByUserId = "user-1",
            approvedAt = resolvedAt
        )

        assertEquals(
            CorrectionStatus.APPROVED,
            request.status
        )

        assertEquals(resolvedAt, request.resolvedAt)
        assertEquals("user-1", request.resolvedByUserId)
    }

    @Test
    fun `reviewer can reject pending correction`() {
        val request = createRequest()

        request.reject(
            requestedByUserId = "user-1",
            rejectedAt = resolvedAt
        )

        assertEquals(
            CorrectionStatus.REJECTED,
            request.status
        )

        assertEquals("user-1", request.resolvedByUserId)
    }

    @Test
    fun `requester can cancel pending correction`() {
        val request = createRequest()

        request.cancel(
            requestedByUserId = "user-2",
            cancelledAt = resolvedAt
        )

        assertEquals(
            CorrectionStatus.CANCELLED,
            request.status
        )

        assertEquals("user-2", request.resolvedByUserId)
    }

    @Test
    fun `non reviewer cannot approve correction`() {
        val request = createRequest()

        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            request.approve(
                requestedByUserId = "user-3",
                approvedAt = resolvedAt
            )
        }

        assertEquals(
            "Only the correction reviewer can approve it",
            exception.message
        )

        assertEquals(
            CorrectionStatus.PENDING,
            request.status
        )
    }

    @Test
    fun `non requester cannot cancel correction`() {
        val request = createRequest()

        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            request.cancel(
                requestedByUserId = "user-3",
                cancelledAt = resolvedAt
            )
        }

        assertEquals(
            "Only the correction requester can cancel it",
            exception.message
        )
    }

    @Test
    fun `approved correction cannot change state again`() {
        val request = createRequest()

        request.approve(
            requestedByUserId = "user-1",
            approvedAt = resolvedAt
        )

        val exception = assertThrows(
            IllegalStateException::class.java
        ) {
            request.reject(
                requestedByUserId = "user-1",
                rejectedAt = resolvedAt.plusSeconds(60)
            )
        }

        assertEquals(
            "Only a pending correction can change status",
            exception.message
        )
    }

    @Test
    fun `resolution time cannot be before creation time`() {
        val request = createRequest()

        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            request.approve(
                requestedByUserId = "user-1",
                approvedAt = createdAt.minusSeconds(60)
            )
        }

        assertEquals(
            "Resolution time cannot be before creation time",
            exception.message
        )

        assertEquals(
            CorrectionStatus.PENDING,
            request.status
        )
    }

    @Test
    fun `rejects blank correction reason`() {
        val exception = assertThrows(
            IllegalArgumentException::class.java
        ) {
            CorrectionRequest.create(
                id = "correction-1",
                groupId = "group-1",
                expenseId = "expense-1",
                requestedByUserId = "user-2",
                reviewerUserId = "user-1",
                reason = " ",
                createdAt = createdAt
            )
        }

        assertEquals(
            "Correction reason cannot be blank",
            exception.message
        )
    }
}