package cv.brulinski.sebastian.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
open class School {

    @PrimaryKey
    @SerializedName("place")
    @ColumnInfo(name = "place")
    var place = ""

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
}