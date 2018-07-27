package cv.brulinski.sebastian.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
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

    @ColumnInfo(name = "image_url")
    @SerializedName("imageUrl")
    var imageUrl = ""

    @Expose
    @ColumnInfo(name = "flagBase64")
    var flagBase64: String? = ""
}