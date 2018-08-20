package cv.brulinski.sebastian.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.TextView
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.utils.drawable

class FirstLetterTextView(context: Context?, attrs: AttributeSet?) : TextView(context, attrs) {

    init {
        background = null
    }

    override fun setBackground(background: Drawable?) {
        super.setBackground(R.drawable.circle_shape.drawable())
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text?.let { if (it.isNotEmpty()) it.substring(0, 1) else "" }, type)
    }
}