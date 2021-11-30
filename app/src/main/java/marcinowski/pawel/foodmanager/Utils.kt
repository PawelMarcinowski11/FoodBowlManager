package marcinowski.pawel.foodmanager

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.time.LocalDate

data class ProductParameters (
    val productName: MutableState<String> = mutableStateOf(""),
    val barcodeNumber: MutableState<String> = mutableStateOf(""),
    val expiryDate: MutableState<String> = mutableStateOf(""),
    val autoComplete: MutableState<Boolean> = mutableStateOf(true),
    val id: MutableState<Int> = mutableStateOf(-1)
)

data class ProductEntry(
    var name: String,
    val id: Int,
    var expiryDate: LocalDate? = null
) {
    fun update(newName: String) {
        name = newName
        //TODO(Implement changing entry in local storage)
    }
}

