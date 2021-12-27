package marcinowski.pawel.foodmanager.data_capturing

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import marcinowski.pawel.foodmanager.R
import org.json.JSONObject
import org.json.JSONTokener
import java.net.URL
import kotlin.concurrent.thread


fun lookUpProductName (barcode: String, productName: MutableState<String>, context: Context) {
    thread {
        try {
            val jsonFile =
                URL("https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json?fields=product_name").readText()
            val jsonObject = JSONTokener(jsonFile).nextValue() as JSONObject
            productName.value = jsonObject.getJSONObject("product").getString("product_name")
        } catch (e: Exception) {
            (context as Activity).runOnUiThread {
                Toast.makeText(context, context.getResources().getString(R.string.toast_product_not_found), Toast.LENGTH_SHORT).show()
            }
            e.printStackTrace()
        }
    }
}