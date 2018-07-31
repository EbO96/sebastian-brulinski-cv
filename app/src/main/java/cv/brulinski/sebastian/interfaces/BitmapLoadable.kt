package cv.brulinski.sebastian.interfaces

interface BitmapLoadable : OverrideByOldBitmap {

    fun getSortKey(): String
    fun getTypeId(): String
    fun getTypeSkillCategory(): String?
}