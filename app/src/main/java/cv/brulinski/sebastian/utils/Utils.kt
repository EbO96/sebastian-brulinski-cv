package cv.brulinski.sebastian.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.AsyncTask
import android.os.Handler
import android.preference.PreferenceManager
import android.webkit.URLUtil
import cv.brulinski.sebastian.dependency_injection.app.App

val ctx by lazy { App.component.getContext() }

val settings by lazy { App.component.getAppSettings() }

fun Int.string() = ctx.getString(this)

fun doAsync(unit: () -> Unit) {
    MyAsync(unit)
}

private class MyAsync(val handler: () -> Unit) : AsyncTask<Unit?, Unit?, Unit?>() {

    init {
        execute()
    }

    override fun doInBackground(vararg p0: Unit?): Unit? {
        handler()
        return null
    }
}

infix fun Any.putPrefsValue(tag: String) {
    App.component.getContext().apply {
        PreferenceManager.getDefaultSharedPreferences(this).edit().apply {
            when (this@putPrefsValue) {
                is String -> putString(tag, this@putPrefsValue)
                is Boolean -> putBoolean(tag, this@putPrefsValue)
                is Int -> putInt(tag, this@putPrefsValue)
                is Float -> putFloat(tag, this@putPrefsValue)
                is Long -> putLong(tag, this@putPrefsValue)
            }
            apply()
        }
    }
}

fun getPrefsValue(tag: String) = PreferenceManager.getDefaultSharedPreferences(App.component.getContext()).all[tag]

fun <T> Long.delay(trigger: () -> T) {
    Handler().postDelayed({
        trigger()
    }, this)
}

fun Int.iterate(step: (Int) -> Unit) {
    for (x in 1..this) {
        step(x)
    }
}

fun isNetworkAvailable(): Boolean {
    val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    return activeNetwork?.isConnected == true
}

fun String.openUrl(activity: Activity? = null, requestCode: Int? = null): Boolean {
    var success = false
    try {
        if (URLUtil.isValidUrl(this)) {
            val page = Uri.parse(this)
            Intent(Intent.ACTION_VIEW, page).let { intent ->
                intent.resolveActivity(ctx.packageManager).apply {
                    success = true
                    if (activity != null && requestCode != null) {
                        activity.startActivityForResult(intent, requestCode)
                    } else ctx.startActivity(intent)
                }
            }
        }
    } catch (e: Exception) {
        return false
    }
    return success
}