package cv.brulinski.sebastian.utils

import cv.brulinski.sebastian.dependency_injection.app.App

val ctx by lazy { App.component.getContext() }

fun Int.get() = ctx.getString(this)