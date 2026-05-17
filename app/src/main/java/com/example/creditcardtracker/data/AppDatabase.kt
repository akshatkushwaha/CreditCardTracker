package com.example.creditcardtracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.creditcardtracker.data.dao.BillHistoryDao
import com.example.creditcardtracker.data.dao.CreditCardDao
import com.example.creditcardtracker.data.model.BillHistory
import com.example.creditcardtracker.data.model.CreditCard

@Database(entities = [CreditCard::class, BillHistory::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun creditCardDao(): CreditCardDao
    abstract fun billHistoryDao(): BillHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "credit_card_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
