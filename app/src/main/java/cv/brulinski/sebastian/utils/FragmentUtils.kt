package cv.brulinski.sebastian.utils

import android.transition.Fade
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun Fragment.set(fm: FragmentManager, container: Int, replace: Boolean = false) {
    fm.beginTransaction().apply {
        val fade = Fade()
        enterTransition = fade
        exitTransition = fade
        if (replace)
            replace(container, this@set)
        else add(container, this@set)
        addToBackStack("${this@set}")
        commit()
    }
}