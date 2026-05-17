package com.example.creditcardtracker.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.creditcardtracker.R

data class BankBrand(
    val mainColor: Color,
    val secondaryColor: Color,
    val iconRes: Int?,
    val defaultIcon: ImageVector,
    val shortName: String
)

object BankBranding {
    private val DEFAULT_BRAND = BankBrand(
        mainColor = Color(0xFF1A237E), // Deep Indigo
        secondaryColor = Color(0xFF0D47A1), // Dark Blue
        iconRes = null,
        defaultIcon = Icons.Default.AccountBalance,
        shortName = "BANK"
    )

    fun getBrand(cardName: String): BankBrand {
        val name = cardName.uppercase()
        return when {
            name.contains("HDFC") -> BankBrand(
                mainColor = Color(0xFF004C8F), // HDFC Blue
                secondaryColor = Color(0xFF003366),
                iconRes = R.drawable.ic_hdfc_logo,
                defaultIcon = Icons.Default.AccountBalance,
                shortName = "HDFC"
            )
            name.contains("AXIS") -> BankBrand(
                mainColor = Color(0xFF97144D), // Axis Maroon
                secondaryColor = Color(0xFF7A0E3C),
                iconRes = R.drawable.ic_axis_logo,
                defaultIcon = Icons.Default.Payments,
                shortName = "AXIS"
            )
            name.contains("ICICI") -> BankBrand(
                mainColor = Color(0xFFF37021), // ICICI Orange
                secondaryColor = Color(0xFFB95210),
                iconRes = R.drawable.ic_icici_logo,
                defaultIcon = Icons.Default.AccountBalance,
                shortName = "ICICI"
            )
            name.contains("SBI") -> BankBrand(
                mainColor = Color(0xFF2196F3), // SBI Blue
                secondaryColor = Color(0xFF0079C1),
                iconRes = R.drawable.ic_sbi_logo,
                defaultIcon = Icons.Default.AccountBalance,
                shortName = "SBI"
            )
            name.contains("AMEX") || name.contains("AMERICAN EXPRESS") -> BankBrand(
                mainColor = Color(0xFF0070CE), // Amex Blue
                secondaryColor = Color(0xFF005AA3),
                iconRes = null,
                defaultIcon = Icons.Default.Star,
                shortName = "AMEX"
            )
            name.contains("HSBC") -> BankBrand(
                mainColor = Color(0xFFDB0011), // HSBC Red
                secondaryColor = Color(0xFFBC0010),
                iconRes = R.drawable.ic_hsbc_logo,
                defaultIcon = Icons.Default.AccountBalance,
                shortName = "HSBC"
            )
            name.contains("CITI") -> BankBrand(
                mainColor = Color(0xFF003B70), // Citi Blue
                secondaryColor = Color(0xFFED1C24), // Citi Red
                iconRes = null,
                defaultIcon = Icons.Default.Public,
                shortName = "CITI"
            )
            name.contains("KOTAK") -> BankBrand(
                mainColor = Color(0xFFED1C24), // Kotak Red
                secondaryColor = Color(0xFF003B70),
                iconRes = null,
                defaultIcon = Icons.Default.AccountBalance,
                shortName = "KOTAK"
            )
            else -> DEFAULT_BRAND.copy(shortName = cardName.split(" ").firstOrNull()?.uppercase() ?: "BANK")
        }
    }
}
