package marcinowski.pawel.foodmanager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.view.TextureView
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlin.concurrent.thread

class ImageProcessing(
    private var textureViewRef: TextureView?,
    productParameters: ProductParameters,
    context: Context
) {


    private var isProcessingText : Semaphore = Semaphore(1)
    private var isProcessingBarcode : Semaphore = Semaphore(1)

    var textureListener: TextureView.SurfaceTextureListener

    init {
        this.textureListener = object :
            TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {

            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return true
            }

            override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {
                if (productParameters.autoComplete.value == true
                    && (isProcessingText.availablePermits > 0
                            || isProcessingBarcode.availablePermits > 0)) {

                    if (textureViewRef != null) {
                        val frame = Bitmap.createBitmap(
                            textureViewRef!!.width,
                            textureViewRef!!.height,
                            Bitmap.Config.ARGB_8888
                        )

                        val bitmap = textureViewRef!!.getBitmap(frame)

                        val inputImage = InputImage.fromBitmap(bitmap, 0)

                        if (isProcessingText.tryAcquire()) {
                            thread {

                                val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                                textRecognizer.process(inputImage)
                                    .addOnSuccessListener { visionText ->
                                        val ddmmyyyyPattern = """[0-3]\d[.,/](([0]\d)|([1][0-2]))[.,/][2][0]\d\d"""
                                        val ddmmyyPattern = """[0-3]\d[.,/](([0]\d)|([1][0-2]))[.,/]\d\d"""
                                        val mmyyyyPattern = """^(([0]\d)|([1][0-2]))[.,/][2][0]\d\d"""
                                        val mmyyPattern = """^(([0]\d)|([1][0-2]))[.,/]\d\d"""

                                        if (Regex(ddmmyyyyPattern).find(visionText.text)?.value != null) {
                                            val expiryDate = Regex(ddmmyyyyPattern).find(visionText.text)?.value!!
                                            productParameters.expiryDate.value = expiryDate.replace('/', '.')
                                        }
                                        else if (Regex(ddmmyyPattern).find(visionText.text)?.value != null) {
                                            val expiryDate = Regex(ddmmyyPattern).find(visionText.text)?.value!!
                                            productParameters.expiryDate.value = expiryDate.substring(0,6).replace('/', '.') + "20" + expiryDate.substring(6).replace('/', '.')
                                        }
                                        else if (Regex(mmyyyyPattern).find(visionText.text)?.value != null) {
                                            val expiryDate = Regex(mmyyyyPattern).find(visionText.text)?.value!!
                                            if (expiryDate.contains('.'))
                                                productParameters.expiryDate.value = "01." + expiryDate
                                            else if (expiryDate.contains('/'))
                                                productParameters.expiryDate.value = "01." + expiryDate.replace('/', '.')
                                        }
                                        else if (Regex(mmyyPattern).find(visionText.text)?.value != null) {
                                            val expiryDate = Regex(mmyyPattern).find(visionText.text)?.value!!
                                            if (expiryDate.contains('.'))
                                                productParameters.expiryDate.value = "01." + expiryDate.substring(0,3) + "20" + expiryDate.substring(3)
                                            else if (expiryDate.contains('/'))
                                                productParameters.expiryDate.value = "01." + expiryDate.substring(0,3).replace('/', '.') + "20" + expiryDate.substring(3).replace('/', '.')

                                        }

                                        isProcessingText.release()
                                    }
                                    .addOnFailureListener {
                                        isProcessingText.release()
                                    }


                            }
                        }

                        if (isProcessingBarcode.tryAcquire()) {
                            thread {


                                val options = BarcodeScannerOptions.Builder()
                                    .setBarcodeFormats(
                                        Barcode.FORMAT_EAN_13,
                                        Barcode.FORMAT_EAN_8,
                                        Barcode.FORMAT_UPC_A,
                                        Barcode.FORMAT_UPC_E,
                                    )
                                    .build()

                                val barcodeRecognizer = BarcodeScanning.getClient(options)
                                barcodeRecognizer.process(inputImage)
                                    .addOnSuccessListener { barcodes ->

                                        if(barcodes.isNotEmpty() && productParameters.barcodeNumber.value !=
                                                barcodes[0].displayValue!!) {
                                            productParameters.barcodeNumber.value =
                                                barcodes[0].displayValue!!

                                            CoroutineScope(Dispatchers.IO).launch {
                                                val barcodesList = Barcodes(context).getBarcodes().firstOrNull()
                                                val existingBarcode = barcodesList?.find {it.number == barcodes[0].displayValue!!}
                                                if (existingBarcode != null) {
                                                    productParameters.productName.value = existingBarcode.name
                                                }
                                                else {
                                                    lookUpProductName(
                                                        productParameters.barcodeNumber.value,
                                                        productParameters.productName,
                                                        context
                                                    );
                                                }
                                            }


                                        }


                                        isProcessingBarcode.release()
                                    }
                                    .addOnFailureListener { e ->
                                        // Task failed with an exception
                                        // ...
                                        isProcessingBarcode.release()
                                    }

                            }
                        }
                    }


                }
            }
        }
    }
}