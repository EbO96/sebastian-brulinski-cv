package cv.brulinski.sebastian.model

import cv.brulinski.sebastian.utils.getPrefsValue
import cv.brulinski.sebastian.utils.putPrefsValue

class AppSettings {

    //Keys
    private val fetchGraphicsKey = "fetch_graphics_key"
    private val firstLaunchKey = "firstLaunch"
    //Values
    var fetchGraphics = true
        set(value) {
            value.putPrefsValue(fetchGraphicsKey)
            field = value
        }
    var firstLaunch = true
        set(value) {
            value.putPrefsValue(firstLaunchKey)
            field = value
        }

    init {
        firstLaunch = (getPrefsValue(firstLaunchKey) as Boolean) ?: true
        fetchGraphics = (getPrefsValue(fetchGraphicsKey) as Boolean) ?: true
    }
}