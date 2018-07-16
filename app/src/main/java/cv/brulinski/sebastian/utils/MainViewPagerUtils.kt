package cv.brulinski.sebastian.utils

import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter.Companion.Page
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter.Companion.pageMap
import cv.brulinski.sebastian.view.MyViewPager

infix fun MyViewPager.goTo(page: Page) {
    paging = true
    setCurrentItem(pageMap[page] ?: 0, true)
}

fun MyViewPager.toPage(pos: Int, smooth: Boolean = true) {
    paging = true
    setCurrentItem(pos, smooth)
}