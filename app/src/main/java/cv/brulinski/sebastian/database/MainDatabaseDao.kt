package cv.brulinski.sebastian.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cv.brulinski.sebastian.model.*

@Dao
interface MainDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWelcome(welcome: Welcome)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPersonalInfo(personalInfo: PersonalInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLanguages(language: List<Language>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSkills(skill: List<Skill>)

    @Query("DELETE FROM Career")
    fun deleteCareer()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCareer(career: List<Career>)

    @Query("DELETE FROM Welcome")
    fun deleteWelcome()

    @Query("DELETE FROM PersonalInfo")
    fun deletePersonalInfo()


    @Query("SELECT * FROM Welcome")
    fun getWelcome(): LiveData<Welcome>

    @Query("SELECT * FROM PersonalInfo")
    fun getPersonalInfo(): LiveData<PersonalInfo>


    @Query("SELECT * FROM Language")
    fun getLanguages(): LiveData<List<Language>>

    @Query("SELECT * FROM Career")
    fun getCareer(): LiveData<List<Career>>

    @Query("SELECT * FROM Skill")
    fun getSkills(): LiveData<List<Skill>>

}