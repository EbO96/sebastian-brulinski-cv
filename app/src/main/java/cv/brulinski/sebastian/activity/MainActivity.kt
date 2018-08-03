package cv.brulinski.sebastian.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomappbar.BottomAppBar
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter
import cv.brulinski.sebastian.adapter.view_pager.MainActivityViewPagerAdapter.Companion.Page.WELCOME_SCREEN
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
import cv.brulinski.sebastian.utils.string
import cv.brulinski.sebastian.utils.view_pager.MyMainViewPager
import cv.brulinski.sebastian.utils.view_pager.ViewPagerStates
import cv.brulinski.sebastian.utils.view_pager.toLeft
import cv.brulinski.sebastian.utils.view_pager.toPage
import cv.brulinski.sebastian.view.SlideDrawer
import cv.brulinski.sebastian.view_model.MainViewModel
import inflate
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*

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
    //Bottom app bar behavior
    private lateinit var bottomAppBarBehavior: BottomAppBar.Behavior

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViewPager()

        slideDrawer({
            drawerTitle = R.string.table_of_contents.string()
        }, {
            val items = arrayListOf(SlideDrawer.DrawerMenuItem(R.string.introduction.string()),
                    SlideDrawer.DrawerMenuItem(R.string.personal_details.string()),
                    SlideDrawer.DrawerMenuItem(R.string.career.string()),
                    SlideDrawer.DrawerMenuItem(R.string.languages.string()),
                    SlideDrawer.DrawerMenuItem(R.string.skills.string()))
            setMenu(items)
            setMenuItemClickListener(object : SlideDrawer.MenuItemsClickListener {
                override fun onClick(position: Int, drawerMenuItem: SlideDrawer.DrawerMenuItem) {
                    viewPager.toPage(position)
                }
            })
        })

        fab.setOnClickListener {
            slideDrawer.apply {
                if (!isOpen()) open() else close()
            }
        }

        bar.replaceMenu(R.menu.bottom_app_bar_menu)

        App.startFetchingData.observe(this, Observer { status ->
            when (status) {
                App.FetchDataStatus.START -> loadingScreen.show()
                App.FetchDataStatus.END, App.FetchDataStatus.ERROR, null -> loadingScreen.hide()
            }
        })
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

    private fun viewPagerPageListener() = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {
        }
    }

    /*
    Loading screen show/hide methods
     */
    private fun View.show() {
//        mainRootContainer?.apply {
//            if (indexOfChild(this@show) == -1 && settings.firstLaunch) {
//                addView(this@show)
//            }
//        }
    }

    private fun View.hide() {
        1000L.delay {
            //            mainRootContainer?.apply {
//                if (indexOfChild(this@hide) != -1) {
//                    removeView(this@hide)
//                }
//            }
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
                slideDrawer.open()
                true
            }
            R.id.refreshContent -> {
                mainViewModel?.refreshAll()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun toPage(page: Int) = viewPager.toPage(page)

    override fun onBackPressed() {
        viewPager.apply {
            if (this@MainActivity.slideDrawer?.isOpen() != false) slideDrawer.close()
            else if (currentItem == pageMap[WELCOME_SCREEN] ?: 0) super.onBackPressed()
            else toLeft()
        }
    }
}
