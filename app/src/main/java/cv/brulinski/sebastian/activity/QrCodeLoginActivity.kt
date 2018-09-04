package cv.brulinski.sebastian.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wonderkiln.camerakit.*
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.utils.camera.QrCodeScanner
import cv.brulinski.sebastian.utils.delay
import kotlinx.android.synthetic.main.activity_qr_code_login.*


class QrCodeLoginActivity : AppCompatActivity() {

    //My qr code scanner class
    private val qrCodeScanner by lazy {
        QrCodeScanner()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_login)

        cameraView.addCameraKitListener(object : CameraKitEventListener {
            override fun onVideo(p0: CameraKitVideo?) {
            }

            override fun onEvent(p0: CameraKitEvent?) {
                p0
            }

            override fun onImage(cameraKitImage: CameraKitImage?) {
                qrCodeScanner.recognizeCredentials(cameraKitImage?.bitmap) {

                }
                cameraView.stop()
            }

            override fun onError(p0: CameraKitError?) {
                p0
            }

        })

    }

    override fun onResume() {
        super.onResume()
        cameraView.start()
    }

    override fun onPause() {
        cameraView.stop()
        super.onPause()
    }

}
