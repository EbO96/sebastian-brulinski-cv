package cv.brulinski.sebastian.utils.camera

import android.graphics.Bitmap
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import cv.brulinski.sebastian.model.Auth
import cv.brulinski.sebastian.utils.log


class QrCodeScanner {

    private var options = FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(
                    FirebaseVisionBarcode.FORMAT_QR_CODE,
                    FirebaseVisionBarcode.FORMAT_AZTEC)
            .build()


    fun recognizeCredentials(bitmap: Bitmap?, auth: (Auth?) -> Unit) {
        if (bitmap != null) {
            val image = FirebaseVisionImage.fromBitmap(bitmap)

            val detector = FirebaseVision.getInstance()
                    .getVisionBarcodeDetector(options)

            detector.detectInImage(image)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (task.result.isNotEmpty()) {
                                val value = task.result[0].displayValue
                                "qr".log("value: $value")
                            }
                        } else {
                            auth(null)
                        }
                    }

        }
    }
}