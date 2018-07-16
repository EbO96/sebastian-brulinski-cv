package cv.brulinski.sebastian.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Class to hold personal info
 */
@Entity
class PersonalInfo {

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id = 1
    @ColumnInfo(name = "name")
    @SerializedName("name")
    var name = ""
    @ColumnInfo(name = "surname")
    @SerializedName("surname")
    var surname = ""
    @ColumnInfo(name = "birthDay")
    @SerializedName("birthDay")
    var birthDay = 0
    @ColumnInfo(name = "birthMonth")
    @SerializedName("birthMonth")
    var birthMonth = 0
    @ColumnInfo(name = "birthYear")
    @SerializedName("birthYear")
    var birthYear = 0
    @ColumnInfo(name = "profileUrl")
    @SerializedName("profileUrl")
    var profilePhotoUrl = ""
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

}