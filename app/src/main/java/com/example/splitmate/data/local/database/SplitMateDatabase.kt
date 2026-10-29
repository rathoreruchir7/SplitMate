package com.example.splitmate.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.splitmate.data.local.dao.ExpenseDao
import com.example.splitmate.data.local.dao.GroupDao
import com.example.splitmate.data.local.dao.PaymentDao
import com.example.splitmate.data.local.dao.UserDao
import com.example.splitmate.data.local.entity.ExpenseEntity
import com.example.splitmate.data.local.entity.ExpenseShareEntity
import com.example.splitmate.data.local.entity.GroupEntity
import com.example.splitmate.data.local.entity.GroupMemberEntity
import com.example.splitmate.data.local.entity.PaymentEntity
import com.example.splitmate.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        GroupEntity::class,
        GroupMemberEntity::class,
        ExpenseEntity::class,
        ExpenseShareEntity::class,
        PaymentEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class SplitMateDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun groupDao(): GroupDao

    abstract fun expenseDao(): ExpenseDao

    abstract fun paymentDao(): PaymentDao

    companion object {

        @Volatile
        private var instance: SplitMateDatabase? = null

        fun getInstance(context: Context): SplitMateDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SplitMateDatabase::class.java,
                    "splitmate.db"
                ).build().also { database ->
                    instance = database
                }
            }
        }
    }
}