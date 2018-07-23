package cv.brulinski.sebastian.utils

import android.util.Log
import android.widget.Toast

val TAG = "cvapp"

infix fun String.log(message: String) = Log.i(this, message)

fun String.toast(long: Boolean = false) = Toast.makeText(ctx, this, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()