package com.example.creditcardtracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creditcardtracker.data.model.BillHistory
import com.example.creditcardtracker.data.model.CreditCard
import com.example.creditcardtracker.utils.BankBranding
import com.example.creditcardtracker.utils.NetworkBranding
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailsScreen(
    card: CreditCard?,
    historyFlow: Flow<List<BillHistory>>,
    onBack: () -> Unit,
    onDelete: (CreditCard) -> Unit,
    onAuthenticate: () -> Unit,
    onUpdateBill: (BillHistory) -> Unit,
    isAuthenticated: Boolean,
    onHideSensitive: () -> Unit,
    onEdit: (CreditCard) -> Unit
) {
    if (card == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Card not found")
        }
        return
    }

    val history by historyFlow.collectAsState(initial = emptyList())
    var showAllHistory by remember { mutableStateOf(false) }
    var showAmountDialog by remember { mutableStateOf<BillHistory?>(null) }

    val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(card.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(card) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { onDelete(card) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Visual Credit Card Graphic
                CreditCardGraphic(card = card, isAuthenticated = isAuthenticated)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Security Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
        DetailItem(
            label = "CVV", 
            value = if (isAuthenticated) card.cvv else "***"
        )
        DetailItem(
            label = "PIN", 
            value = if (isAuthenticated) card.pin else "****"
        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (!isAuthenticated) {
                            Button(
                                onClick = onAuthenticate,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Reveal Security Codes")
                            }
                        } else {
                            OutlinedButton(
                                onClick = onHideSensitive,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.VisibilityOff, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Mask Security Codes")
                            }
                        }
                    }
                }

                Text(
                    text = "Billing Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp)
                )
                
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailItem(label = "Cardholder", value = card.holderName)
                        DetailItem(label = "Billing Day", value = "Day ${card.billingDay}")
                        val upcomingBillingDate = calculateUpcomingBillingDate(card.billingDay)
                        DetailItem(label = "Next Billing Date", value = upcomingBillingDate)
                    }
                }

                // Latest bill info
                val latestBill = history.firstOrNull()
                if (latestBill != null && !latestBill.isPaid) {
                    Text(
                        text = "Current Statement",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = if (latestBill.amount == null) 
                                MaterialTheme.colorScheme.errorContainer 
                            else 
                                MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Due Date", style = MaterialTheme.typography.labelMedium)
                                    Text(dateFormat.format(Date(latestBill.dueDate)), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                }
                                if (latestBill.amount != null) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Amount Due", style = MaterialTheme.typography.labelMedium)
                                        Text("₹${String.format("%.2f", latestBill.amount)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            if (latestBill.amount == null) {
                                Text(
                                    "Bill amount hasn't been entered yet.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { showAmountDialog = latestBill },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Enter Bill Amount")
                                }
                            } else {
                                Button(
                                    onClick = { 
                                        onUpdateBill(latestBill.copy(
                                            isPaid = true,
                                            paymentDate = System.currentTimeMillis()
                                        )) 
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Mark as Paid")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Billing History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            val displayHistory = if (showAllHistory) history else history.take(3)

            items(displayHistory) { item ->
                HistoryListItem(item)
            }

            if (history.size > 3) {
                item {
                    TextButton(
                        onClick = { showAllHistory = !showAllHistory },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (showAllHistory) "Show Less" else "Show All History")
                            Icon(
                                if (showAllHistory) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }
                    }
                }
            } else if (history.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "No history available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showAmountDialog != null) {
        var amountText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAmountDialog = null },
            title = { Text("Enter Bill Amount") },
            text = {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount") },
                    prefix = { Text("₹ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull()
                        if (amount != null) {
                            onUpdateBill(showAmountDialog!!.copy(amount = amount))
                            showAmountDialog = null
                        }
                    }
                ) {
                    Text("Save Amount")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAmountDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CreditCardGraphic(card: CreditCard, isAuthenticated: Boolean) {
    val brand = BankBranding.getBrand(card.name)
    val gradient = Brush.linearGradient(
        colors = listOf(brand.mainColor, brand.secondaryColor)
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.586f) // Standard credit card aspect ratio
            .clip(RoundedCornerShape(16.dp))
            .background(gradient)
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bank Icon / Name Placeholder
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.size(width = 44.dp, height = 30.dp)
                    ) {
                        if (brand.iconRes != null) {
                            Image(
                                painter = painterResource(id = brand.iconRes),
                                contentDescription = null,
                                modifier = Modifier.padding(6.dp)
                            )
                        } else {
                            Icon(
                                imageVector = brand.defaultIcon,
                                contentDescription = null,
                                tint = brand.mainColor,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = brand.shortName,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Network Icon
                NetworkLogo(network = card.network)
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            val displayNum = if (isAuthenticated) {
                card.number.chunked(4).joinToString("  ")
            } else {
                "****  ****  ****  ${card.number.takeLast(4)}"
            }
            
            AnimatedContent(
                targetState = displayNum,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith
                    fadeOut(animationSpec = tween(500))
                },
                label = "CardNumberAnimation"
            ) { targetNum ->
                Text(
                    text = targetNum,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("CARD HOLDER", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                    Text(card.holderName.uppercase(), color = Color.White, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("EXPIRES", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                    Text(card.validDate, color = Color.White, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun NetworkLogo(network: String) {
    val iconRes = NetworkBranding.getNetworkIcon(network)
    
    if (iconRes != null) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = network,
            modifier = Modifier.height(32.dp).widthIn(min = 40.dp)
        )
    } else {
        Surface(
            color = Color.White.copy(alpha = 0.2f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = network,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label, 
            style = MaterialTheme.typography.bodyMedium, 
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        AnimatedContent(
            targetState = value,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                fadeOut(animationSpec = tween(300))
            },
            label = "DetailValueAnimation"
        ) { targetValue ->
            Text(
                text = targetValue, 
                style = MaterialTheme.typography.bodyMedium, 
                fontWeight = FontWeight.Bold,
                fontFamily = if (label == "PIN" || label == "CVV") androidx.compose.ui.text.font.FontFamily.Monospace else null
            )
        }
    }
}

@Composable
fun HistoryListItem(item: BillHistory) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = dateFormat.format(Date(item.billingDate)),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                if (item.isPaid && item.paymentDate != null) {
                    Text(
                        text = "Paid on ${dateFormat.format(Date(item.paymentDate))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4CAF50)
                    )
                } else {
                    Text(
                        text = if (item.isPaid) "Paid" else "Unpaid",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (item.isPaid) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                    )
                }
            }
            Text(
                text = if (item.amount != null) "₹${String.format("%.2f", item.amount)}" else "N/A",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (item.isPaid) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
            )
        }
    }
}

fun calculateUpcomingBillingDate(billingDay: Int): String {
    val calendar = Calendar.getInstance()
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
    
    if (currentDay >= billingDay) {
        calendar.add(Calendar.MONTH, 1)
    }
    
    // Handle days like 31st for months with fewer days
    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    calendar.set(Calendar.DAY_OF_MONTH, billingDay.coerceAtMost(maxDay))
    
    val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    return dateFormat.format(calendar.time)
}
