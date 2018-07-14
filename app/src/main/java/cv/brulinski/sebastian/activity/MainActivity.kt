package cv.brulinski.sebastian.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.MainActivityViewPagerAdapter
import cv.brulinski.sebastian.adapter.MainActivityViewPagerAdapter.Companion.Page.*
import cv.brulinski.sebastian.adapter.MainActivityViewPagerAdapter.Companion.pageMap
import cv.brulinski.sebastian.fragment.PersonalInfoFragment
import cv.brulinski.sebastian.fragment.StartFragment
import cv.brulinski.sebastian.fragment.WelcomeFragment
import cv.brulinski.sebastian.utils.goTo
import cv.brulinski.sebastian.utils.string
import kotlinx.android.synthetic.main.activity_main.*
import setBaseToolbar

class MainActivity : AppCompatActivity(),
        StartFragment.StartFragmentCallback,
        WelcomeFragment.WelcomeFragmentCallback {

    //ViewPager adapter
    private lateinit var mainActivityViewPagerAdapter: MainActivityViewPagerAdapter
    //Menu item forward button
    private var forwardButton: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setBaseToolbar(title = R.string.start.string(), enableHomeButton = true)
        setupViewPager()
        homeForwardButton(pageMap[START_SCREEN] ?: 0)
    }

    private fun setupViewPager() {
        val fragments = ArrayList<Fragment>().apply {
            add(StartFragment())
            add(WelcomeFragment())
            add(PersonalInfoFragment())
        }
        val pagesTitles = ArrayList<String>().apply {
            add(R.string.start.string().apply { asToolbarTitle() })
            add(R.string.welcome.string())
            add(R.string.personal_details.string())
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
            homeForwardButton(position)
        }
    }

    private fun String.asToolbarTitle() {
        supportActionBar?.title = this
    }
    /*
    VIEWPAGER FRAGMENTS CALLBACKS BELOW
     */

    /*
    StartFragment callbacks
     */
    override fun pdfVersionClick() {

    }

    override fun electronicVersionClick() {
        viewPager goTo WELCOME_SCREEN
    }

    override fun printCvClick() {

    }

    override fun onStartFragmentResume() {
        homeForwardButton(viewPager.currentItem)
    }

    /*
    WelcomeFragment callbacks
     */
    override fun nextButtonClicked() {
        viewPager goTo PERSONAL_INFO_SCREEN
    }

    override fun onWelcomeFragmentResume() {
        homeForwardButton(viewPager.currentItem)
    }

    private fun homeForwardButton(pagePosition: Int) = supportActionBar?.apply {
        val enabled = pagePosition != pageMap[START_SCREEN]
        setHomeButtonEnabled(enabled)
        setDisplayHomeAsUpEnabled(enabled)
        setDisplayShowHomeEnabled(enabled)
        forwardButton?.isVisible = enabled
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        forwardButton = menu?.findItem(R.id.pageForward)?.apply { isVisible = false }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                viewPager.toPrevious()
                true
            }
            R.id.pageForward -> {
                when (viewPager.currentItem) {
                    pageMap[WELCOME_SCREEN] -> {
                        nextButtonClicked()
                    }
                    pageMap[PERSONAL_INFO_SCREEN] -> {

                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        viewPager.apply {
            if (currentItem == pageMap[START_SCREEN] ?: 0)
                super.onBackPressed()
            else viewPager.toPrevious()
        }
    }
}
