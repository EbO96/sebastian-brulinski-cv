package cv.brulinski.sebastian.model

import androidx.room.ColumnInfo
import androidx.room.Entity
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

    /*
    About me
     */
    @ColumnInfo(name = "name")
    @SerializedName("name")
    var name = ""

    @ColumnInfo(name = "surname")
    @SerializedName("surname")
    var surname = ""

    @ColumnInfo(name = "speciality")
    @SerializedName("speciality")
    var speciality = ""

    @ColumnInfo(name = "birthDay")
    @SerializedName("birthDay")
    var birthDay = ""

    @ColumnInfo(name = "birthMonth")
    @SerializedName("birthMonth")
    var birthMonth = ""

    @ColumnInfo(name = "birthYear")
    @SerializedName("birthYear")
    var birthYear = ""

    @ColumnInfo(name = "phoneNumber")
    @SerializedName("phoneNumber")
    var phoneNumber = ""

    @ColumnInfo(name = "numberType")
    @SerializedName("numberType")
    var numberType = ""

    @ColumnInfo(name = "email")
    @SerializedName("email")
    var email = ""

    @ColumnInfo(name = "emailType")
    @SerializedName("emailType")
    var emailType = ""

    /*
    Images
     */
    @ColumnInfo(name = "profileUrl")
    @SerializedName("profileUrl")
    var profilePhotoUrl = ""
    @ColumnInfo(name = " profileBcgUrl")
    @SerializedName("profileBcgUrl")
    var profileBcgUrl = ""
    @Expose
    @ColumnInfo(name = "profilePictureBase64")
    var profilePictureBase64: String? = ""
    @Expose
    @ColumnInfo(name = "profilePictureBcgBase64")
    var profilePictureBcgBase64: String? = ""

    /*
    Localization
     */
    @ColumnInfo(name = "city")
    @SerializedName("city")
    var cityName = ""
    @ColumnInfo(name = "province")
    @SerializedName("province")
    var provinceName = ""
    @ColumnInfo(name = "postalCode")
    @SerializedName("postalCode")
    var postalCode = ""
    @ColumnInfo(name = "latitude")
    @SerializedName("latitude")
    var latitude = 0.0
    @ColumnInfo(name = "longitude")
    @SerializedName("longitude")
    var longitude = 0.0
    @ColumnInfo(name = "country")
    @SerializedName("country")
    var country = ""
}