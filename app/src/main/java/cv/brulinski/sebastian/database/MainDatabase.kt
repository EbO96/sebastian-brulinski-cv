package cv.brulinski.sebastian.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cv.brulinski.sebastian.model.Welcome

@Database(entities = [Welcome::class], version = 1)
abstract class MainDatabase : RoomDatabase() {
    abstract fun daoAccess(): MainDatabaseDao
}