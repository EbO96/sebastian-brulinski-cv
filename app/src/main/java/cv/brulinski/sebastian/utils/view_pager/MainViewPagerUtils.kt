package cv.brulinski.sebastian.utils.view_pager

import androidx.viewpager.widget.ViewPager
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter.Companion.Page
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter.Companion.pageMap
import cv.brulinski.sebastian.view.MyViewPager

fun ViewPager.goTo(page: Page, smooth: Boolean = true) {
    setCurrentItem(pageMap[page] ?: 0, smooth)
}

fun ViewPager.toPage(pos: Int, smooth: Boolean = true) {
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

fun ViewPager.toLeft(smooth: Boolean = true) = setCurrentItem(currentItem - 1, smooth)
fun ViewPager.toRight(smooth: Boolean = true) = setCurrentItem(currentItem + 1, smooth)
