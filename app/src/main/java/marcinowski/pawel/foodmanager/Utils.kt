package marcinowski.pawel.foodmanager

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class ProductParameters (
    var productName: MutableState<String> = mutableStateOf(""),
    var barcodeNumber: MutableState<String> = mutableStateOf(""),
    var expiryDate: MutableState<String> = mutableStateOf(""),
    var autoComplete: MutableState<Boolean> = mutableStateOf(true)
)
