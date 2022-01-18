package marcinowski.pawel.foodmanager.storage

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import marcinowski.pawel.foodmanager.BarcodeEntry
import marcinowski.pawel.foodmanager.BarcodeStorage
import marcinowski.pawel.foodmanager.utils.Barcode
import java.io.InputStream
import java.io.OutputStream

object SerializerBarcodeStorage : Serializer<BarcodeStorage> {
    override val defaultValue: BarcodeStorage = BarcodeStorage.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): BarcodeStorage {
        try {
            return BarcodeStorage.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }
    override suspend fun writeTo(t: BarcodeStorage, output: OutputStream) = t.writeTo(output)
}


/**
 * Class responsible for storing and retrieving barcodes
 *
 */
class Barcodes(private val context: Context) {

    companion object {
        private val Context.barcodeProtoDataStore: DataStore<BarcodeStorage> by dataStore(
            fileName = "barcodes.pb",
            serializer = SerializerBarcodeStorage
        )
    }

    /**
     * Retrieve barcodes list
     *
     */
    fun getBarcodes(): Flow<List<Barcode>> {
        return context.barcodeProtoDataStore.data.map { barcodes ->
            val barcodeList: MutableList<Barcode> = mutableListOf()
            barcodes.entriesList.forEach {
                barcodeList.add(
                    Barcode(
                        it.productName,
                        it.barcodeNumber
                    )
                )
            }
            barcodeList
        }
    }

    /**
     * Save barcode entry
     *
     */
    suspend fun saveBarcode(name: String, number: String) {
        context.barcodeProtoDataStore.updateData { barcodeStorage ->
            barcodeStorage.toBuilder()
                .addEntries(
                    BarcodeEntry.newBuilder()
                        .setProductName(name)
                        .setBarcodeNumber(number)
                        .build()
                )
                .build()
        }
    }

    /**
     * Update barcode entry
     *
     */
    suspend fun updateBarcode(name: String, number: String) {
        context.barcodeProtoDataStore.updateData { barcodeStorage ->
            val foundBarcode = barcodeStorage.entriesList.find { it.barcodeNumber == number }
            barcodeStorage.toBuilder().setEntries(
                barcodeStorage.entriesList.indexOf(foundBarcode),
                foundBarcode
                    ?.toBuilder()
                    ?.setProductName(name)
                    ?.build()
            ).build()
        }
    }

    /**
     * Clear all barcode entries
     *
     */
    suspend fun clearBarcodes() {
        context.barcodeProtoDataStore.updateData { data -> data.toBuilder().clearEntries().build() }
    }
}