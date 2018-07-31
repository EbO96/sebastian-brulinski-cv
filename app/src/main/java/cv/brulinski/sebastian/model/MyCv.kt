package cv.brulinski.sebastian.model

import com.google.gson.annotations.SerializedName
import cv.brulinski.sebastian.interfaces.OnGetCvObjects

class MyCv : OnGetCvObjects {

    @SerializedName("status")
    var status = -1
    @SerializedName("welcome")
    var welcome: Welcome? = Welcome()
    @SerializedName("personal_info")
    var personalInfo: PersonalInfo? = PersonalInfo()
    @SerializedName("career")
    var career: List<Career>? = listOf()
    @SerializedName("languages")
    var languages: List<Language>? = listOf()
    @SerializedName("skills")
    var skills: List<Skill>? = listOf()

    override fun getTypeSkills(): List<Skill>? = skills

    override fun getTypeCareer(): List<Career>? = career

    override fun getTypeLanguages(): List<Language>? = languages
}