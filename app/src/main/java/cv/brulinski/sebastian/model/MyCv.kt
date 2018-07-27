package cv.brulinski.sebastian.model

import com.google.gson.annotations.SerializedName

class MyCv {

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
}