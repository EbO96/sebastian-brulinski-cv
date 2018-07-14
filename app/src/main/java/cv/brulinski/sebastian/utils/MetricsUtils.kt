package cv.brulinski.sebastian.utils

import android.util.TypedValue
import cv.brulinski.sebastian.dependency_injection.app.App

fun Float.pxToDp() =
        TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                this,
                App.component.getContext().resources.displayMetrics
        )