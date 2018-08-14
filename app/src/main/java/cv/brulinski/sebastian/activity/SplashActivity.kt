package cv.brulinski.sebastian.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import cv.brulinski.sebastian.utils.settings

class SplashActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }


    override fun onStart() {
        super.onStart()
        //Check for already logged anonymous user
        auth.currentUser?.let {
            //User are already logged
            startMain()
        } ?: run {
            //Login user as anonymous
            auth.signInAnonymously()
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            settings.firstLaunch = true
                            startMain()
                        } else {
                            //TODO information about login failed
                        }
                    }
        }
    }

    private fun startMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}