package cv.brulinski.sebastian.model

/**
 * User known language
 */
class KnownLanguage {

    var name = ""
    var level = 0
        set(value) {
            var level = Math.abs(value)
            if (level > 5)
                level = 5
            field = level
        }
    var description = ""
    var flagUrl = ""
}