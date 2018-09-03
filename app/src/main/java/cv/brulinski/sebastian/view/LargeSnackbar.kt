package cv.brulinski.sebastian.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.annotation.ColorInt
import androidx.coordinatorlayout.widget.CoordinatorLayout
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.utils.color
import cv.brulinski.sebastian.utils.ctx
import cv.brulinski.sebastian.utils.delay
import gone
import inflate
import kotlinx.android.synthetic.main.snackbar_large.view.*
import removeViewSafe
import visible

class LargeSnackbar private constructor() {

    object Instance {
        val instance by lazy { LargeSnackbar() }
    }

    companion object {
        fun getInstance() = Instance.instance
    }

    enum class Duration { LONG,
        SHORT
    }

    private var root: ViewGroup? = null
    private var isOnScreen = false
    private val durations = mapOf(Duration.LONG to 4500L, Duration.SHORT to 2500L)
    private val snackbar: View = R.layout.snackbar_large.inflate()
    private val snackRoot: ViewGroup = snackbar.snackRoot
    private val lineOne = snackbar.messageLineOne
    private val lineTwo = snackbar.messageLineTwo
    private val actionButton = snackbar.actionButton
    @ColorInt
    private var actionButtonColor: Int? = null
    private val actionButtonColorDef = R.color.colorAccent.color()
    private var backgroundColor: Int? = null
    private var backgroundColorDef = R.color.snackbarColor.color()
    private var textTitleColor: Int? = null
    private var textTitleColorDef = R.color.snackbarTextColor.color()
    private var textMessageColor: Int? = null
    private var textMessageColorDef = R.color.snackbarTextColorLight.color()
    var upperTitle = true
    private val showAnimation = ScaleAnimation(0f,
            1f,
            0f,
            1f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f).apply {
        fillBefore = true
        duration = 200
    }

    private val hideAnimation = ObjectAnimator.ofFloat(snackbar, "translationX", 0f, (ctx.resources.displayMetrics.widthPixels + 16).toFloat()).apply {
        duration = 200
        addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                snackbar.apply {
                    gone()
                    translationX = 0f
                    root?.removeViewSafe(this)

                }
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }
        })
    }

    fun setTextTitleColor(@ColorInt color: Int) {
        textTitleColor = color
    }

    fun setTextMessageColor(@ColorInt color: Int) {
        textMessageColor = color
    }

    fun setBackgroundColor(@ColorInt color: Int) {
        backgroundColor = color
    }

    fun setButtonColor(@ColorInt color: Int) {
        actionButtonColor = color
    }

    fun show(rootView: ViewGroup, view: View, lineOneText: String = "", lineTwoText: String = "", duration: Duration, actionText: String, bottomMargin: Int = 196, actionClick: (View) -> Unit) {
        root = rootView

        if (!isOnScreen) {

            lineOne?.setTextColor(textTitleColor ?: textTitleColorDef)
            lineTwo?.setTextColor(textMessageColor ?: textMessageColorDef)
            actionButton?.setTextColor(actionButtonColor ?: actionButtonColorDef)
            snackRoot.setBackgroundColor(backgroundColor ?: backgroundColorDef)

            (rootView as? CoordinatorLayout)?.apply {
                lineOne.text = lineOneText.let { if (upperTitle) it.toUpperCase() else it }
                lineTwo.text = lineTwoText
                this@LargeSnackbar.actionButton?.apply {
                    text = actionText.toUpperCase()
                    setOnClickListener {
                        hideSnackbar()
                        actionClick(it)
                    }
                }
                snackbar.layoutParams = CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT).apply {
                    anchorGravity = Gravity.TOP
                    setMargins(16, 16, 16, bottomMargin)
                    anchorId = view.id
                }

                try {
                    addView(snackbar)
                    showSnackbar()
                    durations[duration]?.delay {
                        hideSnackbar()
                    }
                } catch (e: Exception) {

                }
            }

            textTitleColor = null
            textMessageColor = null
            backgroundColor = null
            actionButtonColor = null
        }
    }


    /*
    Private methods
     */
    private fun hideSnackbar() {
        if (isOnScreen) {
            isOnScreen = false
            hideAnimation.start()
        }
    }

    private fun showSnackbar() {
        if (!isOnScreen) {
            isOnScreen = true
            snackbar.visible()
            snackbar.startAnimation(showAnimation)
        }
    }
}