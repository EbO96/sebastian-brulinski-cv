package cv.brulinski.sebastian.utils.camera

import android.util.SparseArray
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import cv.brulinski.sebastian.model.Auth
import cv.brulinski.sebastian.utils.ctx


class QrCodeScanner(private val onAuthDecoded: OnAuthDecoded? = null) {

    private var cameraSource: CameraSource? = null
    private var barcodeDetector: BarcodeDetector? = null

    private fun getBarcodeDetector(): BarcodeDetector? {
        if (barcodeDetector == null)
            barcodeDetector = BarcodeDetector.Builder(ctx)
                    .setBarcodeFormats(Barcode.QR_CODE).build()

        return barcodeDetector
    }

    fun <C> ConvertToList(sparseArray: SparseArray<C>?): List<C>? {
        if (sparseArray == null) return null
        val arrayList = ArrayList<C>(sparseArray.size())

        for (i in 0 until sparseArray.size())
            arrayList.add(sparseArray.valueAt(i))
        return arrayList
    }

    fun getCameraSource(): CameraSource? {
        if (cameraSource == null) {

            getBarcodeDetector()?.setProcessor(object : Detector.Processor<Barcode> {
                override fun receiveDetections(detector: Detector.Detections<Barcode>?) {
                    val detectedItems = detector?.detectedItems
                    val barcodeSize = detectedItems?.size() ?: 0
                    if (barcodeSize != 0) {
                        ConvertToList(detectedItems)?.filter { it.rawValue.isNotBlank() }?.forEach {
                            onAuthDecoded?.onQrDetected(it.boundingBox)
                            it.displayValue.split("#").apply {
                                if (this.size == 2) {
                                    val auth = Auth(get(0), get(1))
                                    onAuthDecoded?.authDecoded(auth)
                                }
                            }
                        }
                    }
                }

                override fun release() {
                }
            })

            cameraSource = CameraSource.Builder(ctx, barcodeDetector)
                    .setRequestedFps(24f)
                    .setAutoFocusEnabled(true)
                    .build()

        }
        return cameraSource
    }
}