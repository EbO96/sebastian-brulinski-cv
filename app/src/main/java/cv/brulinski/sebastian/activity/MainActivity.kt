package cv.brulinski.sebastian.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.os.Bundle
import android.transition.Fade
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.facebook.shimmer.ShimmerFrameLayout
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
import cv.brulinski.sebastian.fragment.SettingsFragment
import cv.brulinski.sebastian.fragment.WelcomeFragment
import cv.brulinski.sebastian.model.MyCv
import cv.brulinski.sebastian.utils.currentFragment
import cv.brulinski.sebastian.utils.set
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
import kotlinx.android.synthetic.main.fragment_welcome.*

class MainActivity : AppCompatActivity(),
        WelcomeFragment.WelcomeFragmentCallback,
        PersonalInfoFragment.PersonalInfoCallback,
        CareerFragment.CareerFragmentCallback,
        Toolbar.OnMenuItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

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
            if (currentFragment !is SettingsFragment)
                slideDrawer.apply {
                    if (!isOpen()) open() else close()
                }
            else {
                backFromSettings()
            }
        }

        val h = AnimatorSet()

        val stateListAnimator = StateListAnimator()
        val showAnimX = ObjectAnimator.ofFloat(fab, "scaleX", 0f, 1f)
        val showAnimY = ObjectAnimator.ofFloat(fab, "scaleY", 0f, 1f)
        val showAnimSet = AnimatorSet()
        showAnimSet.playTogether(showAnimX, showAnimY)

        val hideAnimX = ObjectAnimator.ofFloat(fab, "scaleX", 1f, 0f)
        val hideAnimY = ObjectAnimator.ofFloat(fab, "scaleY", 1f, 0f)
        val hideAnimSet = AnimatorSet()
        hideAnimSet.playTogether(hideAnimX, hideAnimY)

        h.playSequentially(hideAnimSet, showAnimSet)

        stateListAnimator.addState(intArrayOf(-android.R.attr.state_selected), h)
        stateListAnimator.addState(intArrayOf(android.R.attr.state_selected), h)

        fab.stateListAnimator = stateListAnimator

        bar.replaceMenu(R.menu.bottom_app_bar_menu)
        bar.setOnMenuItemClickListener(this)
        swipeRefreshLayout.setOnRefreshListener(this)

        App.startFetchingData.observe(this, Observer {
            when (it) {
                App.FetchDataStatus.START -> {
                }
                App.FetchDataStatus.END -> swipeRefreshLayout.isRefreshing = false
                App.FetchDataStatus.ERROR -> swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    /*
    Public methods
     */

    fun toPage(page: Int) = viewPager.toPage(page)

    /*
    Private methods
     */

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
            bar.menu.apply {
                setGroupVisible(R.id.callMailGroup, position != 1)
            }
        }
    }

    private fun backFromSettings(): Boolean {
        val fragment = supportFragmentManager.findFragmentById(this@MainActivity.mainContainer.id)
        return if (fragment is SettingsFragment) {
            supportFragmentManager.beginTransaction().remove(fragment).commit()
            this@MainActivity.fab.setImageState(intArrayOf(-android.R.attr.state_selected), false)
            currentFragment = null
            this@MainActivity.bar.menu.apply {
                setGroupVisible(R.id.callMailGroup, viewPager.currentItem != 1)
                setGroupVisible(R.id.settingsGroup, true)
            }
            true
        } else false

    }

    private fun goToSettings() {
        SettingsFragment().apply {
            val fade = Fade()
            enterTransition = fade
            exitTransition = fade
            this@apply.set(supportFragmentManager, this@MainActivity.mainContainer.id)
            this@MainActivity.fab.setImageState(intArrayOf(android.R.attr.state_selected), false)
            this@MainActivity.bar.menu.apply {
                setGroupVisible(R.id.callMailGroup, false)
                setGroupVisible(R.id.settingsGroup, false)
            }
        }
    }

    /*
    Override methods
     */

    override fun onRefresh() {
        mainViewModel?.refreshAll()
    }

    override fun onMenuItemClick(item: MenuItem?) = when (item?.itemId) {
        R.id.settings -> {
            goToSettings()
            true
        }
        else -> false
    }

    override fun onBackPressed() {
        viewPager.apply {
            when {
                backFromSettings() -> {
                }
                this@MainActivity.slideDrawer?.isOpen() != false -> slideDrawer.close()
                currentItem == pageMap[WELCOME_SCREEN] ?: 0 -> super.onBackPressed()
                else -> toLeft()
            }
        }
    }
}
