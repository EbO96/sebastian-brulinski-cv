package cv.brulinski.sebastian.utils.view_pager

import androidx.viewpager.widget.ViewPager
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter.Companion.Page
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter.Companion.pageMap
import cv.brulinski.sebastian.view.MyViewPager
import kotlinx.android.synthetic.main.activity_main.view.*

infix fun MyViewPager.goTo(page: Page) {
    setCurrentItem(pageMap[page] ?: 0, true)
}

fun MyViewPager.toPage(pos: Int, smooth: Boolean = true) {
    setCurrentItem(pos, smooth)
}

fun <T> ViewPager.pages(welcome: () -> T,
                        personalInfo: () -> T,
                        career: () -> T,
                        other: () -> T) = when (currentItem) {
    pageMap[Page.WELCOME_SCREEN] -> welcome()
    pageMap[Page.PERSONAL_INFO_SCREEN] -> personalInfo()
    pageMap[Page.CAREER] -> career()
    else -> other()
}

fun ViewPager.toLeft(smooth: Boolean = true) = viewPager.setCurrentItem(currentItem - 1, smooth)
fun ViewPager.toRight(smooth: Boolean = true) = viewPager.setCurrentItem(currentItem + 1, smooth)
