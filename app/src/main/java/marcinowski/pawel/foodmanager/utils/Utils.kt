package marcinowski.pawel.foodmanager.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.time.LocalDate
import java.util.*

/**
 * Product parameters for scanning
 *
 */
data class ProductParameters (
    val productName: MutableState<String> = mutableStateOf(""),
    val barcodeNumber: MutableState<String> = mutableStateOf(""),
    val expiryDate: MutableState<String> = mutableStateOf(""),
    val autoComplete: MutableState<Boolean> = mutableStateOf(true),
    val id: MutableState<UUID> = mutableStateOf(UUID.randomUUID())
)

/**
 * Product entry for storage purposes
 *
 */
data class Product(
    val name: String,
    val barcodeNumber: String,
    val id: UUID,
    val expiryDate: LocalDate? = null
)

/**
 * Barcode entry for storage purposes
 *
 */
data class Barcode(
    var name: String,
    val number: String
)