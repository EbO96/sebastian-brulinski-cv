package cv.brulinski.sebastian.database

import android.content.Context
import androidx.room.Room

class AppDatabase {
    fun database(context: Context) = Room.databaseBuilder(context, MainDatabase::class.java, "main_database").build()
}