package cv.brulinski.sebastian.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * User skill
 */
@Entity
class Skill {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id = ""
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
}