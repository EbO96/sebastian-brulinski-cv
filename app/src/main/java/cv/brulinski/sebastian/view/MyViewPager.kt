package cv.brulinski.sebastian.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.utils.toPage

class MyViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    private val params = context.obtainStyledAttributes(attrs, R.styleable.MyViewPager)
    var paging = params?.getBoolean(R.styleable.MyViewPager_enablePaging, true) ?: true
    var previousPage = 0

    /**
     * returns current screen
     */
    fun toPrevious() = previousPage.apply {
        toPage(this)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return paging && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return paging && super.onTouchEvent(ev)
    }

    override fun setCurrentItem(item: Int) {
        previousPage = currentItem
        super.setCurrentItem(item)
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        previousPage = currentItem
        super.setCurrentItem(item, smoothScroll)
    }
}