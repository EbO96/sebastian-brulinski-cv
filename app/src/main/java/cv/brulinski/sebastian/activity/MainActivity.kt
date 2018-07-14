package cv.brulinski.sebastian.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.MainActivityViewPagerAdapter
import cv.brulinski.sebastian.adapter.MainActivityViewPagerAdapter.Companion.Page.START_SCREEN
import cv.brulinski.sebastian.adapter.MainActivityViewPagerAdapter.Companion.Page.WELCOME_SCREEN
import cv.brulinski.sebastian.adapter.MainActivityViewPagerAdapter.Companion.pageMap
import cv.brulinski.sebastian.fragment.StartFragment
import cv.brulinski.sebastian.fragment.WelcomeFragment
import cv.brulinski.sebastian.utils.goTo
import cv.brulinski.sebastian.utils.string
import kotlinx.android.synthetic.main.activity_main.*
import setBaseToolbar

class MainActivity : AppCompatActivity(),
        StartFragment.StartFragmentCallback {

    //ViewPager adapter
    private lateinit var mainActivityViewPagerAdapter: MainActivityViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setBaseToolbar(title = R.string.start.string(), enableHomeButton = true)
        homeButton(false)

        setupViewPager()
    }

    private fun setupViewPager() {
        val fragments = ArrayList<Fragment>().apply {
            add(StartFragment())
            add(WelcomeFragment())
        }
        val pagesTitles = ArrayList<String>().apply {
            add(R.string.start.string().apply { asToolbarTitle() })
            add(R.string.welcome.string())
        }
        viewPager.apply {
            mainActivityViewPagerAdapter = MainActivityViewPagerAdapter(fragments, pagesTitles, supportFragmentManager).apply { adapter = this }
            offscreenPageLimit = fragments.size
            addOnPageChangeListener(viewPagerPageListener())
        }
    }

    private fun viewPagerPageListener() = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            mainActivityViewPagerAdapter.getPageTitle(position).asToolbarTitle()
            viewPager.paging = false
            homeButton(!(position == pageMap[START_SCREEN] ?: 0))
        }
    }

    private fun String.asToolbarTitle() {
        supportActionBar?.title = this
    }

    /*
    VIEWPAGER FRAGMENTS CALLBACKS BELOW
     */

    /*
    Start Fragment callbacks
     */

    override fun pdfVersionClick() {

    }

    override fun electronicVersionClick() {
        homeButton(true)
        viewPager goTo WELCOME_SCREEN
    }

    override fun printCvClick() {

    }

    private fun homeButton(enabled: Boolean = true) = supportActionBar?.apply {
        setHomeButtonEnabled(enabled)
        setDisplayHomeAsUpEnabled(enabled)
        setDisplayShowHomeEnabled(enabled)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                viewPager.toPrevious()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        viewPager.apply {
            if (currentItem == pageMap[START_SCREEN] ?: 0)
            super.onBackPressed()
            else toPrevious()
        }
    }
}
