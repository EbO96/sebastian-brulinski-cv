package cv.brulinski.sebastian.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter.Companion.Page.*
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter.Companion.pageMap
import cv.brulinski.sebastian.fragment.CareerFragment
import cv.brulinski.sebastian.fragment.PersonalInfoFragment
import cv.brulinski.sebastian.fragment.StartFragment
import cv.brulinski.sebastian.fragment.WelcomeFragment
import cv.brulinski.sebastian.model.JobExperience
import cv.brulinski.sebastian.utils.delay
import cv.brulinski.sebastian.utils.goTo
import cv.brulinski.sebastian.utils.pages
import cv.brulinski.sebastian.utils.string
import cv.brulinski.sebastian.view_model.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import setBaseToolbar

class MainActivity : AppCompatActivity(),
        StartFragment.StartFragmentCallback,
        WelcomeFragment.WelcomeFragmentCallback,
        PersonalInfoFragment.PersonalInfoCallback,
        SwipeRefreshLayout.OnRefreshListener,
        CareerFragment.CareerFragmentCallback {

    //ViewPager adapter
    private lateinit var mainActivityViewPagerAdapter: MainActivityViewPagerAdapter
    //Menu item forward button
    private var forwardButton: MenuItem? = null
    //ViewModel
    private var mainViewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setBaseToolbar(title = R.string.start.string(), enableHomeButton = true)
        setupViewPager()
        homeForwardButton(pageMap[START_SCREEN] ?: 0)
        //ViewModel
        mainViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(MainViewModel::class.java)
        refreshLayout.setOnRefreshListener(this)
        lockUnlockRefreshLayout()
    }

    private fun setupViewPager() {
        val fragments = ArrayList<Fragment>().apply {
            add(StartFragment())
            add(WelcomeFragment())
            add(PersonalInfoFragment())
            add(CareerFragment())
        }
        val pagesTitles = ArrayList<String>().apply {
            add(R.string.start.string().apply { asToolbarTitle() })
            add(R.string.welcome.string())
            add(R.string.personal_details.string())
            add(R.string.career.string())
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
            lockUnlockRefreshLayout()
        }
    }

    private fun lockUnlockRefreshLayout() {
        refreshLayout.isEnabled = viewPager.pages({
            false
        }, {
            true
        }, {
            true
        }, {
            true
        }, {
            false
        })
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
    override fun goToPersonalInfoScreen() {
        viewPager goTo PERSONAL_INFO_SCREEN
    }

    override fun onWelcomeFragmentResume() {
        homeForwardButton(viewPager.currentItem)
    }

    override fun getWelcome() = mainViewModel?.welcome

    override fun refreshWelcome() {
        mainViewModel?.refreshWelcome()
    }

    /*
    PersonalInfo callbacks
     */
    override fun getPersonalInfo() = mainViewModel?.personalInfo

    override fun goToCareerScreen() {
        viewPager goTo CAREER
    }

    override fun onRefreshed() {
        200L.delay {
            refreshLayout.isRefreshing = false
        }
    }

    /*
    CareerFragment callbacks
     */
    override fun getEducation() = mainViewModel?.education

    override fun getJobExperience() = mainViewModel?.jobExperience

    override fun refreshEducation() {
        mainViewModel?.refreshEducation()
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
                        goToPersonalInfoScreen()
                    }
                    pageMap[PERSONAL_INFO_SCREEN] -> {
                        goToCareerScreen()
                    }
                    pageMap[CAREER] -> {

                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRefresh() {
        viewPager.pages({
            //START
            refreshLayout.isRefreshing = false
        }, {
            //WELCOME
            mainViewModel?.refreshWelcome()
        }, {
            //PERSONAL INFO
            mainViewModel?.refreshPersonalInfo()
        }, {
            //CAREER
            mainViewModel?.refreshEducation()
        }, {
            //ELSE
            refreshLayout.isRefreshing = false
        })
    }

    override fun onResume() {
        super.onResume()
        homeForwardButton(viewPager.currentItem)
    }

    override fun onBackPressed() {
        viewPager.apply {
            if (currentItem == pageMap[START_SCREEN] ?: 0)
                super.onBackPressed()
            else viewPager.toPrevious()
        }
    }
}
