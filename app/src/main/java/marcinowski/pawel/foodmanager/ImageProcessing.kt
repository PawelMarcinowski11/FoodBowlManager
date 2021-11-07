package marcinowski.pawel.foodmanager

import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.view.TextureView
import kotlinx.coroutines.sync.Semaphore
import kotlin.concurrent.thread

class ImageProcessing(private var camera: Camera,
                      private var textureViewRef: Reference<TextureView?>) {

    private var isProcessing : Reference<Boolean> = Reference(false)
    private var isProcessingNew : Semaphore = Semaphore(1)

    var textureListener: TextureView.SurfaceTextureListener = object :
        TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            //open your camera here
            camera.openCamera()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
            // Transform you image captured size according to the surface width and height
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {
            // Start changes
            // Get the bitmap



            // Do whatever you like with the frame
            //frameProcessor?.processFrame(frame)


            //async { isProcessingNew.withPermit {  }}

            if(isProcessing.value == false /* ||isProcessingNew.tryAcquire()*/) {
                isProcessing.value = true
                thread {
                    val frame = Bitmap.createBitmap(textureViewRef.value!!.width, textureViewRef.value!!.height, Bitmap.Config.ARGB_8888)
                    textureViewRef.value!!.getBitmap(frame)
                    //decodeImage(frame,isProcessing)
                    //isProcessingNew.release()
                }


                //val promise = launch(CommonPool1) {
                //    asyncDecode(frame,isProcessing)
                //}
            }
            // End changes
        }
    }
}