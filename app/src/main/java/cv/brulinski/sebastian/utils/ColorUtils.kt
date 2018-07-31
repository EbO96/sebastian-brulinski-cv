package cv.brulinski.sebastian.utils

import android.graphics.Bitmap
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

fun Bitmap.getAverageColor(): Int {
    var redColors = 0
    var greenColors = 0
    var blueColors = 0
    var pixelCount = 0

    for (y in 0 until height) {
        for (x in 0 until width) {
            val c = getPixel(x, y)
            pixelCount++
            redColors += Color.red(c)
            greenColors += Color.green(c)
            blueColors += Color.blue(c)
        }
    }
    val red = redColors / pixelCount
    val green = greenColors / pixelCount
    val blue = blueColors / pixelCount

    return Color.argb(56, red, green, blue)
}