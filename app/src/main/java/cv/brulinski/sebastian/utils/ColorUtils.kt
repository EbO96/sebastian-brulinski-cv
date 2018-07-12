package cv.brulinski.sebastian.utils

import androidx.core.content.ContextCompat
import cv.brulinski.sebastian.dependency_injection.app.App

fun Int.color() = ContextCompat.getColor(App.component.getContext(), this)