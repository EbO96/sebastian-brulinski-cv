package cv.brulinski.sebastian.utils

import cv.brulinski.sebastian.dependency_injection.app.App

val retrofit by lazy { App.component.getRetrofitApiCallbacks() }