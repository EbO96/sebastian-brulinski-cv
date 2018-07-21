package cv.brulinski.sebastian.utils

import android.util.Log

val TAG = "cvapp"

infix fun String.log(message: String) = Log.i(this, message)