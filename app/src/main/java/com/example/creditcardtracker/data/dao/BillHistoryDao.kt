package com.example.creditcardtracker.data.dao

import androidx.room.*
import com.example.creditcardtracker.data.model.BillHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface BillHistoryDao {
    @Query("SELECT * FROM bill_history WHERE cardId = :cardId ORDER BY billingDate DESC")
    fun getHistoryByCardId(cardId: Long): Flow<List<BillHistory>>

    @Query("SELECT * FROM bill_history ORDER BY billingDate DESC")
    fun getAllHistory(): Flow<List<BillHistory>>

    @Query("SELECT * FROM bill_history WHERE isPaid = 0 AND dueDate <= :currentTime")
    suspend fun getUnpaidBills(currentTime: Long): List<BillHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: BillHistory): Long

    @Update
    suspend fun updateBill(bill: BillHistory)

    @Delete
    suspend fun deleteBill(bill: BillHistory)
}
