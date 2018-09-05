package cv.brulinski.sebastian.utils.camera

import android.graphics.Rect
import cv.brulinski.sebastian.model.Auth

interface OnAuthDecoded {
    fun authDecoded(auth: Auth)
    fun onQrDetected(rect: Rect)
}