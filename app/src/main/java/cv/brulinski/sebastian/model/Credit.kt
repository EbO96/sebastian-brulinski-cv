package cv.brulinski.sebastian.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class Credit {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id = ""

    @SerializedName("author")
    @ColumnInfo(name = "author")
    var author = ""

    @SerializedName("authorUrl")
    @ColumnInfo(name = "authorUrl")
    var authorUrl = ""

}