package cv.brulinski.sebastian.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class Language {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id = ""

    @ColumnInfo(name = "name")
    @SerializedName("name")
    var name = ""

    @ColumnInfo(name = "description")
    @SerializedName("description")
    var description = ""

    @ColumnInfo(name = "level")
    @SerializedName("level")
    var level = 1

    @ColumnInfo(name = "levelScale")
    @SerializedName("levelScale")
    var levelScale = 1

    @ColumnInfo(name = "timestamp")
    @SerializedName("timestamp")
    var timestamp = -1L
}