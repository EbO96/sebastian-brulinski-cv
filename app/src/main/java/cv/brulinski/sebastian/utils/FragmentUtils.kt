package cv.brulinski.sebastian.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

var currentFragment: Fragment? = null

fun Fragment.set(fm: FragmentManager, container: Int, replace: Boolean = false) {
    fm.beginTransaction().apply {
        if (replace)
            replace(container, this@set)
        else add(container, this@set)
        currentFragment = this@set
        commit()
    }
}