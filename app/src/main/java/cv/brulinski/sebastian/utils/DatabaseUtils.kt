package cv.brulinski.sebastian.utils

import android.os.AsyncTask
import cv.brulinski.sebastian.database.AppDatabase
import cv.brulinski.sebastian.dependency_injection.app.App
import cv.brulinski.sebastian.model.Welcome

val database by lazy { AppDatabase().database(App.component.getContext()).daoAccess() }

class InsertWelcome : AsyncTask<Welcome, Unit, Unit?>() {
    override fun doInBackground(vararg p0: Welcome?): Unit? {
        p0.forEach {
           it?.apply { database.insertWelcome(this) }
        }
        return null
    }
}