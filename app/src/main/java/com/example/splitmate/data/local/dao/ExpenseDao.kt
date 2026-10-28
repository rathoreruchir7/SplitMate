package com.example.splitmate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.splitmate.data.local.entity.ExpenseEntity
import com.example.splitmate.data.local.entity.ExpenseShareEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertExpense(
        expense: ExpenseEntity
    )

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertShares(
        shares: List<ExpenseShareEntity>
    )

    @Transaction
    suspend fun insertExpenseWithShares(
        expense: ExpenseEntity,
        shares: List<ExpenseShareEntity>
    ) {
        insertExpense(expense)
        insertShares(shares)
    }

    @Query(
        """
        SELECT * FROM expenses
        WHERE groupId = :groupId
        ORDER BY createdAtEpochMillis DESC
        """
    )
    fun observeExpenses(
        groupId: String
    ): Flow<List<ExpenseEntity>>

    @Query(
        """
        SELECT expense_shares.*
        FROM expense_shares
        INNER JOIN expenses
            ON expense_shares.expenseId = expenses.id
        WHERE expenses.groupId = :groupId
        ORDER BY
            expenses.createdAtEpochMillis DESC,
            expense_shares.userId ASC
        """
    )
    fun observeSharesForGroup(
        groupId: String
    ): Flow<List<ExpenseShareEntity>>

    @Query(
        """
        SELECT * FROM expense_shares
        WHERE expenseId = :expenseId
        ORDER BY userId ASC
        """
    )
    suspend fun getSharesForExpense(
        expenseId: String
    ): List<ExpenseShareEntity>

    @Query(
        """
        DELETE FROM expenses
        WHERE id = :expenseId
        """
    )
    suspend fun deleteExpense(expenseId: String): Int
}