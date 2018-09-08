package cv.brulinski.sebastian.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
class PersonalDataProcessing {

    @Expose
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id = 1

    @SerializedName("message")
    @ColumnInfo(name = "message")
    var message = ""

    @SerializedName("showAgain")
    @ColumnInfo(name = "showAgain")
    var showAgain = true
}