package cv.brulinski.sebastian.interfaces

import android.view.View

interface OnItemClickListener {
    fun onClick(item: Any, position: Int, v: View)
}