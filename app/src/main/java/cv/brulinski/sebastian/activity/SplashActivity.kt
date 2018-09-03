package cv.brulinski.sebastian.activity

import addToAndroidContainer
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.dependency_injection.app.App
import cv.brulinski.sebastian.fragment.EmailPasswordLogin
import cv.brulinski.sebastian.fragment.LoginFragment
import cv.brulinski.sebastian.fragment.QrCodeLogin
import cv.brulinski.sebastian.utils.*
import cv.brulinski.sebastian.view.LargeSnackbar
import inflate
import removeFromAndroidContainer

/**
 * This is Splash screen. This screen decides where to go. MainActivity or LoginFragment
 * when user is not authorized yet
 */
class SplashActivity : AppCompatActivity(),
        EmailPasswordLogin,
        QrCodeLogin {

    private val auth by lazy { FirebaseAuth.getInstance() }
    //Screen displayed during getting token
    private var getTokenScreen: View? = null
    //Login screen
    private var loginFragment: LoginFragment? = null
    //Camera permission request code
    private val CAMERA_REQUEST_CODE = 0

    /*
    Public methods
     */


    /*
    Private methods
     */

    private fun getToken() {
        if (App.token == null) {
            getTokenScreen = R.layout.authorization_screen.inflate()
            addToAndroidContainer(getTokenScreen)
            FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    App.token = it.result.token
                    startMain()
                } else {
                    getTokenScreen?.findViewById<ProgressBar>(R.id.progress)?.visibility = View.GONE
                    getTokenScreen?.findViewById<TextView>(R.id.message)?.text = R.string.cant_authorize.string()
                    1500L.delay {
                        if (loginFragment == null)
                            startLogin()
                        else removeFromAndroidContainer(getTokenScreen)
                    }
                }
            }
        } else startMain()
    }

    private fun startMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun startLogin() {
        if (loginFragment == null) {
            loginFragment = LoginFragment()
            loginFragment?.set(supportFragmentManager, android.R.id.content, addToStack = false)
        }
    }

    private fun requestForCameraPermission() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE)
    }

    private fun startQrCodeLogin() {
        startActivity(Intent(this, QrCodeLoginActivity::class.java))
    }

    private fun makeCameraExplanation() {
        LargeSnackbar.getInstance().apply {
            loginFragment?.apply {
                getRootView()?.also { rootView ->
                    snackbarAnchorView()?.also { snackbarAnchorView ->
                        show(rootView, snackbarAnchorView,
                                R.string.warning.string(),
                                R.string.explanation_camera.string(),
                                LargeSnackbar.Duration.LONG,
                                R.string.settings.string(), 8) {
                            goToAppSettings()
                        }
                    }
                }
            }
        }
    }

    /*
    Override methods
     */

    override fun onStart() {
        super.onStart()
        //Check for already logged anonymous user
        auth.currentUser?.let {
            //User are already logged
            getToken()
        } ?: run { startLogin() }
    }

    override fun emailPasswordSuccessful() {
        getToken()
    }

    override fun tryQrLogin() {
        requestForCameraPermission()
    }

    override fun qrCodeSignedIn() {
        getToken()
    }

    override fun requestForCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            //Permission is not granted
            requestForCameraPermission()
        } else {
            //Permission granted
            startQrCodeLogin()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startQrCodeLogin()
                } else {
                    if (!shouldRequestRationale(Manifest.permission.CAMERA))
                        makeCameraExplanation()
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }
}