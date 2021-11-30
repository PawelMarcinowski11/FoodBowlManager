package marcinowski.pawel.foodmanager

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.util.*

data class ProductParameters (
    var productName: MutableState<String> = mutableStateOf(""),
    var barcodeNumber: MutableState<String> = mutableStateOf(""),
    var expiryDate: MutableState<String> = mutableStateOf(""),
    var autoComplete: MutableState<Boolean> = mutableStateOf(true)
)

data class ProductEntry(
    val name: String,
    val id: Number,
    val expiryDate: Date? = null
)

