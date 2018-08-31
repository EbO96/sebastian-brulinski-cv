package cv.brulinski.sebastian.activity

import addToAndroidContainer
import android.content.Intent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.dependency_injection.app.App
import cv.brulinski.sebastian.fragment.LoginFragment
import cv.brulinski.sebastian.interfaces.SplashScreenCallback
import cv.brulinski.sebastian.utils.delay
import cv.brulinski.sebastian.utils.set
import cv.brulinski.sebastian.utils.string
import inflate
import removeFromAndroidContainer

/**
 * This is Splash screen. This screen decides where to go. MainActivity or LoginFragment
 * when user is not authorized yet
 */
class SplashActivity : AppCompatActivity(), SplashScreenCallback {

    private val auth by lazy { FirebaseAuth.getInstance() }
    //Screen displayed during getting token
    private var getTokenScreen: View? = null
    //Login screen
    private var loginFragment: LoginFragment? = null

    override fun onStart() {
        super.onStart()
        //Check for already logged anonymous user
        auth.currentUser?.let {
            //User are already logged
            getToken()
        } ?: run { startLogin() }
    }

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
        if (loginFragment == null)
            loginFragment = LoginFragment()
        loginFragment?.set(supportFragmentManager, android.R.id.content, addToStack = false)
    }

    override fun loginSuccessful() {
        getToken()
    }
}