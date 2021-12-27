package marcinowski.pawel.foodmanager.data_capturing

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.media.ImageReader
import android.os.Looper
import android.util.Size
import android.view.TextureView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.util.concurrent.HandlerExecutor
import marcinowski.pawel.foodmanager.MainActivity

class Camera(
    private var activity: MainActivity,
    private var context: Context
) {

    private val REQUEST_CAMERA_PERMISSION = 200

    private lateinit var cameraId: String
    private var cameraDevice: CameraDevice? = null
    private var cameraCaptureSessions: CameraCaptureSession? = null
    private var captureRequestBuilder: CaptureRequest.Builder? = null
    private var imageDimension: Size? = null
    private var imageReader: ImageReader? = null

    var textureViewRef: TextureView? = null

    fun openCamera() {
        val manager = context.getSystemService(ComponentActivity.CAMERA_SERVICE) as CameraManager

        try {
            cameraId = manager.cameraIdList[0]
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val resolutions =
                characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            for (resolution in resolutions.getOutputSizes(SurfaceTexture::class.java)) {
                if (resolution.height * 16 == resolution.width * 9 && resolution.height * resolution.width < 2500000) {
                    imageDimension = resolution
                    break
                }
            }

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
                return
            }
            manager.openCamera(cameraId, stateCallback, null)

        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        textureViewRef?.alpha = 1.0f
    }

    fun closeCamera() {
        textureViewRef?.alpha = 0f
        if (null != cameraDevice) {
            cameraDevice!!.close()
            cameraDevice = null
        }
        if (null != imageReader) {
            imageReader!!.close()
            imageReader = null
        }
    }

    private val stateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            createCameraPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice?.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraDevice?.close()
            cameraDevice = null
        }
    }

    private fun createCameraPreview() {
        try {
            val texture = textureViewRef?.surfaceTexture!!
            texture.setDefaultBufferSize(imageDimension!!.width, imageDimension!!.height)
            val surface = android.view.Surface(texture)
            captureRequestBuilder =
                cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
            captureRequestBuilder!!.addTarget(surface)

            cameraDevice!!.createCaptureSession(
                SessionConfiguration(
                    SessionConfiguration.SESSION_REGULAR,
                    listOf(OutputConfiguration(surface)),
                    HandlerExecutor(Looper.getMainLooper()),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                            if (null == cameraDevice) {
                                return
                            }
                            cameraCaptureSessions = cameraCaptureSession
                            updatePreview()
                        }
                        override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                        }
                    }
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updatePreview() {
        captureRequestBuilder!!.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        try {
            cameraCaptureSessions!!.setRepeatingRequest(
                captureRequestBuilder!!.build(),
                null,
                activity.mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }
}