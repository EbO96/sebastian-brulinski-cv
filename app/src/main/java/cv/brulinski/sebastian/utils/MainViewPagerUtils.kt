package cv.brulinski.sebastian.utils

import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter.Companion.Page
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter.Companion.pageMap
import cv.brulinski.sebastian.view.MyViewPager

infix fun MyViewPager.goTo(page: Page) {
    setCurrentItem(pageMap[page] ?: 0, true)
}

fun MyViewPager.toPage(pos: Int, smooth: Boolean = true) {
    setCurrentItem(pos, smooth)
}

fun <T> ViewPager.pages(start: () -> T,
                          welcome: () -> T,
                          personalInfo: () -> T,
                          career: () -> T,
                          other: () -> T) = when (currentItem) {
    pageMap[Page.START_SCREEN] -> start()
    pageMap[Page.WELCOME_SCREEN] -> welcome()
    pageMap[Page.PERSONAL_INFO_SCREEN] -> personalInfo()
    pageMap[Page.CAREER] -> career()
    else -> other()
}
