package com.example.creditcardtracker.utils

import com.example.creditcardtracker.R

object NetworkBranding {
    fun getNetworkIcon(network: String): Int? {
        return when (network.lowercase()) {
            "visa" -> R.drawable.ic_visa
            "mastercard" -> R.drawable.ic_mastercard
            "rupay" -> R.drawable.ic_rupay
            "american express" -> R.drawable.ic_amex
            "diners club" -> R.drawable.ic_diners_logo
            else -> null
        }
    }
}