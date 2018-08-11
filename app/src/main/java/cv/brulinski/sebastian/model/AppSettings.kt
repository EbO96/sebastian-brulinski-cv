package cv.brulinski.sebastian.model

import cv.brulinski.sebastian.utils.getPrefsValue
import cv.brulinski.sebastian.utils.putPrefsValue

class AppSettings {

    //Keys
    private val fetchGraphicsKey = "fetch_graphics_key"
    private val firstLaunchKey = "firstLaunch"
    private val newCvNotificationKey = "newCvNotification"
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
    var newCvNotification = true
        set(value) {
            value.putPrefsValue(newCvNotificationKey)
            field = value
        }


    init {
        firstLaunch = (getPrefsValue(firstLaunchKey) as? Boolean) ?: true
        fetchGraphics = (getPrefsValue(fetchGraphicsKey) as? Boolean) ?: true
        newCvNotification = (getPrefsValue(newCvNotificationKey) as? Boolean) ?: true
    }
}