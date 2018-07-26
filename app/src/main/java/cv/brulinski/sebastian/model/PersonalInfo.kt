package cv.brulinski.sebastian.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Class to hold personal info
 */
@Entity
class PersonalInfo {

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id = 1
    @SerializedName("timestamp")
    @ColumnInfo(name = "timestamp")
    var timestamp = -1L
    @ColumnInfo(name = "name")
    @SerializedName("name")
    var name = ""
    @ColumnInfo(name = "surname")
    @SerializedName("surname")
    var surname = ""
    @ColumnInfo(name = "birthDay")
    @SerializedName("birthDay")
    var birthDay = ""
    @ColumnInfo(name = "birthMonth")
    @SerializedName("birthMonth")
    var birthMonth = ""
    @ColumnInfo(name = "birthYear")
    @SerializedName("birthYear")
    var birthYear = ""
    @ColumnInfo(name = "profileUrl")
    @SerializedName("profileUrl")
    var profilePhotoUrl = ""
    @ColumnInfo(name = " profileBcgUrl")
    @SerializedName("profileBcgUrl")
    var profileBcgUrl = ""
    @ColumnInfo(name = "phoneNumber")
    @SerializedName("phoneNumber")
    var phoneNumber = ""
    @ColumnInfo(name = "email")
    @SerializedName("email")
    var email = ""
    @ColumnInfo(name = "city")
    @SerializedName("city")
    var cityName = ""
    @ColumnInfo(name = "province")
    @SerializedName("province")
    var provinceName = ""
    @ColumnInfo(name = "latitude")
    @SerializedName("latitude")
    var latitude = 0.0
    @ColumnInfo(name = "longitude")
    @SerializedName("longitude")
    var longitude = 0.0
    @Expose
    @Ignore
    var profilePicture: Bitmap? = null
    @Expose
    @Ignore
    var profileBcg: Bitmap? = null
    @Expose
    @ColumnInfo(name = "profilePictureBase64")
    var profilePictureBase64: String? = ""
    @Expose
    @ColumnInfo(name = "profilePictureBcgBase64")
    var profilePictureBcgBase64: String? = ""
}