package marcinowski.pawel.foodmanager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.SparseIntArray
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import marcinowski.pawel.foodmanager.data_capturing.Camera
import marcinowski.pawel.foodmanager.notifications.FoodNotifications
import marcinowski.pawel.foodmanager.screens.MainScreen
import marcinowski.pawel.foodmanager.ui.theme.FoodManagerTheme
import java.util.*

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPagerApi::class)
    var pagerState: PagerState? = null

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val useNotifications = this.dataStore.data
            .map { settings ->
                settings[booleanPreferencesKey("useNotifications")] ?: true
            }
        CoroutineScope(Dispatchers.IO).launch {
            if (useNotifications.first())
                FoodNotifications().setNotifications(this@MainActivity, 7)
        }
        setContent {
            val darkTheme = remember { mutableStateOf(false) }
            FoodManagerTheme (darkTheme.value) {
                pagerState = rememberPagerState(initialPage = 1)
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen(camera, pagerState!!, darkTheme)
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
    var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null

    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("Camera Background")
        mBackgroundThread!!.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.looper)
    }

    private fun stopBackgroundThread() {
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
        private val ORIENTATIONS = SparseIntArray()
        private const val REQUEST_CAMERA_PERMISSION = 200
        init {
            ORIENTATIONS.append(android.view.Surface.ROTATION_0, 90)
            ORIENTATIONS.append(android.view.Surface.ROTATION_90, 0)
            ORIENTATIONS.append(android.view.Surface.ROTATION_180, 270)
            ORIENTATIONS.append(android.view.Surface.ROTATION_270, 180)
        }
    }

    @ExperimentalPagerApi
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pagerState?.currentPage == 0) {
                    camera.openCamera()
                }
            }
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    override fun onResume() {
        super.onResume()
        startBackgroundThread()
    }

    @OptIn(ExperimentalPagerApi::class)
    override fun onRestart() {
        super.onRestart()
        if (pagerState?.currentPage == 0) {
            camera.openCamera()
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    override fun onPause() {
        if (pagerState?.currentPage == 0) {
            camera.closeCamera()
        }
        stopBackgroundThread()
        super.onPause()
    }

    private fun checkAndRequestPermissionsFor(items: ArrayList<UserPermission>){
        val itemsRequirePermission = ArrayList<UserPermission>()
        for (item in items){
            if (!hasPermissionFor(item)){
                itemsRequirePermission.add(item)
            }
        }
        if (itemsRequirePermission.isNotEmpty()){
            requestPermissionFor(itemsRequirePermission)
        }
    }

    private fun hasPermissionFor(item: UserPermission): Boolean {
        return when (item) {
            UserPermission.CAMERA -> {
                checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            }
            UserPermission.WRITE_DATA -> {
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    private fun requestPermissionFor(items: ArrayList<UserPermission>){
        val manifestInfo = ArrayList<String>()
        for (item in items) {
            manifestInfo.add(getManifestInfoFor(item))
        }
        val arrayOfPermissionItems = arrayOfNulls<String>(manifestInfo.size)
        manifestInfo.toArray(arrayOfPermissionItems)
        this.requestPermissions(arrayOfPermissionItems, 2)
    }

    private fun getManifestInfoFor(item: UserPermission): String {
        return when (item) {
            UserPermission.CAMERA -> {
                Manifest.permission.CAMERA
            }
            UserPermission.WRITE_DATA -> {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            }
        }
    }
}