package cv.brulinski.sebastian.utils

import cv.brulinski.sebastian.database.AppDatabase
import cv.brulinski.sebastian.dependency_injection.app.App
import cv.brulinski.sebastian.model.*

val database by lazy { AppDatabase().database(App.component.getContext()).daoAccess() }

fun Welcome.insert() {
    doAsync {
        database.insertWelcome(this)
    }
}

fun deleteWelcome() {
    doAsync {
        database.deleteWelcome()
    }
}

fun PersonalInfo.insert() {
    doAsync {
        database.insertPersonalInfo(this)
    }
}

fun deletePersonalInfo() {
    doAsync {
        database.deletePersonalInfo()
    }
}

fun List<School>.insertSchools() {
    doAsync {
        database.insertSchools(this)
    }
}

fun List<Job>.insertJobs() {
    doAsync {
        database.insertJobs(this)
    }
}



