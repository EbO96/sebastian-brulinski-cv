package cv.brulinski.sebastian.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import cv.brulinski.sebastian.interfaces.BitmapLoadable

@Entity
class Language : BitmapLoadable {

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

    @Expose
    @Ignore
    var flagBitmap: Bitmap? = null

    @ColumnInfo(name = "avgBitmapColor")
    @Expose
    var avgBitmapColor: Int = -1

    override fun setAvgColor(color: Int) {
        avgBitmapColor = color
    }

    override fun getSortKey() = "$level"

    override fun getTypeId() = id

    override fun getTypeBitmap() = flagBitmap

    override fun getTypeBitmapBase64() = flagBase64

    override fun setTypeBitmapBase64(bitmap: String?) {
        flagBase64 = bitmap
    }

    override fun setTypeBitmap(bitmap: Bitmap?) {
        flagBitmap = bitmap
    }

    override fun getTypeSkillCategory(): String? = null
}