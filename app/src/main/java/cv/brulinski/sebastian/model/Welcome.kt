package cv.brulinski.sebastian.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Welcome screen content
 */
@Entity
class Welcome {

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id = 1
    @SerializedName("title")
    @ColumnInfo(name = "title")
    var title = ""
    @SerializedName("description")
    @ColumnInfo(name = "description")
    var description = ""
}