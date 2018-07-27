package cv.brulinski.sebastian.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import cv.brulinski.sebastian.utils.putPrefsValue

class SplashActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }


    override fun onStart() {
        super.onStart()
        auth.currentUser?.let { startMain() } ?: run {
            auth.signInAnonymously()
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            startMain()
                        }
                    }
        }
    }

    private fun startMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}