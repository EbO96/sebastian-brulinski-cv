package cv.brulinski.sebastian.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import cv.brulinski.sebastian.annotations.Crypto
import cv.brulinski.sebastian.interfaces.BitmapLoadable

/**
 * User skill
 */
@Entity
open class Skill : BitmapLoadable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id = ""

    @ColumnInfo(name = "moreButton")
    @SerializedName("moreButton")
    var moreButton = false

    @Crypto
    @ColumnInfo(name = "moreUrl")
    @SerializedName("moreUrl")
    var moreUrl = ""

    @ColumnInfo(name = "skillName")
    @SerializedName("skillName")
    var skillName = ""

    @ColumnInfo(name = "skillDescription")
    @SerializedName("skillDescription")
    var skillDescription = ""

    @ColumnInfo(name = "skillCategory")
    @SerializedName("skillCategory")
    var skillCategory = ""

    @ColumnInfo(name = "timestamp")
    @SerializedName("timestamp")
    var timestamp = -1L

    @ColumnInfo(name = "iconUrl")
    @SerializedName("iconUrl")
    var iconUrl = ""

    @ColumnInfo(name = "iconBase64")
    @Expose
    var iconBase64: String? = ""

    @Ignore
    @Expose
    var iconBitmap: Bitmap? = null

    @ColumnInfo(name = "avgBitmapColor")
    @Expose
    var avgBitmapColor: Int = -1

    override fun getSortKey() = skillName

    override fun getTypeId() = id

    override fun getTypeBitmap() = iconBitmap

    override fun getTypeBitmapBase64() = iconBase64

    override fun setTypeBitmapBase64(bitmap: String?) {
        iconBase64 = bitmap
    }

    override fun setTypeBitmap(bitmap: Bitmap?) {
        iconBitmap = bitmap
    }

    override fun setAvgColor(color: Int) {
        avgBitmapColor = color
    }

    override fun getTypeSkillCategory() = skillCategory
}