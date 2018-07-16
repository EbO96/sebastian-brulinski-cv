package cv.brulinski.sebastian.utils

import cv.brulinski.sebastian.database.AppDatabase
import cv.brulinski.sebastian.dependency_injection.app.App
import cv.brulinski.sebastian.model.PersonalInfo
import cv.brulinski.sebastian.model.Welcome

val database by lazy { AppDatabase().database(App.component.getContext()).daoAccess() }

fun insertWelcome(welcome: Welcome) {
    doAsync {
        database.insertWelcome(welcome)
    }
}

fun insertPersonalInfo(personalInfo: PersonalInfo) {
    doAsync {
        database.insertPersonalInfo(personalInfo)
    }
}
