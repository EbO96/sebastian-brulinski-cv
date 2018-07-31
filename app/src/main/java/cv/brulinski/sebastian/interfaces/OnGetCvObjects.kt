package cv.brulinski.sebastian.interfaces

import cv.brulinski.sebastian.model.Career
import cv.brulinski.sebastian.model.Language
import cv.brulinski.sebastian.model.Skill

interface OnGetCvObjects {
    fun getTypeSkills(): List<Skill>?
    fun getTypeCareer(): List<Career>?
    fun getTypeLanguages(): List<Language>?
}