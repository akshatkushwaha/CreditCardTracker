package com.example.creditcardtracker

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.creditcardtracker.data.AppDatabase
import com.example.creditcardtracker.notifications.NotificationWorker
import com.example.creditcardtracker.ui.screens.*
import com.example.creditcardtracker.ui.theme.CreditCardTrackerTheme
import com.example.creditcardtracker.utils.BiometricHelper
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val cardDao = database.creditCardDao()
        val historyDao = database.billHistoryDao()
        val biometricHelper = BiometricHelper(this)

        NotificationWorker.scheduleDaily(this)

        lifecycleScope.launch {
            com.example.creditcardtracker.utils.DummyDataHelper.populateIfEmpty(cardDao, historyDao)
        }

        setContent {
            CreditCardTrackerTheme {
                val navController = rememberNavController()
                var isAuthenticated by remember { mutableStateOf(false) }

                NavHost(navController = navController, startDestination = "card_list") {
                    composable("card_list") {
                        CardListScreen(
                            cardsFlow = cardDao.getAllCards(),
                            onAddCard = { navController.navigate("add_card") },
                            onCardClick = { cardId ->
                                isAuthenticated = false
                                navController.navigate("card_details/$cardId")
                            }
                        )
                    }

                    composable("add_card") {
                        AddEditCardScreen(
                            onSaveCard = { card ->
                                lifecycleScope.launch {
                                    val cardId = cardDao.insertCard(card)

                                    // Generate initial bill for the current/previous billing cycle
                                    val calendar = Calendar.getInstance()
                                    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

                                    if (currentDay < card.billingDay) {
                                        // If billing day hasn't arrived this month, generate for previous month
                                        calendar.add(Calendar.MONTH, -1)
                                    }

                                    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                                    calendar.set(Calendar.DAY_OF_MONTH, card.billingDay.coerceAtMost(maxDay))
                                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                                    calendar.set(Calendar.MINUTE, 0)
                                    calendar.set(Calendar.SECOND, 0)
                                    calendar.set(Calendar.MILLISECOND, 0)

                                    val billingTime = calendar.timeInMillis
                                    calendar.add(Calendar.DAY_OF_YEAR, 20)
                                    val dueDate = calendar.timeInMillis

                                    historyDao.insertBill(
                                        com.example.creditcardtracker.data.model.BillHistory(
                                            cardId = cardId,
                                            billingDate = billingTime,
                                            amount = null,
                                            isPaid = false,
                                            dueDate = dueDate
                                        )
                                    )

                                    navController.popBackStack()
                                }
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("card_details/{cardId}") { backStackEntry ->
                        val cardId = backStackEntry.arguments?.getString("cardId")?.toLongOrNull() ?: 0
                        val cardState = produceState<com.example.creditcardtracker.data.model.CreditCard?>(initialValue = null) {
                            value = cardDao.getCardById(cardId)
                        }

                        CardDetailsScreen(
                            card = cardState.value,
                            historyFlow = historyDao.getHistoryByCardId(cardId),
                            onBack = { navController.popBackStack() },
                            onDelete = { card ->
                                lifecycleScope.launch {
                                    cardDao.deleteCard(card)
                                    navController.popBackStack()
                                }
                            },
                            onAuthenticate = {
                                if (biometricHelper.canAuthenticate()) {
                                    biometricHelper.showBiometricPrompt(
                                        title = "Unlock Details",
                                        subtitle = "Authenticate to see PIN and CVV",
                                        onSuccess = { isAuthenticated = true },
                                        onError = { /* Handle error */ }
                                    )
                                } else {
                                    // Fallback if no biometrics
                                    isAuthenticated = true
                                }
                            },
                            onHideSensitive = { isAuthenticated = false },
                            onUpdateBill = { bill ->
                                lifecycleScope.launch {
                                    historyDao.updateBill(bill)
                                }
                            },
                            isAuthenticated = isAuthenticated,
                            onEdit = { card ->
                                navController.navigate("edit_card/${card.id}")
                            }
                        )
                    }

                    composable("edit_card/{cardId}") { backStackEntry ->
                        val cardId = backStackEntry.arguments?.getString("cardId")?.toLongOrNull() ?: 0
                        val cardState = produceState<com.example.creditcardtracker.data.model.CreditCard?>(initialValue = null) {
                            value = cardDao.getCardById(cardId)
                        }

                        AddEditCardScreen(
                            initialCard = cardState.value,
                            onSaveCard = { updatedCard ->
                                lifecycleScope.launch {
                                    cardDao.updateCard(updatedCard)
                                    navController.popBackStack()
                                }
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // history screen was here
                }
            }
        }
    }
}
