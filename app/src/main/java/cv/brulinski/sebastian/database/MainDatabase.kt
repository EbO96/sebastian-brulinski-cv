package cv.brulinski.sebastian.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cv.brulinski.sebastian.model.*

@Database(entities = [Welcome::class, PersonalInfo::class, Career::class, Language::class, Skill::class,
    Credit::class], version = 1,
        exportSchema = false)
abstract class MainDatabase : RoomDatabase() {
    abstract fun daoAccess(): MainDatabaseDao
}