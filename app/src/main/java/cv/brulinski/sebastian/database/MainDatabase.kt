package cv.brulinski.sebastian.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cv.brulinski.sebastian.model.*

@Database(entities = [Welcome::class, PersonalInfo::class, School::class, Job::class], version = 1)
abstract class MainDatabase : RoomDatabase() {
    abstract fun daoAccess(): MainDatabaseDao
}