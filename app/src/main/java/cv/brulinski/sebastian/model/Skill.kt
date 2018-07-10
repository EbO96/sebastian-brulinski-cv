package cv.brulinski.sebastian.model

/**
 * User skill
 */
class Skill {

    var name = ""
    var description = ""
    var level = 0
        set(value) {
            var level = Math.abs(value)
            if (level > 5)
                level = 5
            field = level
        }
}