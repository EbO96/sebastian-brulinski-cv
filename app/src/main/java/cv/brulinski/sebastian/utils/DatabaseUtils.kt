package cv.brulinski.sebastian.utils

import cv.brulinski.sebastian.database.AppDatabase
import cv.brulinski.sebastian.dependency_injection.app.App
import cv.brulinski.sebastian.model.MyCv

val database by lazy { AppDatabase().database(App.component.getContext()).daoAccess() }

fun MyCv.insert() {
    doAsync {
        database.apply {
            career?.let { insertCareer(it) }
            welcome?.let { insertWelcome(it) }
            personalInfo?.let { insertPersonalInfo(it) }
            languages?.let { insertLanguages(it) }
            skills?.let { insertSkills(it) }
        }
    }
}

fun deleteCvLocal(result: () -> Unit) {
    doAsync {
        with(database) {
            deleteWelcome()
            deletePersonalInfo()
            deleteCareer()
            deleteLanguage()
            deleteSkill()
            deleteCredit()
            result()
        }
    }
}



