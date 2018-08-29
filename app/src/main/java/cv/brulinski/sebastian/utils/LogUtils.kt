package cv.brulinski.sebastian.utils

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

infix fun String.log(message: String) = Log.i(this, message)

fun String.toast(long: Boolean = false) = Toast.makeText(ctx, this, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()

fun String.snack(activity: Activity, long: Boolean = false, dismiss: (() -> Unit)? = null) =
        Snackbar.make(activity.findViewById(android.R.id.content), this, if (long) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT).apply {
            addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    dismiss?.invoke()
                }
            })
            show()
        }