package cv.brulinski.sebastian.adapter.view_pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MainActivityViewPagerAdapter(private val fragments: ArrayList<Fragment>,
                                   private val pagesTitles: ArrayList<String>,
                                   fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    companion object {
        enum class Page {
            START_SCREEN,
            WELCOME_SCREEN,
            PERSONAL_INFO_SCREEN,
            CAREER
        }

        val pageMap = mapOf(
                Page.START_SCREEN to 0,
                Page.WELCOME_SCREEN to 1,
                Page.PERSONAL_INFO_SCREEN to 2,
                Page.CAREER to 3)
    }

    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = fragments.size

    override fun getPageTitle(position: Int) = pagesTitles[position]
}