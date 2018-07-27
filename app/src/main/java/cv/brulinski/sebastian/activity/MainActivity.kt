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
import cv.brulinski.sebastian.dependency_injection.component.DaggerPagesComponent
import cv.brulinski.sebastian.dependency_injection.component.PagesComponent
import cv.brulinski.sebastian.dependency_injection.module.PagesModule
import cv.brulinski.sebastian.fragment.CareerFragment
import cv.brulinski.sebastian.fragment.PersonalInfoFragment
import cv.brulinski.sebastian.fragment.WelcomeFragment
import cv.brulinski.sebastian.model.MyCv
import cv.brulinski.sebastian.utils.navigation_drawer.close
import cv.brulinski.sebastian.utils.settings
import cv.brulinski.sebastian.utils.string
import cv.brulinski.sebastian.utils.toast
import cv.brulinski.sebastian.utils.view_pager.*
import cv.brulinski.sebastian.view_model.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.activity_main_drawer_header.view.*
import setBaseToolbar

class MainActivity : AppCompatActivity(),
        WelcomeFragment.WelcomeFragmentCallback,
        PersonalInfoFragment.PersonalInfoCallback,
        CareerFragment.CareerFragmentCallback {

    //ViewPager adapter
    private var mainActivityViewPagerAdapter: MainActivityViewPagerAdapter? = null
    //ViewModel
    private var mainViewModel: MainViewModel? = null
    //MyCv
    private var myCv: LiveData<MyCv>? = null
    //Dagger ViewPager pages component
    private lateinit var pagesComponent: PagesComponent

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
            }
            true
        }
    }

    private fun setupViewPager() {
        val myMainViewPager = MyMainViewPager(supportFragmentManager, viewPager, viewPagerPageListener()).setup()
        mainActivityViewPagerAdapter = myMainViewPager.mainActivityViewPagerAdapter
        myMainViewPager.observeForever { it ->
            it?.let {
                when (it) {
                    ViewPagerStates.VIEW_PAGER_PAGES_CREATED -> {
                        mainActivityViewPagerAdapter?.let { adapter ->
                            pagesComponent = DaggerPagesComponent.builder().pagesModule(PagesModule(adapter, viewPager)).build()
                            pagesComponent.inject(this)
                            //ViewModel
                            mainViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(MainViewModel::class.java)
                            myCv = mainViewModel?.myCv
                            myCv?.observe(this, Observer {
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

    private fun viewPagerPageListener() = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
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
    VIEWPAGER FRAGMENTS CALLBACKS BELOW
     */

    /*
    WelcomeFragment callbacks
     */

    /*
    PersonalInfo callbacks
     */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
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
                "${getString(R.string.refreshing)}...".toast()
                mainViewModel?.refreshAll()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onBackPressed() {
        viewPager.apply {
            when {
                mainDrawerLayout.isDrawerOpen(Gravity.START) -> mainDrawerLayout.closeDrawer(Gravity.START)
                currentItem == pageMap[WELCOME_SCREEN] ?: 0 -> super.onBackPressed()
                else -> toLeft()
            }
        }
    }
}
