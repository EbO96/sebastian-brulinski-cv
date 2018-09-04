package cv.brulinski.sebastian.utils.camera

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import cv.brulinski.sebastian.R

/**
 * Class used to draw above camera preview
 */
class QrGraphicOverlay(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    private val qrPlaceholderSize = (context?.resources?.displayMetrics?.widthPixels ?: 400) / 2
    private val boundsColor = context?.let {
        ContextCompat.getColor(context, R.color.colorAccentLight)
    } ?: -1

    //Placeholder for Qr code
    private val qrCodePlaceholder = FrameLayout(context).apply {
        context?.also {
            layoutParams = RelativeLayout.LayoutParams(qrPlaceholderSize, qrPlaceholderSize).apply {
                addRule(RelativeLayout.CENTER_VERTICAL)
                addRule(RelativeLayout.CENTER_HORIZONTAL)
            }
            background = ContextCompat.getDrawable(context, R.drawable.outline_background)
        }
        id = View.generateViewId()
    }

    private val topBound = View(context).apply {
        layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT).apply {
            addRule(RelativeLayout.ABOVE, qrCodePlaceholder.id)
        }
        setBackgroundColor(boundsColor)
        id = View.generateViewId()
    }

    private val bottomBound = View(context).apply {
        layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT).apply {
            addRule(RelativeLayout.BELOW, qrCodePlaceholder.id)
        }
        setBackgroundColor(boundsColor)
        id = View.generateViewId()
    }

    private val rightBound = View(context).apply {
        layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, qrPlaceholderSize).apply {
            addRule(RelativeLayout.RIGHT_OF, qrCodePlaceholder.id)
            addRule(RelativeLayout.CENTER_VERTICAL)
        }
        setBackgroundColor(boundsColor)
        id = View.generateViewId()
    }

    private val leftBound = View(context).apply {
        layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, qrPlaceholderSize).apply {
            addRule(RelativeLayout.LEFT_OF, qrCodePlaceholder.id)
            addRule(RelativeLayout.CENTER_VERTICAL)
        }
        setBackgroundColor(boundsColor)
        id = View.generateViewId()
    }

    init {
        context?.also {
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }
        addView(qrCodePlaceholder)
        addView(leftBound)
        addView(rightBound)
        addView(topBound)
        addView(bottomBound)
    }
}