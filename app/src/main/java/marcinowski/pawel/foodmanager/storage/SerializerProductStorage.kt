package marcinowski.pawel.foodmanager.storage

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import marcinowski.pawel.foodmanager.ProductEntry
import marcinowski.pawel.foodmanager.ProductStorage
import marcinowski.pawel.foodmanager.utils.Product
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDate
import java.util.*

object SerializerProductStorage : Serializer<ProductStorage> {
    override val defaultValue: ProductStorage = ProductStorage.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): ProductStorage {
        try {
            return ProductStorage.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }
    override suspend fun writeTo(t: ProductStorage, output: OutputStream) = t.writeTo(output)
}


/**
 * Class responsible for storing and retrieving products
 *
 */
class Products(private val context: Context) {
    companion object {
        private val Context.productProtoDataStore: DataStore<ProductStorage> by dataStore(
            fileName = "products.pb",
            serializer = SerializerProductStorage
        )
    }

    /**
     * Retrieve product list
     *
     */
    fun getProducts(): Flow<List<Product>> {
        return context.productProtoDataStore.data.map { products ->
            val productList: MutableList<Product> = mutableListOf()
            products.entriesList.forEach {
                productList.add(
                    Product(
                        it.productName,
                        it.barcodeNumber,
                        UUID.fromString(it.id),
                        LocalDate.ofEpochDay(it.expiryDate)
                    )
                )
            }
            productList.sortBy { product -> product.expiryDate }
            productList
        }
    }

    /**
     * Save product entry
     *
     */
    suspend fun saveProduct(name: String, number: String, date: LocalDate) {
        context.productProtoDataStore.updateData { productsStorage ->
            productsStorage.toBuilder()
                .addEntries(
                    ProductEntry.newBuilder()
                        .setProductName(name)
                        .setBarcodeNumber(number)
                        .setId(UUID.randomUUID().toString())
                        .setExpiryDate(date.toEpochDay())
                        .build()
                )
                .build()
        }
    }

    /**
     * Update product entry
     *
     */
    suspend fun updateProduct(name: String, number: String, id: UUID, date: LocalDate) {
        context.productProtoDataStore.updateData { productsStorage ->
            val foundProduct = productsStorage.entriesList.find { it.id == id.toString() }
            productsStorage.toBuilder().setEntries(
                productsStorage.entriesList.indexOf(foundProduct),
                foundProduct
                    ?.toBuilder()
                    ?.setProductName(name)
                    ?.setBarcodeNumber(number)
                    ?.setExpiryDate(date.toEpochDay())
                    ?.build()
            ).build()
        }
    }

    /**
     * Remove product entry
     *
     */
    suspend fun removeProduct(id: UUID) {
        context.productProtoDataStore.updateData { productsStorage ->
            val foundProduct = productsStorage.entriesList.find { it.id == id.toString() }
            if (foundProduct != null)
                productsStorage
                    .toBuilder()
                    .removeEntries(productsStorage.entriesList.indexOf(foundProduct))
                    .build()
            else
                productsStorage
        }
    }

    /**
     * Clear all product entries
     *
     */
    suspend fun clearProducts() {
        context.productProtoDataStore.updateData { data -> data.toBuilder().clearEntries().build() }
    }
}