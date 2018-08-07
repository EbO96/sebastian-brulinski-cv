package cv.brulinski.sebastian.interfaces

import cv.brulinski.sebastian.model.*

/**
 * Used for getting CV data from MainActivity by each fragment
 * @see cv.brulinski.sebastian.activity.MainActivity
 * @see Welcome
 * @see PersonalInfo
 * @see Career
 * @see Language
 * @see Skill
 */
interface DataProviderInterface {
    fun getWelcome(block: (Welcome) -> Unit)
    fun getPersonalInfo(block: (PersonalInfo) -> Unit)
    fun getCareer(block: (List<Career>) -> Unit)
    fun getLanguages(block: (List<Language>) -> Unit)
    fun getSkills(block: (List<Skill>) -> Unit)
}