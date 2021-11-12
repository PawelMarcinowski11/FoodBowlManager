package marcinowski.pawel.foodmanager

import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.view.TextureView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.sync.Semaphore
import kotlin.concurrent.thread

class ImageProcessing(
    private var textureViewRef: TextureView?,
    productParameters: ProductParameters
) {


    private var isProcessing : Semaphore

    var textureListener: TextureView.SurfaceTextureListener

    init {
        this.isProcessing = Semaphore(1)
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
                if (isProcessing.tryAcquire()) {
                    thread {
                        if (textureViewRef != null) {
                            val frame = Bitmap.createBitmap(
                                textureViewRef!!.width,
                                textureViewRef!!.height,
                                Bitmap.Config.ARGB_8888
                            )

                            val bitmap = textureViewRef!!.getBitmap(frame)

//                            val options = BarcodeScannerOptions.Builder()
//                                .setBarcodeFormats(
//                                    Barcode.FORMAT_QR_CODE,
//                                    Barcode.FORMAT_AZTEC)
//                                .build()

                            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                            val result = recognizer.process(InputImage.fromBitmap(bitmap, 0))
                                .addOnSuccessListener { visionText ->
                                    //productParameters.productName.value = visionText.text

                                    var datePattern = Regex("""[0-3]\d[.][0-1]\d[.][2][0]\d\d""")
                                    var trimmedResult = datePattern.find(visionText.text)?.value

                                    if(trimmedResult != null)
                                        productParameters.productName.value = trimmedResult


                                    //var xd2 = visionText.textBlocks[0]
                                    // Task completed successfully
                                    // ...
                                    isProcessing.release()
                                }
                                .addOnFailureListener { e ->
                                    // Task failed with an exception
                                    // ...
                                    isProcessing.release()
                                }

                        }
                    }
                }
            }
        }
    }
}