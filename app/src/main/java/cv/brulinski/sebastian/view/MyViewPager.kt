package cv.brulinski.sebastian.view

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.utils.view_pager.toPage

class MyViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    private val params = context.obtainStyledAttributes(attrs, R.styleable.MyViewPager)
    var paging = params?.getBoolean(R.styleable.MyViewPager_enablePaging, true) ?: true
    var previousPage = 0

    /**
     * returns current screen
     */
    fun toPrevious() {
        val page = currentItem - 1
        toPage(if (page < 0) 0 else page)
    }

//    override fun onTouchEvent(ev: MotionEvent): Boolean {
//        return paging && super.onTouchEvent(ev) || true
//    }

//    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
//        return paging && super.onTouchEvent(ev) || true
//    }
//
//    override fun setCurrentItem(item: Int) {
//        previousPage = currentItem
//        super.setCurrentItem(item)
//    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        previousPage = currentItem
        super.setCurrentItem(item, smoothScroll)
    }
}