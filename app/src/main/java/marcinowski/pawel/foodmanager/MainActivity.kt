package marcinowski.pawel.foodmanager

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.util.SparseIntArray
import android.view.TextureView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.objects.FirebaseVisionObject
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions
import marcinowski.pawel.foodmanager.ui.theme.FoodManagerTheme
import java.io.File
import java.util.*


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen(textureView, camera)
                }
            }
        }
        checkAndRequestPermissionsFor(arrayListOf(UserPermission.CAMERA, UserPermission.WRITE_DATA))

    }

    enum class UserPermission{
        CAMERA,
        WRITE_DATA
    }

    private var count = 0

    private var textureView: Reference<TextureView?> = Reference(null)

    private var camera = Camera(this, this, textureView)


    lateinit var cameraId: String
    protected var cameraDevice: CameraDevice? = null
    protected var cameraCaptureSessions: CameraCaptureSession? = null
    protected var captureRequestBuilder: CaptureRequest.Builder? = null
    public var imageDimension: Size? = null
    private var imageReader: ImageReader? = null
    private val file: File? = null
    public var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null
    private data class referenceBool (var value: Boolean)






    suspend private fun asyncDecode(frame: Bitmap, isProcessing: referenceBool) {
        //decodeImage(frame)
        isProcessing.value = false
    }




    protected fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("Camera Background")
        mBackgroundThread!!.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.looper)
    }

    protected fun stopBackgroundThread() {
        mBackgroundThread!!.quitSafely()
        try {
            mBackgroundThread!!.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "AndroidCameraApi"
        private val ORIENTATIONS = SparseIntArray()
        private const val REQUEST_CAMERA_PERMISSION = 200

        init {
            ORIENTATIONS.append(android.view.Surface.ROTATION_0, 90)
            ORIENTATIONS.append(android.view.Surface.ROTATION_90, 0)
            ORIENTATIONS.append(android.view.Surface.ROTATION_180, 270)
            ORIENTATIONS.append(android.view.Surface.ROTATION_270, 180)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(
                    this@MainActivity,
                    "Sorry!!!, you can't use this app without granting permission",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        if (textureView.value?.isAvailable == true) {
            camera.openCamera()
        } else {
            textureView.value?.surfaceTextureListener = ImageProcessing(camera, textureView).textureListener
        }
    }

    override fun onPause() {
        camera.closeCamera();
        stopBackgroundThread()
        super.onPause()
    }















    private fun decodeImage(img: Bitmap, isProcessing: referenceBool){

        val image = FirebaseVisionImage.fromBitmap(img)

        val options = FirebaseVisionObjectDetectorOptions.Builder()
            .setDetectorMode(FirebaseVisionObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()

        val detector = FirebaseVision.getInstance().getOnDeviceObjectDetector(options)

        detector.processImage(image)
            .addOnSuccessListener {
                // Task completed successfully
                Toast.makeText(baseContext, "Cos jest: " + count,
                    Toast.LENGTH_SHORT).show()
                setValuesToTextView(it)
                //detector.close()
                isProcessing.value = false
            }
            .addOnFailureListener {
                // Task failed with an exception
                Toast.makeText(baseContext, "Oops, something went wrong!",
                    Toast.LENGTH_SHORT).show()
                //detector.close()
                isProcessing.value = false
            }




    }

    private fun setValuesToTextView(visionObjects : List<FirebaseVisionObject>) {
        for ((idx, obj) in visionObjects.withIndex()) {
            val box = obj.boundingBox
            var categoryName :String = ""
            if (obj.classificationCategory != FirebaseVisionObject.CATEGORY_UNKNOWN) {
                val confidence: Int = obj.classificationConfidence!!.times(100).toInt()
                when(obj.classificationCategory)
                {
                    FirebaseVisionObject.CATEGORY_FOOD->   categoryName = "food"
                    FirebaseVisionObject.CATEGORY_PLACE->   categoryName = "place"
                    FirebaseVisionObject.CATEGORY_FASHION_GOOD->   categoryName = "fashion food"
                    FirebaseVisionObject.CATEGORY_HOME_GOOD->   categoryName = "home good"
                    FirebaseVisionObject.CATEGORY_UNKNOWN->   categoryName = "unknown"
                    FirebaseVisionObject.CATEGORY_PLANT->   categoryName = "plant"

                }
                Toast.makeText(baseContext, "Detected object: ${idx}\n" + "Category: ${obj.classificationCategory}\n"
                        + "trackingId: ${obj.trackingId}\n"
                        + "boundingBox: (${box.left}, ${box.top}) - (${box.right},${box.bottom})\n"
                        + "Confidence: ${confidence}%\n" + "Category Label is : ${categoryName}"
                    ,
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    /***
     *      _____                    _         _
     *     |  __ \                  (_)       (_)
     *     | |__) ___ _ __ _ __ ___  _ ___ ___ _  ___  _ __  ___
     *     |  ___/ _ | '__| '_ ` _ \| / __/ __| |/ _ \| '_ \/ __|
     *     | |  |  __| |  | | | | | | \__ \__ | | (_) | | | \__ \
     *     |_|   \___|_|  |_| |_| |_|_|___|___|_|\___/|_| |_|___/
     *
     *
     */

    private fun checkAndRequestPermissionsFor(items: ArrayList<UserPermission>){

        var itemsRequirePermission = ArrayList<UserPermission>()
        for (item in items){

            if (!hasPermissionFor(item)){
                itemsRequirePermission.add(item)
            }
        }
        if (!itemsRequirePermission.isEmpty()){
            requestPermissionFor(itemsRequirePermission)
        }

    }

    private fun hasPermissionFor(item: UserPermission): Boolean{

        var isPermitted = false
        when (item){

            UserPermission.CAMERA ->{

                isPermitted = this.checkSelfPermission(Manifest.permission.CAMERA) === PackageManager.PERMISSION_GRANTED

            }
            UserPermission.WRITE_DATA ->{
                isPermitted = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            }
        }
        return isPermitted
    }
    private fun requestPermissionFor(items: ArrayList<UserPermission>){

        var manisfestInfo = ArrayList<String>()
        for (item in items){

            manisfestInfo.add(getManisfestInfoFor(item))

        }
        val arrayOfPermissionItems = arrayOfNulls<String>(manisfestInfo.size)
        manisfestInfo.toArray(arrayOfPermissionItems)
        this.requestPermissions(arrayOfPermissionItems, 2)

    }

    private fun getManisfestInfoFor(item: UserPermission): String{

        var manifestString = ""
        when (item){

            UserPermission.CAMERA ->{

                manifestString = Manifest.permission.CAMERA
                //this.requestPermissions(arrayOf<String>(Manifest.permission.CAMERA), 1)

            }
            UserPermission.WRITE_DATA ->{
                manifestString = Manifest.permission.WRITE_EXTERNAL_STORAGE
                //this.requestPermissions(arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE), 2)
            }
        }
        return manifestString
    }


    private fun showAlert(message: String) {
        val dialog = android.app.AlertDialog.Builder(this)
        dialog.setTitle("Recognized Text")
        dialog.setMessage(message)
        dialog.setPositiveButton(" OK ",
            { dialog, id -> dialog.dismiss() })
        dialog.show()

    }

}