package cv.brulinski.sebastian.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cv.brulinski.sebastian.model.PersonalInfo
import cv.brulinski.sebastian.model.Welcome

@Dao
interface MainDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWelcome(welcome: Welcome)

    @Query("DELETE FROM Welcome")
    fun deleteWelcome()

    @Query("SELECT * FROM Welcome")
    fun getWelcome(): LiveData<Welcome>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPersonalInfo(personalInfo: PersonalInfo)

    @Query("DELETE FROM PersonalInfo")
    fun deletePersonalInfo()

    @Query("SELECT * FROM PersonalInfo")
    fun getPersonalInfo(): LiveData<PersonalInfo>

}