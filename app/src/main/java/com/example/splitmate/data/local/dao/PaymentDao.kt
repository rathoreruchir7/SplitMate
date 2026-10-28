package com.example.splitmate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.splitmate.data.local.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(payment: PaymentEntity)

    @Update
    suspend fun update(payment: PaymentEntity): Int

    @Query(
        """
        SELECT * FROM payments
        WHERE id = :paymentId
        LIMIT 1
        """
    )
    suspend fun getById(
        paymentId: String
    ): PaymentEntity?

    @Query(
        """
        SELECT * FROM payments
        WHERE groupId = :groupId
        ORDER BY createdAtEpochMillis DESC
        """
    )
    fun observePayments(
        groupId: String
    ): Flow<List<PaymentEntity>>

    @Query(
        """
        SELECT * FROM payments
        WHERE recipientUserId = :userId
            AND status = 'PENDING_CONFIRMATION'
        ORDER BY createdAtEpochMillis DESC
        """
    )
    fun observePendingForRecipient(
        userId: String
    ): Flow<List<PaymentEntity>>

    @Query(
        """
        DELETE FROM payments
        WHERE id = :paymentId
        """
    )
    suspend fun deletePayment(paymentId: String): Int
}