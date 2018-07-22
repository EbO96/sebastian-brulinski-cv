package cv.brulinski.sebastian.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import cv.brulinski.sebastian.utils.TYPE_ITEM

@Entity
class Career : RecyclerItem(), Cloneable {

    @Ignore
    @Expose
    override var itemType: Int = TYPE_ITEM

    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id = ""

    @SerializedName("type")
    @ColumnInfo(name = "type")
    var type = -1

    @SerializedName("placeName")
    @ColumnInfo(name = "placeName")
    var placeName = ""

    @SerializedName("function")
    @ColumnInfo(name = "function")
    var function = ""

    @SerializedName("description")
    @ColumnInfo(name = "description")
    var description = ""

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

    @SerializedName("timestamp")
    @ColumnInfo(name = "timestamp")
    var timestamp = -1L

    @Ignore
    public override fun clone(): Career {
        return super.clone() as Career
    }
}