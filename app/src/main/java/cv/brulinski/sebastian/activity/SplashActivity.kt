package cv.brulinski.sebastian.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import cv.brulinski.sebastian.dependency_injection.app.App
import cv.brulinski.sebastian.fragment.LoginFragment
import cv.brulinski.sebastian.interfaces.SplashScreenCallback
import cv.brulinski.sebastian.utils.set

/**
 * This is Splash screen. This screen decides where to go. MainActivity or LoginFragment
 * when user is not authorized yet
 */
class SplashActivity : AppCompatActivity(), SplashScreenCallback {

    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onStart() {
        super.onStart()
        //Check for already logged anonymous user
        auth.currentUser?.let {
            //User are already logged
            startMain()
        } ?: run { startLogin() }
    }

    private fun startMain() {
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener {
            if (it.isSuccessful) {
                App.token = it.result.token
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else finish()
        }
    }

    private fun startLogin() {
        LoginFragment().set(supportFragmentManager, android.R.id.content, addToStack = false)
    }

    override fun loginSuccessful() {
        startMain()
    }
}