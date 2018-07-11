package cv.brulinski.sebastian.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MainActivityViewPagerAdapter(private val fragments: ArrayList<Fragment>,
                                   private val pagesTitles: ArrayList<String>,
                                   fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = fragments.size

    override fun getPageTitle(position: Int) = pagesTitles[position]
}