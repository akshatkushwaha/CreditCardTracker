package com.example.creditcardtracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "bill_history",
    foreignKeys = [
        ForeignKey(
            entity = CreditCard::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Serializable
data class BillHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardId: Long,
    val billingDate: Long, // Timestamp
    val amount: Double?,
    val isPaid: Boolean,
    val dueDate: Long, // Timestamp (automatically 20 days after billing)
    val paymentDate: Long? = null // Timestamp of when it was marked as paid
)
