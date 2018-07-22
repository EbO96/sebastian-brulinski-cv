package cv.brulinski.sebastian.utils

import android.os.AsyncTask
import android.os.Handler
import android.preference.PreferenceManager
import cv.brulinski.sebastian.dependency_injection.app.App

val ctx by lazy { App.component.getContext() }

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