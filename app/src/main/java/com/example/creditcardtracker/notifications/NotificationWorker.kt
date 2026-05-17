package com.example.creditcardtracker.notifications

import android.content.Context
import androidx.work.*
import com.example.creditcardtracker.data.AppDatabase
import com.example.creditcardtracker.data.model.BillHistory
import kotlinx.coroutines.flow.first
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val cardDao = database.creditCardDao()
        val historyDao = database.billHistoryDao()
        val notificationHelper = NotificationHelper(applicationContext)

        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_MONTH)
        val currentTime = System.currentTimeMillis()

        // 1. Check for Billing Date
        val allCards = cardDao.getAllCards().first()
        for (card in allCards) {
            if (card.billingDay == today) {
                notificationHelper.showBillingNotification(card.name)
                
                // Create a placeholder history entry if it doesn't exist for this month
                // This is a simple implementation; in a real app, we'd check if one already exists for this billing cycle
                val dueDate = Calendar.getInstance().apply {
                    timeInMillis = currentTime
                    add(Calendar.DAY_OF_YEAR, 20)
                }.timeInMillis
                
                historyDao.insertBill(
                    BillHistory(
                        cardId = card.id,
                        billingDate = currentTime,
                        amount = null,
                        isPaid = false,
                        dueDate = dueDate
                    )
                )
            }
        }

        // 2. Check for Reminders (Unpaid bills)
        val unpaidBills = historyDao.getUnpaidBills(currentTime + TimeUnit.DAYS.toMillis(7)) // Remind up to 7 days after due date if still unpaid
        for (bill in unpaidBills) {
            val card = cardDao.getCardById(bill.cardId)
            if (card != null) {
                notificationHelper.showDueReminder(card.name)
            }
        }

        return Result.success()
    }

    companion object {
        fun scheduleDaily(context: Context) {
            val request = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
                .setConstraints(Constraints.NONE)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "daily_notifications",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
