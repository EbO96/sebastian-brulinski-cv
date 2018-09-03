package cv.brulinski.sebastian.utils

import android.app.Activity
import androidx.core.app.ActivityCompat

fun Activity.shouldRequestRationale(permission: String) =
        ActivityCompat.shouldShowRequestPermissionRationale(this,
                permission)