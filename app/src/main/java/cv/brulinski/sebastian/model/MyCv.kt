package cv.brulinski.sebastian.model

import com.google.gson.annotations.SerializedName

class MyCv {

    @SerializedName("status")
    var status = -1
    @SerializedName("welcome")
    var welcome: Welcome? = null
    @SerializedName("personal_info")
    var personalInfo: PersonalInfo? = null
    @SerializedName("career")
    var career: Career? = null
    @SerializedName("languages")
    var languages: ArrayList<Language>? = null
}