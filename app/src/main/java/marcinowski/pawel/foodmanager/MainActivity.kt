package marcinowski.pawel.foodmanager

import android.Manifest
import android.content.pm.PackageManager
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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import marcinowski.pawel.foodmanager.ui.theme.FoodManagerTheme
import java.io.File
import java.util.*

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPagerApi::class)
    var pagerState: PagerState? = null

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodManagerTheme {
                pagerState = rememberPagerState(initialPage = 1)

                Surface(color = MaterialTheme.colors.background) {
                    MainScreen(textureView, camera, pagerState!!)
                }

            }
        }
        checkAndRequestPermissionsFor(arrayListOf(UserPermission.CAMERA, UserPermission.WRITE_DATA))

    }

    enum class UserPermission{
        CAMERA,
        WRITE_DATA
    }

    private val camera = Camera(this, this)

    private var count = 0

    private var textureView: Reference<TextureView?> = Reference(null)

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

    @OptIn(ExperimentalPagerApi::class)
    override fun onResume() {
        super.onResume()
        startBackgroundThread()

        if (pagerState?.currentPage == 0) {
            //if (textureView.value?.isAvailable == true)
                camera.openCamera()
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    override fun onPause() {
        if (pagerState?.currentPage == 0) {
            camera.closeCamera();
        }
        stopBackgroundThread()
        super.onPause()
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