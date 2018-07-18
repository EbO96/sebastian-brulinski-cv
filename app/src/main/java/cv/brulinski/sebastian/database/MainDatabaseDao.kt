package cv.brulinski.sebastian.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cv.brulinski.sebastian.model.PersonalInfo
import cv.brulinski.sebastian.model.School
import cv.brulinski.sebastian.model.Welcome

@Dao
interface MainDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWelcome(welcome: Welcome)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPersonalInfo(personalInfo: PersonalInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSchools(schools: List<School>)

    @Query("DELETE FROM Welcome")
    fun deleteWelcome()

    @Query("DELETE FROM PersonalInfo")
    fun deletePersonalInfo()

    @Query("DELETE FROM School")
    fun deleteSchools()

    @Query("SELECT * FROM Welcome")
    fun getWelcome(): LiveData<Welcome>

    @Query("SELECT * FROM PersonalInfo")
    fun getPersonalInfo(): LiveData<PersonalInfo>

    @Query("SELECT * FROM School")
    fun getSchools(): LiveData<List<School>>
}