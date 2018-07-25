package cv.brulinski.sebastian.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.utils.iterate
import cv.brulinski.sebastian.utils.setAlpha

class DotLevelView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private val defColor1 = Color.parseColor("#FF0000")
    private val defColor2 = Color.parseColor("#0000FF")
    private val a = context?.obtainStyledAttributes(attrs, R.styleable.DotLevelView)
    private val dotSize = a?.getDimensionPixelSize(R.styleable.DotLevelView_dotSize, 0) ?: 0
    private val dotMargin = a?.getDimensionPixelSize(R.styleable.DotLevelView_dotMargin, 0) ?: 0
    private val levelDotColor = a?.getColor(R.styleable.DotLevelView_levelDotColor, defColor1)
            ?: defColor1
    private val rangeDotColor = a?.getColor(R.styleable.DotLevelView_rangeDotColor, defColor2)
            ?: defColor2
    private val level = a?.getInt(R.styleable.DotLevelView_level, 0) ?: 0
    private val range = a?.getInt(R.styleable.DotLevelView_range, 0) ?: 0
    private var levelDot: GradientDrawable? = null
    private var rangeDot: GradientDrawable? = null
    private val viewLp = LinearLayout.LayoutParams(dotSize,
            dotSize).apply {
        setMargins(dotMargin, dotMargin, dotMargin, dotMargin)
    }

    init {
        orientation = HORIZONTAL

        levelDot = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(levelDotColor)
        }

        rangeDot = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(rangeDotColor.let { it.setAlpha(128) ?: it })
        }

        create(level, range)
    }

    fun create(level: Int, range: Int) {
        if (childCount > 0)
            removeAllViews()

        var l = level
        var r = range
        if (level > range) {
            l = r
            r = level
        }
        r = Math.abs(r)
        l = Math.abs(l)
        r.iterate {
            val view = View(context)
            view.layoutParams = viewLp
            view.background = if ((l + 1) <= it) rangeDot
            else levelDot
            addView(view)
        }
        invalidate()
    }
}