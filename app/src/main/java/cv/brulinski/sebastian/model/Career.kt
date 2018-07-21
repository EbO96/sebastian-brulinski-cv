package cv.brulinski.sebastian.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Career {

    @SerializedName("status")
    var status = -1

    @SerializedName("schools")
    var schools: List<School> = arrayListOf()

    @SerializedName("jobs")
    var jobs: List<Job> = arrayListOf()

    @Expose
    var timestamp = -1L
}