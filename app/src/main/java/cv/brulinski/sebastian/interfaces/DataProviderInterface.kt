package cv.brulinski.sebastian.interfaces

import cv.brulinski.sebastian.model.*

interface DataProviderInterface {
    fun getWelcome(block: (Welcome) -> Unit)
    fun getPersonalInfo(block: (PersonalInfo) -> Unit)
    fun getCareer(block: (List<Career>) -> Unit)
    fun getLanguages(block: (List<Language>) -> Unit)
    fun getSkills(block: (List<Skill>) -> Unit)
}