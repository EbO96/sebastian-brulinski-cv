package cv.brulinski.sebastian.utils

import android.graphics.Color
import androidx.core.content.ContextCompat
import cv.brulinski.sebastian.dependency_injection.app.App

fun Int.color() = ContextCompat.getColor(App.component.getContext(), this)

fun Int.setAlpha(alpha: Int): Int? {
    return try {
        val r = Color.red(this)
        val g = Color.green(this)
        val b = Color.blue(this)
        Color.argb(Math.abs(alpha).let { if (it > 255) 255 else it }, r, g, b)
    } catch (e: RuntimeException) {
        null
    }
}