package com.example.creditcardtracker.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.creditcardtracker.R

class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val billingChannel = NotificationChannel(
                BILLING_CHANNEL_ID,
                "Billing Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for billing dates"
            }

            val reminderChannel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                "Due Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminders for unpaid bills"
            }

            notificationManager.createNotificationChannel(billingChannel)
            notificationManager.createNotificationChannel(reminderChannel)
        }
    }

    fun showBillingNotification(cardName: String) {
        val notification = NotificationCompat.Builder(context, BILLING_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Placeholder
            .setContentTitle("Billing Date: $cardName")
            .setContentText("Today is billing date, please manually enter the bill amount.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(cardName.hashCode(), notification)
    }

    fun showDueReminder(cardName: String) {
        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Placeholder
            .setContentTitle("Bill Reminder: $cardName")
            .setContentText("Your bill is not marked as paid. Please pay before the due date.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(cardName.hashCode() + 1, notification)
    }

    companion object {
        const val BILLING_CHANNEL_ID = "billing_channel"
        const val REMINDER_CHANNEL_ID = "reminder_channel"
    }
}
