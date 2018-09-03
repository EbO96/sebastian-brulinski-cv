package cv.brulinski.sebastian.utils

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions

/**
 * Class used to read data from QR codes
 */
class MyBarCodeDetector {

    /**
     * Options for QR code scanner
     */
    private val options by lazy {
        FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(
                        FirebaseVisionBarcode.FORMAT_QR_CODE,
                        FirebaseVisionBarcode.FORMAT_AZTEC
                )
                .build()
    }


}