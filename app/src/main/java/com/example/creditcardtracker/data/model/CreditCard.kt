package com.example.creditcardtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "credit_cards")
@Serializable
data class CreditCard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // Nickname (e.g., Axis Ace)
    val holderName: String, // Actual card holder name
    val number: String,
    val network: String,
    val validDate: String, // MM/YY
    val cvv: String,
    val pin: String,
    val billingDay: Int // Day of month (1-31)
)
