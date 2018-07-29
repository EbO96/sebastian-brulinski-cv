package cv.brulinski.sebastian.utils.view_pager

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.viewpager.widget.ViewPager
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter
import cv.brulinski.sebastian.fragment.*
import cv.brulinski.sebastian.interfaces.ViewPagerUtilsFragmentCreatedListener
import cv.brulinski.sebastian.utils.string

class MyMainViewPager(private val fragmentManager: FragmentManager,
                      private val viewPager: ViewPager,
                      private val onPageChangeListener: ViewPager.OnPageChangeListener? = null) : ViewPagerUtilsFragmentCreatedListener,
        LiveData<ViewPagerStates>() {

    var mainActivityViewPagerAdapter: MainActivityViewPagerAdapter? = null
    private var numberOfPages = 0
    private var numberOfCreatedFragments = 0

    override fun onFragmentCreated() {
        numberOfCreatedFragments++
        if (numberOfCreatedFragments == numberOfPages) {
            value = ViewPagerStates.VIEW_PAGER_PAGES_CREATED
        }
    }

    override fun onFragmentDestroyed() {
        numberOfCreatedFragments--
        if (numberOfCreatedFragments == 0)
            value = ViewPagerStates.VIEW_PAGER_PAGES_DESTROYED
    }

    fun setup(): MyMainViewPager {
        val fragments = arrayListOf(
                WelcomeFragment.newInstance(this),
                PersonalInfoFragment.newInstance(this),
                CareerFragment.newInstance(this),
                LanguagesFragment.newInstance(this),
                SkillsFragment.newInstance(this)
        )
        numberOfPages = fragments.size
        val titles = arrayListOf("",
                R.string.personal_details.string(),
                R.string.career.string(),
                R.string.languages.string(),
                R.string.skills.string())

        viewPager.apply {
            mainActivityViewPagerAdapter = MainActivityViewPagerAdapter(fragments, titles, fragmentManager).apply {
                adapter = this
            }
            onPageChangeListener?.let {
                addOnPageChangeListener(it)
            }
            offscreenPageLimit = numberOfPages
        }
        return this
    }
}