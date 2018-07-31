package cv.brulinski.sebastian.interfaces

import android.graphics.Bitmap

interface OverrideByOldBitmap {
    fun setTypeBitmap(bitmap: Bitmap?)
    fun getTypeBitmap(): Bitmap?
    fun setTypeBitmapBase64(bitmap: String?)
    fun getTypeBitmapBase64(): String?
}