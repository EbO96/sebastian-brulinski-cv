package cv.brulinski.sebastian.utils

import cv.brulinski.sebastian.database.AppDatabase
import cv.brulinski.sebastian.dependency_injection.app.App

val database by lazy { AppDatabase().database(App.component.getContext()).daoAccess() }