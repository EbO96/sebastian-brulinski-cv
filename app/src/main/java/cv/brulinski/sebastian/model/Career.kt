package cv.brulinski.sebastian.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import cv.brulinski.sebastian.annotations.Crypto
import cv.brulinski.sebastian.utils.TYPE_ITEM

@Entity
class Career() : RecyclerItem(), Cloneable {

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

    @Crypto
    @SerializedName("placeName")
    @ColumnInfo(name = "placeName")
    var placeName = ""

    @Crypto
    @SerializedName("function")
    @ColumnInfo(name = "function")
    var function = ""

    @Crypto
    @SerializedName("description")
    @ColumnInfo(name = "description")
    var description = ""

    @Crypto
    @SerializedName("startTimeDescription")
    @ColumnInfo(name = "startTimeDescription")
    var startTimeDescription = ""

    @Crypto
    @SerializedName("startTime")
    @ColumnInfo(name = "startTime")
    var startTime = ""

    @Crypto
    @SerializedName("endTimeDescription")
    @ColumnInfo(name = "endTimeDescription")
    var endTimeDescription = ""

    @Crypto
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

    constructor(parcel: Parcel) : this() {
        itemType = parcel.readInt()
        id = parcel.readString()
        type = parcel.readInt()
        placeName = parcel.readString()
        function = parcel.readString()
        description = parcel.readString()
        startTimeDescription = parcel.readString()
        startTime = parcel.readString()
        endTimeDescription = parcel.readString()
        endTime = parcel.readString()
        latitude = parcel.readDouble()
        longitude = parcel.readDouble()
        timestamp = parcel.readLong()
    }

    @Ignore
    public override fun clone(): Career {
        return super.clone() as Career
    }
}