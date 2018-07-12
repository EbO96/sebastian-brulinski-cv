package cv.brulinski.sebastian.view

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.widget.Button
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.utils.color
import cv.brulinski.sebastian.utils.drawable

class MyMaterialButton(context: Context?, attrs: AttributeSet?) : Button(context, attrs) {

    private val params = context?.obtainStyledAttributes(attrs, R.styleable.MyMaterialButton)
    private val defaultColor = R.color.colorAccent.color()
    private val buttonColor = params?.getColor(R.styleable.MyMaterialButton_buttonColor, defaultColor)
            ?: defaultColor
    private val flat = params?.getBoolean(R.styleable.MyMaterialButton_flat, false) ?: false

    init {
        if (flat)
            stateListAnimator = null
        R.drawable.material_button_background.drawable()?.apply {
            background = this
            background.setColorFilter(buttonColor, PorterDuff.Mode.SRC_IN)
        }
    }
}