package cv.brulinski.sebastian.activity

import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter.Companion.Page.*
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter.Companion.pageMap
import cv.brulinski.sebastian.dependency_injection.app.App
import cv.brulinski.sebastian.dependency_injection.component.DaggerPagesComponent
import cv.brulinski.sebastian.dependency_injection.component.PagesComponent
import cv.brulinski.sebastian.dependency_injection.module.PagesModule
import cv.brulinski.sebastian.fragment.CareerFragment
import cv.brulinski.sebastian.fragment.PersonalInfoFragment
import cv.brulinski.sebastian.fragment.WelcomeFragment
import cv.brulinski.sebastian.model.MyCv
import cv.brulinski.sebastian.utils.delay
import cv.brulinski.sebastian.utils.navigation_drawer.close
import cv.brulinski.sebastian.utils.settings
import cv.brulinski.sebastian.utils.string
import cv.brulinski.sebastian.utils.view_pager.*
import cv.brulinski.sebastian.view_model.MainViewModel
import inflate
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.activity_main_drawer_header.view.*
import setBaseToolbar

class MainActivity : AppCompatActivity(),
        WelcomeFragment.WelcomeFragmentCallback,
        PersonalInfoFragment.PersonalInfoCallback,
        CareerFragment.CareerFragmentCallback {

    //Loading screen - displayed during first fetching
    private val loadingScreen by lazy { R.layout.data_loading_screen.inflate(this) }
    //ViewPager adapter
    private var mainActivityViewPagerAdapter: MainActivityViewPagerAdapter? = null
    //ViewModel
    private var mainViewModel: MainViewModel? = null
    //MyCv
    private var myCv: LiveData<MyCv>? = null
    //Dagger ViewPager pages component
    private lateinit var pagesComponent: PagesComponent
    //Menu items
    private var refreshMenuItem: MenuItem? = null
    //Main ViewPager
    private var myMainViewPager: MyMainViewPager? = null
    //Flags
    private var pagesProgress = 0
    //Number of pages
    private var numberOfPages = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setBaseToolbar(title = R.string.start.string(), enableHomeButton = true)
        setupViewPager()
        mainDrawerLayout.setupNavigationDrawer()

        mainNavigationView.getHeaderView(0)?.apply {
            fetchGraphicsSwitch.isChecked = settings.fetchGraphics ?: true
            fetchGraphicsSwitch.setOnCheckedChangeListener { _, checked ->
                settings.fetchGraphics = checked
            }
        }

        App.startFetchingData.observe(this, Observer { status ->
            when (status) {
                App.FetchDataStatus.START -> loadingScreen.show()
                App.FetchDataStatus.END, App.FetchDataStatus.ERROR, null -> loadingScreen.hide()
            }
        })
    }

    private fun DrawerLayout.setupNavigationDrawer() {
        addDrawerListener(drawerListener())
        mainNavigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.preambuleItem -> {
                    mainDrawerLayout.close {
                        viewPager.goTo(WELCOME_SCREEN)
                    }
                }
                R.id.personalDetailsItem -> {
                    mainDrawerLayout.close {
                        viewPager.goTo(PERSONAL_INFO_SCREEN)
                    }
                }
                R.id.careerItem -> {
                    mainDrawerLayout.close {
                        viewPager.goTo(CAREER)
                    }
                }
                R.id.languagesItem -> {
                    mainDrawerLayout.close {
                        viewPager.goTo(LANGUAGES)
                    }
                }
                R.id.skillsItem -> {
                    mainDrawerLayout.close {
                        viewPager.goTo(SKILLS)
                    }
                }
            }
            true
        }
    }

    private fun setupViewPager() {
        myMainViewPager = MyMainViewPager(supportFragmentManager, viewPager, viewPagerPageListener()).setup()
        mainActivityViewPagerAdapter = myMainViewPager?.mainActivityViewPagerAdapter
        myMainViewPager?.observeForever { it ->
            it?.let {
                when (it) {
                    ViewPagerStates.VIEW_PAGER_PAGES_CREATED -> {
                        numberOfPages = myMainViewPager?.getNumberOfPages() ?: 0
                        mainActivityViewPagerAdapter?.let { adapter ->
                            pagesComponent = DaggerPagesComponent.builder().pagesModule(PagesModule(adapter, viewPager)).build()
                            pagesComponent.inject(this)
                            //ViewModel
                            mainViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(MainViewModel::class.java)
                            myCv = mainViewModel?.myCv
                            myCv?.observe(this, Observer {
                                App.startFetchingData.value = App.FetchDataStatus.END
                                loadingScreen.hide()
                                it?.apply {
                                    welcome?.let { welcome ->
                                        pagesComponent.getWelcomeScreen().update(welcome)
                                    }
                                    personalInfo?.let { personalInfo ->
                                        pagesComponent.getPersonalInfoScreen().update(personalInfo)
                                    }
                                    career?.let { career ->
                                        pagesComponent.getCareerScreen().update(career)
                                    }
                                    languages?.let { languages ->
                                        pagesComponent.getLanguagesScreen().update(languages)
                                    }
                                    skills?.let { skills ->
                                        pagesComponent.getSkillsScreen().update(skills)
                                    }
                                }
                            })
                        }
                    }
                    ViewPagerStates.VIEW_PAGER_PAGES_DESTROYED -> {
                    }
                }
            }
        }
    }

    var o = 0
    private fun viewPagerPageListener() = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            val progress = 100 / (numberOfPages) * (position + 1)
            contentProgressBar.progress = progress
        }

        override fun onPageSelected(position: Int) {
            mainActivityViewPagerAdapter?.getPageTitle(position)?.asToolbarTitle()
        }
    }

    private fun drawerListener() = object : DrawerLayout.DrawerListener {
        override fun onDrawerStateChanged(newState: Int) {
        }

        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            val slideX = drawerView.width * slideOffset
            mainContent.translationX = slideX
        }

        override fun onDrawerClosed(drawerView: View) {
        }

        override fun onDrawerOpened(drawerView: View) {
        }
    }

    private fun String.asToolbarTitle() {
        supportActionBar?.title = this
    }

    /*
    Loading screen show/hide methods
     */
    private fun View.show() {
        mainRootContainer?.apply {
            if (indexOfChild(this@show) == -1 && settings.firstLaunch) {
                addView(this@show)
                this@MainActivity.mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
    }

    private fun View.hide() {
        1000L.delay {
            mainRootContainer?.apply {
                if (indexOfChild(this@hide) != -1) {
                    removeView(this@hide)
                    this@MainActivity.mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
            }
        }
    }
    /*
    VIEWPAGER FRAGMENTS CALLBACKS BELOW
     */

    /*
    PersonalInfo callbacks
     */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        refreshMenuItem = menu?.findItem(R.id.refreshContent)
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                viewPager.toLeft()
                true
            }
            R.id.pageForward -> {
                viewPager.toRight()
                true
            }
            R.id.refreshContent -> {
                mainViewModel?.refreshAll()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        viewPager.apply {
            when {
                this@MainActivity.mainDrawerLayout.isDrawerOpen(Gravity.START) -> this@MainActivity.mainDrawerLayout.closeDrawer(Gravity.START)
                currentItem == pageMap[WELCOME_SCREEN] ?: 0 -> super.onBackPressed()
                else -> toLeft()
            }
        }
    }
}
