package cv.brulinski.sebastian.utils

import android.content.Context
import androidx.core.content.ContextCompat
import cv.brulinski.sebastian.dependency_injection.app.App

fun Int.drawable(ctx: Context? = null) = ContextCompat.getDrawable(ctx
        ?: App.component.getContext(), this)