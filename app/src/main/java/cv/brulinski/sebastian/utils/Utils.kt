package cv.brulinski.sebastian.utils

import android.os.AsyncTask
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