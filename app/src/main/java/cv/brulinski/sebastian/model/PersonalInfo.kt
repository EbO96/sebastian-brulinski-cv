package cv.brulinski.sebastian.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import cv.brulinski.sebastian.annotations.Crypto

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
    @Crypto
    @ColumnInfo(name = "name")
    @SerializedName("name")
    var name = ""

    @Crypto
    @ColumnInfo(name = "surname")
    @SerializedName("surname")
    var surname = ""

    @Crypto
    @ColumnInfo(name = "speciality")
    @SerializedName("speciality")
    var speciality = ""

    @Crypto
    @ColumnInfo(name = "birthDay")
    @SerializedName("birthDay")
    var birthDay = ""

    @Crypto
    @ColumnInfo(name = "birthMonth")
    @SerializedName("birthMonth")
    var birthMonth = ""

    @Crypto
    @ColumnInfo(name = "birthYear")
    @SerializedName("birthYear")
    var birthYear = ""

    @Crypto
    @ColumnInfo(name = "phoneNumber")
    @SerializedName("phoneNumber")
    var phoneNumber = ""

    @ColumnInfo(name = "numberType")
    @SerializedName("numberType")
    var numberType = ""

    @Crypto
    @ColumnInfo(name = "email")
    @SerializedName("email")
    var email = ""

    @ColumnInfo(name = "emailType")
    @SerializedName("emailType")
    var emailType = ""

    /*
    Images
     */
    @Crypto
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
    @Crypto
    @ColumnInfo(name = "city")
    @SerializedName("city")
    var cityName = ""
    @Crypto
    @ColumnInfo(name = "province")
    @SerializedName("province")
    var provinceName = ""
    @Crypto
    @ColumnInfo(name = "postalCode")
    @SerializedName("postalCode")
    var postalCode = ""
    @ColumnInfo(name = "latitude")
    @SerializedName("latitude")
    var latitude = 0.0
    @ColumnInfo(name = "longitude")
    @SerializedName("longitude")
    var longitude = 0.0
    @Crypto
    @ColumnInfo(name = "country")
    @SerializedName("country")
    var country = ""
}