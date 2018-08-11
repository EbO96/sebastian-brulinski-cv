package cv.brulinski.sebastian.model

import com.google.gson.annotations.SerializedName
import cv.brulinski.sebastian.annotations.Crypto
import cv.brulinski.sebastian.interfaces.CryptoClass
import cv.brulinski.sebastian.interfaces.OnGetCvObjects

class MyCv : OnGetCvObjects, CryptoClass, Cloneable {

    @SerializedName("status")
    var status = -1

    @Crypto
    @SerializedName("welcome")
    var welcome: Welcome? = Welcome()

    @Crypto
    @SerializedName("personal_info")
    var personalInfo: PersonalInfo? = PersonalInfo()

    @Crypto
    @SerializedName("career")
    var career: List<Career>? = listOf()

    @SerializedName("languages")
    var languages: List<Language>? = listOf()

    @SerializedName("skills")
    var skills: List<Skill>? = listOf()

    override fun getTypeSkills(): List<Skill>? = skills

    override fun getTypeCareer(): List<Career>? = career

    override fun getTypeLanguages(): List<Language>? = languages

    override fun getTypePersonalInfo(): List<PersonalInfo> = listOf(personalInfo ?: PersonalInfo())

    public override fun clone(): MyCv {
        return super.clone() as MyCv
    }
}