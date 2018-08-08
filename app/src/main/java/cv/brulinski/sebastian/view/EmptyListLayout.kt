package cv.brulinski.sebastian.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import cv.brulinski.sebastian.R
import inflate
import kotlinx.android.synthetic.main.empty_list.view.*

class EmptyListLayout(context: Context?, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    init {
        val layout = R.layout.empty_list.inflate(context)
        val a = context?.obtainStyledAttributes(attrs, R.styleable.EmptyListLayout)
        val image = a?.getDrawable(R.styleable.EmptyListLayout_image)
        val text = a?.getText(R.styleable.EmptyListLayout_text)
        layout.imageImageView.setImageDrawable(image)
        layout.textTextView.text = text
        a?.recycle()
        removeAllViews()
        addView(layout)
    }
}