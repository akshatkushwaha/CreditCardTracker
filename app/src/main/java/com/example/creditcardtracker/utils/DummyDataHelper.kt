package com.example.creditcardtracker.utils

import com.example.creditcardtracker.data.dao.BillHistoryDao
import com.example.creditcardtracker.data.dao.CreditCardDao
import com.example.creditcardtracker.data.model.BillHistory
import com.example.creditcardtracker.data.model.CreditCard
import kotlinx.coroutines.flow.first
import java.util.*
import kotlin.random.Random

object DummyDataHelper {
    suspend fun populateIfEmpty(cardDao: CreditCardDao, historyDao: BillHistoryDao) {
        val existingCards = cardDao.getAllCards().first()
        if (existingCards.isNotEmpty()) return

        val cards = listOf(
            CreditCard(name = "HDFC Millennia", holderName = "John Doe", number = "4567123456789012", network = "Visa", validDate = "05/28", cvv = "123", pin = "1122", billingDay = 2),
            CreditCard(name = "Axis Ace", holderName = "John Doe", number = "5123987654321098", network = "Mastercard", validDate = "12/26", cvv = "456", pin = "3344", billingDay = 18),
            CreditCard(name = "ICICI Amazon Pay", holderName = "Jane Smith", number = "4321567843215678", network = "Visa", validDate = "08/29", cvv = "789", pin = "5566", billingDay = 10),
            CreditCard(name = "SBI SimplyClick", holderName = "John Doe", number = "6071234567890123", network = "Rupay", validDate = "02/27", cvv = "012", pin = "7788", billingDay = 25),
            CreditCard(name = "Amex Platinum", holderName = "John Doe", number = "371234567890123", network = "American Express", validDate = "11/30", cvv = "9876", pin = "9900", billingDay = 5)
        )

        val calendar = Calendar.getInstance()
        val currentTime = System.currentTimeMillis()

        for (card in cards) {
            val cardId = cardDao.insertCard(card)
            
            // Generate 2 years (24 months) of history
            for (monthOffset in -24..0) {
                val billCalendar = Calendar.getInstance().apply {
                    add(Calendar.MONTH, monthOffset)
                    set(Calendar.DAY_OF_MONTH, card.billingDay)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val billingDate = billCalendar.timeInMillis
                
                // Due date is 20 days after billing
                val dueCalendar = (billCalendar.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_YEAR, 20)
                }
                val dueDate = dueCalendar.timeInMillis

                // Randomize if paid or not (older ones are mostly paid)
                val isPaid = if (monthOffset < 0) {
                    Random.nextDouble() > 0.1 // 90% chance paid for past months
                } else {
                    false // Current month is unpaid
                }

                // Randomize amount between 500 and 50000
                val amount = if (monthOffset <= 0) {
                    Random.nextDouble(500.0, 50000.0)
                } else {
                    null // Future months (not used here but for logic)
                }

                historyDao.insertBill(
                    BillHistory(
                        cardId = cardId,
                        billingDate = billingDate,
                        amount = if (monthOffset == 0 && Random.nextBoolean()) null else amount, // Some current ones missing amount
                        isPaid = isPaid,
                        dueDate = dueDate
                    )
                )
            }
        }
    }
}
