package cv.brulinski.sebastian.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity
class Language {

    @SerializedName("id")
    var id = ""
    @SerializedName("name")
    var name = ""
    @SerializedName("description")
    var description = ""
    @SerializedName("level")
    var level = 1
    @SerializedName("levelScale")
    var levelScale = 1
}