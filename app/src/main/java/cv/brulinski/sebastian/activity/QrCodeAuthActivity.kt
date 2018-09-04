package cv.brulinski.sebastian.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.model.Auth
import cv.brulinski.sebastian.utils.camera.OnAuthDecoded
import cv.brulinski.sebastian.utils.camera.QrCodeScanner
import kotlinx.android.synthetic.main.activity_qr_code_login.*

class QrCodeAuthActivity : AppCompatActivity(), OnAuthDecoded,
        SurfaceHolder.Callback {

    companion object {
        const val AUTH = "auth"
    }

    //My qr code scanner class
    private val qrCodeScanner by lazy {
        QrCodeScanner(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_login)
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        qrCodeScanner.getCameraSource()?.stop()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        qrCodeScanner.getCameraSource()?.start(surfaceView.holder)
    }

    override fun authDecoded(auth: Auth) {
        setResult(SplashActivity.QR_CODE_AUTH, Intent().apply {
            putExtra(AUTH, auth)
        })
        finish()
    }

    override fun onResume() {
        super.onResume()
        surfaceView.holder.addCallback(this)
    }

    override fun onPause() {
        qrCodeScanner.getCameraSource()?.stop()
        surfaceView.holder.removeCallback(this)
        super.onPause()
    }
}
