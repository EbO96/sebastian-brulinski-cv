package cv.brulinski.sebastian.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class Job {

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id = ""

    @SerializedName("name")
    @ColumnInfo(name = "name")
    var name = ""

    @SerializedName("companyName")
    @ColumnInfo(name = "companyName")
    var companyName = ""

    @SerializedName("jobPosition")
    @ColumnInfo(name = "jobPosition")
    var jobPosition = ""

    @SerializedName("jobDescription")
    @ColumnInfo(name = "jobDescription")
    var jobDescription = ""

    @SerializedName("startTimeDescription")
    @ColumnInfo(name = "startTimeDescription")
    var startTimeDescription = ""

    @SerializedName("startTime")
    @ColumnInfo(name = "startTime")
    var startTime = ""

    @SerializedName("endTimeDescription")
    @ColumnInfo(name = "endTimeDescription")
    var endTimeDescription = ""

    @SerializedName("endTime")
    @ColumnInfo(name = "endTime")
    var endTime = ""

    @SerializedName("latitude")
    @ColumnInfo(name = "latitude")
    var latitude = 0.0

    @SerializedName("longitude")
    @ColumnInfo(name = "longitude")
    var longitude = 0.0

}