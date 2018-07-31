package cv.brulinski.sebastian.interfaces

interface BitmapLoadable : OverrideByOldBitmap {

    fun getTypeId(): String
    fun getTypeSkillCategory(): String?
}