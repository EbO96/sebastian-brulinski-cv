package cv.brulinski.sebastian.interfaces

import androidx.annotation.ColorInt

interface BitmapLoadable : OverrideByOldBitmap {

    fun setAvgColor(@ColorInt color: Int)
    fun getSortKey(): String
    fun getTypeId(): String
    fun getTypeSkillCategory(): String?
}