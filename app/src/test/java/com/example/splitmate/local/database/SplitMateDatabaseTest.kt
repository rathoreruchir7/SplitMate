package com.example.splitmate.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.splitmate.data.local.entity.ExpenseEntity
import com.example.splitmate.data.local.entity.ExpenseShareEntity
import com.example.splitmate.data.local.entity.GroupEntity
import com.example.splitmate.data.local.entity.GroupMemberEntity
import com.example.splitmate.data.local.entity.PaymentEntity
import com.example.splitmate.data.local.entity.UserEntity
import com.example.splitmate.domain.model.MemberRole
import com.example.splitmate.domain.model.PaymentStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplitMateDatabaseTest {

    private lateinit var database: SplitMateDatabase

    private val timestamp = 1_798_000_000_000L

    @Before
    fun setUp() {
        val context: Context =
            InstrumentationRegistry
                .getInstrumentation()
                .targetContext

        database = Room.inMemoryDatabaseBuilder(
            context,
            SplitMateDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `stores and reads complete group data`() = runBlocking {
        val userDao = database.userDao()
        val groupDao = database.groupDao()
        val expenseDao = database.expenseDao()
        val paymentDao = database.paymentDao()

        val users = listOf(
            UserEntity(
                id = "user-1",
                name = "Ruchir",
                email = "ruchir@example.com"
            ),
            UserEntity(
                id = "user-2",
                name = "Aman",
                email = "aman@example.com"
            )
        )

        userDao.insertAll(users)

        val group = GroupEntity(
            id = "group-1",
            name = "Flatmates",
            createdByUserId = "user-1",
            createdAtEpochMillis = timestamp
        )

        val members = listOf(
            GroupMemberEntity(
                groupId = "group-1",
                userId = "user-1",
                role = MemberRole.ADMIN.name,
                joinedAtEpochMillis = timestamp
            ),
            GroupMemberEntity(
                groupId = "group-1",
                userId = "user-2",
                role = MemberRole.MEMBER.name,
                joinedAtEpochMillis = timestamp
            )
        )

        groupDao.insertGroupWithMembers(
            group = group,
            members = members
        )

        val expense = ExpenseEntity(
            id = "expense-1",
            groupId = "group-1",
            description = "Dinner",
            totalAmountInPaise = 60_000,
            paidByUserId = "user-1",
            createdByUserId = "user-1",
            createdAtEpochMillis = timestamp
        )

        val shares = listOf(
            ExpenseShareEntity(
                expenseId = "expense-1",
                userId = "user-1",
                amountInPaise = 30_000
            ),
            ExpenseShareEntity(
                expenseId = "expense-1",
                userId = "user-2",
                amountInPaise = 30_000
            )
        )

        expenseDao.insertExpenseWithShares(
            expense = expense,
            shares = shares
        )

        val payment = PaymentEntity(
            id = "payment-1",
            groupId = "group-1",
            payerUserId = "user-2",
            recipientUserId = "user-1",
            amountInPaise = 30_000,
            status = PaymentStatus.PENDING_CONFIRMATION.name,
            createdAtEpochMillis = timestamp,
            resolvedAtEpochMillis = null,
            resolvedByUserId = null
        )

        paymentDao.insert(payment)

        val storedGroup =
            groupDao.observeGroup("group-1").first()

        val storedMembers =
            groupDao.observeMembers("group-1").first()

        val storedExpenses =
            expenseDao.observeExpenses("group-1").first()

        val storedShares =
            expenseDao.getSharesForExpense("expense-1")

        val storedPayment =
            paymentDao.getById("payment-1")

        assertEquals("Flatmates", storedGroup?.name)
        assertEquals(2, storedMembers.size)
        assertEquals(1, storedExpenses.size)
        assertEquals("Dinner", storedExpenses.first().description)
        assertEquals(2, storedShares.size)

        assertEquals(
            PaymentStatus.PENDING_CONFIRMATION.name,
            storedPayment?.status
        )

        // Verify foreign-key cascade behavior.
        val deletedRows =
            groupDao.deleteGroup("group-1")

        assertEquals(1, deletedRows)

        assertNull(
            groupDao.observeGroup("group-1").first()
        )

        assertTrue(
            groupDao.observeMembers("group-1")
                .first()
                .isEmpty()
        )

        assertTrue(
            expenseDao.observeExpenses("group-1")
                .first()
                .isEmpty()
        )

        assertTrue(
            expenseDao.getSharesForExpense("expense-1")
                .isEmpty()
        )

        assertNull(
            paymentDao.getById("payment-1")
        )
    }
}